package Processes;

import Components.CPU;
import Components.Memory;
import Components.SupervisorMemory;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum.Name;
import Resources.ResourceEnum.Type;
import Tools.Word;
import VirtualMachine.VirtualMachine;

import static Processes.ProcessEnum.Name.SWAPPING;
import static Processes.ProcessEnum.SWAPPING_PRIORITY;
import static Processes.ProcessEnum.State.BLOCKED;
import static Resources.ResourceEnum.Name.FROM_SWAPING;
import static Tools.Constants.*;

public class Swapping extends ProcessInterface {

    private final RealMachine realMachine;
    private final Memory internalMemory;
    private final Memory externalMemory;
    private final SupervisorMemory supervisorMemory;

    public Swapping(RealMachine father, ProcessPlanner planner, ResourceDistributor distributor) {

        super(father, BLOCKED, SWAPPING_PRIORITY, SWAPPING, planner, distributor);

        new Resource(this, FROM_SWAPING, Type.DYNAMIC);
        new Resource(this, Name.SWAPPING, Type.DYNAMIC);
        new Resource(this, Name.CHANNEL_DEVICE, Type.DYNAMIC);


        this.realMachine = father;
        internalMemory = realMachine.getInternalMemory();
        externalMemory = realMachine.getExternalMemory();
        supervisorMemory = realMachine.getSupervisorMemory();
    }

    @Override
    public void executeTask() throws Exception {
        System.out.println("\n------------SWAPPING------------\n");
        super.executeTask();
        VirtualMachine vm = null;
        switch (IC) {
            case 0:
                IC++;
                resourceDistributor.ask(Name.SWAPPING, this);
                break;
            case 1:
                IC++;
                resourceDistributor.ask(Name.CHANNEL_DEVICE, this);
                break;
            case 2:
                IC = 0;
                Resource resource = resourceDistributor.get(Name.SWAPPING);
                vm = (VirtualMachine) resource.get(0);
                CPU cpu = vm.getCpu();
                String state = (String) resource.get(1);
                int block = (int) resource.get(2);
                switch (state) {
                    case "CS":
                        swapCS(block, cpu);
                        break;
                    case "DS":
                        swapDS(block, cpu);
                        break;
                    case "SS":
                        swapSS(block, cpu);
                        break;
                }
                resourceDistributor.disengage(FROM_SWAPING);
                break;
        }
    }

    public void saveBlock(int internal, int external) {
        try {
            Word[] block = internalMemory.getBlock(internal);
            externalMemory.setBlock(external, block);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void swapSS(int newBlock, CPU cpu) {
        int internal = cpu.getSS().getBlockFromAddress();
        int external = cpu.getExternalMemoryBegin() + cpu.getSS().getWordFromAddress();
        saveBlock(internal, external);
        long addr = findSegmentInMemoryTable('S', cpu);
        try {
            internalMemory.setWord(new Word(external), addr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long ptr = cpu.getPTR().getNumber() * 256;
        external = (STACK_SEGMENT / 256) + newBlock;
        uploadBlock(ptr, internal, external, 'S');
        int newSSvalue = internal * 256 + external;
        cpu.setSS(new Word(newSSvalue));
    }

    public void uploadBlock(long ptr, int internal, int external, char segment) {
        try {
            long externalInMemoryTable = internalMemory.getWord(ptr + external).getNumber();
            Word[] ds = externalMemory.getBlock((int) externalInMemoryTable);
            internalMemory.setBlock(internal, ds);

            Word value = new Word(internal);
            value.setByte(segment, 0);
            internalMemory.setWord(value, ptr + external);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void swapDS(int newBlock, CPU cpu) {
        int internal = cpu.getDS().getBlockFromAddress();
        int external = cpu.getExternalMemoryBegin() + cpu.getDS().getWordFromAddress();
        saveBlock(internal, external);

        long addr = findSegmentInMemoryTable('D', cpu);
        try {
            internalMemory.setWord(new Word(external), addr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long ptr = cpu.getPTR().getNumber() * 256;
        external = (DATA_SEGMENT / 256) + newBlock;
        uploadBlock(ptr, internal, external, 'D');

        int newDSvalue = internal * 256 + external;
        cpu.setDS(new Word(newDSvalue));
    }

    private void swapCS(int newBlock, CPU cpu) {
        int internal = cpu.getCS().getBlockFromAddress();
        int external = cpu.getExternalMemoryBegin() + cpu.getCS().getWordFromAddress();
        saveBlock(internal, external);

        long addr = findSegmentInMemoryTable('C', cpu);
        try {
            internalMemory.setWord(new Word(external), addr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long ptr = cpu.getPTR().getNumber() * 256;
        external = (CODE_SEGMENT / 256) + newBlock;
        uploadBlock(ptr, internal, external, 'C');

        int newCSValue = internal * 256 + external;
        cpu.setCS(new Word(newCSValue));
    }

    public long findSegmentInMemoryTable(int segment, CPU cpu) {
        Memory internalMemory = realMachine.getInternalMemory();
        long ptr = cpu.getPTR().getNumber() * 256;

        for (int i = 0; i < 256; i++) {
            try {
                long virtualAddress = ptr + i;
                int firstByte = internalMemory.getWord(virtualAddress).getByte(0);
                if (firstByte == segment) {
                    return virtualAddress;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return - 1;
    }
}

