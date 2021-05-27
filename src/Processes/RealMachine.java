package Processes;

import Components.CPU;
import Components.Memory;
import Components.SupervisorMemory;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum.Type;

import static Processes.ProcessEnum.Name.REAL_MACHINE;
import static Processes.ProcessEnum.REAL_MACHINE_PRIORITY;
import static Resources.ResourceEnum.Name.*;

public class RealMachine extends ProcessInterface {

    private final Memory externalMemory;
    private final Memory internalMemory;
    private final SupervisorMemory supervisorMemory;

    private final Parser parser;
    private final Loader loader;
    private final JobToSwap jobToSwap;
    private final Swapping swapping;
    private final PrintLine printLine;
    private final ReadFromKeyboard readFromKeyboard;
    private final MainProc mainProc;
    private final Interrupt interrupt;
    private final CPU cpu;


    public RealMachine(ProcessPlanner processPlanner, ResourceDistributor distributor) throws Exception {
        super(null, ProcessEnum.State.ACTIVE, REAL_MACHINE_PRIORITY, REAL_MACHINE, processPlanner, distributor);
        internalMemory = new Memory(this, INTERNAL_MEMORY, 16, 1);
        externalMemory = new Memory(this, EXTERNAL_MEMORY, 2560, 256);

        supervisorMemory = new SupervisorMemory(this);
        cpu = new CPU(this);

        new Resource(this, MOS_END, Type.DYNAMIC);

        parser = new Parser(this, processPlanner, distributor);
        jobToSwap = new JobToSwap(this, processPlanner, distributor);
        loader = new Loader(this, processPlanner, distributor);
        swapping = new Swapping(this, processPlanner, distributor);
        printLine = new PrintLine(this, processPlanner, distributor);
        readFromKeyboard = new ReadFromKeyboard(this, processPlanner, distributor);
        mainProc = new MainProc(this, processPlanner, distributor);
        interrupt = new Interrupt(this, processPlanner, distributor);

        setActive(true);
        setPrepared(true);

        readFromKeyboard.setPrepared(true);
        loader.setPrepared(true);
        jobToSwap.setPrepared(true);
        mainProc.setPrepared(true);
        parser.setPrepared(true);
        interrupt.setPrepared(true);
        printLine.setPrepared(true);
        swapping.setPrepared(true);

        processPlanner.runOperatingSystem();
    }

    @Override
    public void executeTask() throws Exception {
        System.out.println("\n------------PROCESS PLANNER------------\n");
        super.executeTask();
        switch(IC) {
            case 0:
                IC++;
                resourceDistributor.ask(MOS_END, this);
                break;
            case 1:
                try {
                    for (ProcessInterface proc : createdProcesses) {
                        Thread.sleep(300);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
                break;
        }
    }

    public Memory getExternalMemory() {
        return externalMemory;
    }

    public Memory getInternalMemory() {
        return internalMemory;
    }

    public SupervisorMemory getSupervisorMemory() {
        return supervisorMemory;
    }

    public Loader getLoader() {
        return loader;
    }
    public Parser getParser() {
        return parser;
    }

    public CPU getDescriptor(int id){
        return cpu.getDescriptor(id);
    }

}
