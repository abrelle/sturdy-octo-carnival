package Processes;

import Components.SupervisorMemory;

import Resources.Resource;
import Resources.ResourceDistributor;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static Processes.ProcessEnum.Name.READ_FROM_INTERFACE;
import static Processes.ProcessEnum.READ_FROM_INTERFACE_PRIORITY;
import static Processes.ProcessEnum.State.PREPARED;
import static Processes.ProcessPlanner.looop;
import static Processes.ReadFromKeyboard.Situation.*;
import static Resources.ResourceEnum.Name.*;
import static Resources.ResourceEnum.Type.DYNAMIC;

public class ReadFromKeyboard extends ProcessInterface {

    static int teest = 0;
    private final RealMachine realMachine;

    String message;

    private Resource userInput;

    public ReadFromKeyboard(
            RealMachine father,
            ProcessPlanner planner,
            ResourceDistributor distributor) {

        super(father, PREPARED, READ_FROM_INTERFACE_PRIORITY, READ_FROM_INTERFACE, planner, distributor);
        new Resource(this, FROM_KEYBOARD, DYNAMIC);
        new Resource(this, TASK_IN_SUPERVISOR_MEMORY, DYNAMIC);
        new Resource(this, TASK_COMPLETED, DYNAMIC);
        new Resource(this, UPLOAD_VIRTUAL_MACHINE, DYNAMIC);

        this.realMachine = father;

    }

    private void readUserInput() {

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        List<String> lines = Arrays.asList(input.split(" "));
        String command = lines.get(0);

        if (command.equalsIgnoreCase("CREATEVM")) {
            String filename = lines.get(1).replace("\"", "");

            resourceDistributor.disengage(FROM_KEYBOARD, CREATEVM, scanner.nextLine());
            synchronized (looop) {
                looop.notifyAll();
            }

        } else if (command.equalsIgnoreCase("RUNALL")) {

            resourceDistributor.disengage(FROM_KEYBOARD, RUNALL);
            synchronized (looop) {
                looop.notifyAll();
            }
        }
        else if (command.equalsIgnoreCase("PARSE")) {

            String[] words = scanner.nextLine().split("\\s+");
            ((RealMachine)father).getParser().parsMODE(words[1]);
        }  else if (command.equalsIgnoreCase("OSEND")) {

            resourceDistributor.disengage(FROM_KEYBOARD, OSEND);
            synchronized (looop) {
                looop.notifyAll();
            }
        } else {

        }
    }

    @Override
    public void executeTask() throws Exception {

        System.out.println("\n------------READ FROM INTERFACE------------\n");
        super.executeTask();

        Scanner scanner = new Scanner(System.in);

        switch (IC) {
            case 0:
                IC++;
                resourceDistributor.ask(FROM_KEYBOARD, this);
                break;
            case 1:
                IC++;
                userInput = resourceDistributor.get(FROM_KEYBOARD);
                Situation situation = (Situation) userInput.getElementList().get(0);
                switch (situation) {
                    case RUNALL:
                        resourceDistributor.disengage(START_EXECUTION);
                        break;
                    case CREATEVM:
                        SupervisorMemory memory = (SupervisorMemory) resourceDistributor.get(SUPERVISOR_MEMORY);
                        String[] words = scanner.nextLine().split("\\s+");
                        if(words.length>1)
                        {
                            memory.getFileList().push(words[1]);
                            resourceDistributor.disengage(TASK_IN_SUPERVISOR_MEMORY);
                        }else {
                            IC = 0;
                        }
                        break;
                    case OSEND:
                        resourceDistributor.disengage(MOS_END);
                        break;
                }
                break;
            case 2:
                IC++;
                resourceDistributor.ask(TASK_COMPLETED, this);
                break;
            case 3:
                IC = 0;
                message = (String) resourceDistributor.get(TASK_COMPLETED).getElementList().toString();
                //System.out.println(ANSI_RED + "TURI_ATEITI_IKI_CIA --------------->" + message + teest + ANSI_BLACK);
                teest++;
                break;
        }
    }

    enum Situation {
        CREATEVM,
        RUNALL,
        OSEND,
    }
}
