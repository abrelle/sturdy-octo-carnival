package Processes;

import Components.CPU;
import Components.Memory;
import Resources.Resource;
import Resources.ResourceDistributor;
import Tools.Word;

import static Processes.ProcessEnum.LOADER_PRIORITY;
import static Processes.ProcessEnum.Name.LOADER;
import static Processes.ProcessEnum.State.BLOCKED;
import static Resources.ResourceEnum.Name.FROM_LOADER;
import static Resources.ResourceEnum.Name.LOADING_PACKAGE;
import static Resources.ResourceEnum.Type.DYNAMIC;

public class Loader extends ProcessInterface {

    private final RealMachine realMachine;
    private final Memory internalMemory;
    private final Memory externalMemory;

    public Loader(RealMachine father, ProcessPlanner planner, ResourceDistributor distributor) {
        super(father, BLOCKED, LOADER_PRIORITY, LOADER, planner, distributor);
        this.realMachine = father;
        internalMemory = realMachine.getInternalMemory();
        externalMemory = realMachine.getExternalMemory();

        new Resource(this, LOADING_PACKAGE, DYNAMIC);
        new Resource(this, FROM_LOADER, DYNAMIC);
    }

    public void uploadBlock(long ptr, int internal, int external, char segment) {
        try {
            long external_in_memory_table = internalMemory.getWord(ptr + external).getNumber();
            Word[] ds = externalMemory.getBlock((int) external_in_memory_table);
            internalMemory.setBlock(internal, ds);

            Word value = new Word(internal);
            value.setByte(segment, 0);
            internalMemory.setWord(value, ptr + external);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadVirtualMachineMemory(CPU cpu) {
        long ptr = cpu.getPTR().getNumber() * 256;
        int ss_internal = cpu.getSS().getBlockFromAddress();
        int ss_external = cpu.getSS().getWordFromAddress();
        int ds_internal = cpu.getDS().getBlockFromAddress();
        int ds_external = cpu.getDS().getWordFromAddress();
        int cs_internal = cpu.getCS().getBlockFromAddress();
        int cs_external = cpu.getCS().getWordFromAddress();

        uploadBlock(ptr, ss_internal, ss_external, 'S');
        uploadBlock(ptr, ds_internal, ds_external, 'D');
        uploadBlock(ptr, cs_internal, cs_external, 'C');
    }

    public void saveBlock(int internal, int external) {
        try {
            Word[] block = internalMemory.getBlock(internal);
            externalMemory.setBlock(external, block);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveVirtualMachineMemory(CPU cpu) {
        int internal = cpu.getSS().getBlockFromAddress();
        int external = cpu.getExternalMemoryBegin() + cpu.getSS().getWordFromAddress();
        saveBlock(internal, external);
        internal = cpu.getDS().getBlockFromAddress();
        external = cpu.getExternalMemoryBegin() + cpu.getDS().getWordFromAddress();
        saveBlock(internal, external);
        internal = cpu.getCS().getBlockFromAddress();
        external = cpu.getExternalMemoryBegin() + cpu.getCS().getWordFromAddress();
        saveBlock(internal, external);
    }

    @Override
    public void executeTask() throws Exception {
        super.executeTask();

        System.out.println("\n------------LOADER------------\n");

        switch (IC) {
            case 0:
                IC++;
                resourceDistributor.ask(LOADING_PACKAGE, this);
                break;
            case 1:
                IC = 0;
                State state = (State) resourceDistributor.get(LOADING_PACKAGE).get(0);
                CPU cpu = (CPU) resourceDistributor.get(LOADING_PACKAGE).get(1);
                switch (state) {
                    case LOAD_VIRTUAL_MACHINE_MEMORY:
                        loadVirtualMachineMemory(cpu);
                        break;
                    case SAVE_VIRTUAL_MACHINE_MEMORY:
                        saveVirtualMachineMemory(cpu);
                        break;
                }
                resourceDistributor.disengage(FROM_LOADER);
                break;
        }
    }


    enum State {
        LOAD_VIRTUAL_MACHINE_MEMORY,
        SAVE_VIRTUAL_MACHINE_MEMORY,
    }

}