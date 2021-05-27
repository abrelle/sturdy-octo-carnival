package Processes;

import Components.Memory;
import Components.SupervisorMemory;
import Resources.Resource;
import Resources.ResourceDistributor;
import Tools.Word;

import java.util.ArrayList;

import static Processes.MainProc.State.TASK_CREATED;
import static Processes.ProcessEnum.JOB_TO_SWAP_PRIORITY;
import static Processes.ProcessEnum.Name.JOB_TO_SWAP;
import static Processes.ProcessEnum.VIRTUAL_MACHINE_PRIORITY;
import static Resources.ResourceEnum.Name.EXTERNAL_MEMORY;
import static Resources.ResourceEnum.Name.SUPERVISOR_MEMORY;
import static Resources.ResourceEnum.Name.TASK_COMPLETED;
import static Resources.ResourceEnum.Name.TASK_IN_DRUM;
import static Resources.ResourceEnum.Name.TASK_PARAMETERS_IN_SUPERVISOR_MEMORY;
import static Tools.Constants.CODE_SEGMENT;
import static Tools.Constants.DATA_SEGMENT;
import static Tools.Word.WORD_TYPE.SYMBOLIC;

public class JobToSwap extends ProcessInterface {

    private static int TASKID = VIRTUAL_MACHINE_PRIORITY;
    private final RealMachine realMachine;

    public JobToSwap(RealMachine father, ProcessPlanner processPlanner, ResourceDistributor distributor) {
        super(father, ProcessEnum.State.BLOCKED, JOB_TO_SWAP_PRIORITY, JOB_TO_SWAP, processPlanner, distributor);
        this.realMachine = father;
    }

    @Override
    public void executeTask() throws Exception {
        super.executeTask();

        System.out.println("\n------------JOB TO SWAP------------\n");

        switch (IC) {
            case 0:
                IC++;
                resourceDistributor.ask(TASK_PARAMETERS_IN_SUPERVISOR_MEMORY, this);
                break;
            case 1:
                IC++;
                Resource task = resourceDistributor.get(TASK_PARAMETERS_IN_SUPERVISOR_MEMORY);
                SupervisorMemory supervisorMemory = (SupervisorMemory) resourceDistributor.get(SUPERVISOR_MEMORY);
                Memory external = (Memory) resourceDistributor.get(EXTERNAL_MEMORY);
                String fileName = (String) task.get(0);

                if (external.hasFreeSpace()) {
                    IC = 2;
                    int externalMemoryBegin = external.getFreeSpaceBeginAddress();
                    uploadTaskToExternalMemory(fileName, externalMemoryBegin, supervisorMemory);
                    resourceDistributor.disengage(TASK_IN_DRUM, TASK_CREATED, fileName, TASKID++, externalMemoryBegin);
                } else {
                    IC = 0;
                    resourceDistributor.disengage(TASK_COMPLETED, "Truksta atminties");
                }
                break;
            case 2:
                IC = 0;
                resourceDistributor.disengage(TASK_COMPLETED, "Užduotis užkrauta sėkmingai");
                break;
        }

    }

    public void uploadTaskToExternalMemory(String task, int externalMemoryBegin, SupervisorMemory supervisorMemory) {

        int dataSegBegin = DATA_SEGMENT + (externalMemoryBegin * 256);
        int codeSegBegin = CODE_SEGMENT + (externalMemoryBegin * 256);

        System.out.println("externalMemoryBegin  ---------------> " + externalMemoryBegin );
        System.out.println("dataSegBegin  ---------------> " + dataSegBegin / 256 );
        System.out.println("codeSegBegin  ---------------> " + codeSegBegin / 256 );


        ArrayList<Parser.Command> codeSegment = supervisorMemory.getCodeSegs().get(task);
        ArrayList<Parser.Command> dataSegment = supervisorMemory.getDataSegs().get(task);

        Memory externalMemory = realMachine.getExternalMemory();

        for (Parser.Command c : codeSegment) {
            int adr = c.getPosition() + codeSegBegin;
            try {
                externalMemory.setWord(new Word(c.getValue(), SYMBOLIC), adr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (Parser.Command c : dataSegment) {
            int adr = c.getPosition() + dataSegBegin;
            try {
                externalMemory.setWord(new Word(c.getValue(), Word.WORD_TYPE.NUMERIC), adr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        supervisorMemory.getCodeSegs().remove(task);
        supervisorMemory.getDataSegs().remove(task);

    }

}