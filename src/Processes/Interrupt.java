package Processes;

import Components.CPU;
import Resources.Resource;
import Resources.ResourceDistributor;
import Tools.Word;
import VirtualMachine.VirtualMachine;

import static Processes.Interrupt.State.*;
import static Processes.ProcessEnum.*;
import static Processes.ProcessEnum.Name.INTERRUPT;
import static Resources.ResourceEnum.Name.FROM_INTERRUPT;
import static Resources.ResourceEnum.Name.PROCESS_INTERRUPT;
import static Resources.ResourceEnum.Type.DYNAMIC;
import static Tools.Constants.SYSTEM_INTERRUPTION.NONE;
import static Tools.Constants.SYSTEM_MODE.SUPERVISOR_MODE;

public class Interrupt extends ProcessInterface {

    public Interrupt(RealMachine father, ProcessPlanner planner, ResourceDistributor distributor) {
        super(father, ProcessEnum.State.BLOCKED, INTERRUPT_PRIORITY, INTERRUPT, planner, distributor);
        new Resource(this, PROCESS_INTERRUPT, DYNAMIC);
        new Resource(this, FROM_INTERRUPT, DYNAMIC);
    }

    @Override
    public void executeTask() throws Exception {
        Word addr, value;

        System.out.println("\n------------INTERRUPT------------\n");
        super.executeTask();

        switch (IC) {
            case 0:
                IC++;
                resourceDistributor.ask(PROCESS_INTERRUPT, this);
                break;
            case 1:
                IC = 0;
                VirtualMachine vm = (VirtualMachine) resourceDistributor.get(PROCESS_INTERRUPT).get(0);
                CPU cpu = vm.getCpu();
                cpu.setMODE(SUPERVISOR_MODE);
                switch (cpu.getSI()) {
                    case HALT:
                        resourceDistributor.disengage(FROM_INTERRUPT, HALT);
                        break;
                    case TIMER:
                        cpu.increaseIC();
                        cpu.setSI(NONE);
                        resourceDistributor.disengage(FROM_INTERRUPT, TIMER);
                        break;
                    case PRINTLINE_GET:
                        String address = vm.getInterpretator().getVirtualAddress();
                        cpu.increaseIC();
                        cpu.setSI(NONE);
                        resourceDistributor.disengage(FROM_INTERRUPT, PRINTLINE, "INPUT", address);
                        break;
                    case PRINTLINE_PUT:
                        address = vm.getInterpretator().getVirtualAddress();
                        for (int i = 0; i < 16; i++) {
                            int addrNum = Integer.parseInt(address, 16);
                            vm.getOutputBuffer().add(vm.bufferElementsFactory(
                                    new Word(addrNum + i),
                                    null
                            ));
                        }
                        cpu.increaseIC();
                        cpu.setSI(NONE);
                        resourceDistributor.disengage(FROM_INTERRUPT, PRINTLINE_NEEDS_BUFFER);
                        break;
                    case PRINTLINE_READING_DONE:
                        cpu.setSI(NONE);
                        resourceDistributor.disengage(FROM_INTERRUPT, PRINTLINE, "OUTPUT", "WORDS");
                        break;
                    case PRINTLINE_PUT_R:
                        cpu.increaseIC();
                        cpu.setSI(NONE);
                        resourceDistributor.disengage(FROM_INTERRUPT, PRINTLINE, "OUTPUT", "REGISTERS");
                        break;
                    case SWAPING_CS:
                        //do pop
                        addr = cpu.getSP().copy();
                        value = cpu.getSS(addr.add(-1));
                        cpu.decreaseSP();
                        int newBlock = value.getBlockFromAddress();
                        cpu.setSI(NONE);
                        resourceDistributor.disengage(FROM_INTERRUPT, SWAPING, "CS", newBlock);
                        break;
                    case SWAPING_DS:
                        //do pop
                        addr = cpu.getSP().copy();
                        value = cpu.getSS(addr.add(-1));
                        cpu.decreaseSP();
                        newBlock = value.getBlockFromAddress();
                        cpu.setSI(NONE);
                        resourceDistributor.disengage(FROM_INTERRUPT, SWAPING, "DS", newBlock);
                        break;
                    case SWAPING_SS:
                        //do pop
                        addr = cpu.getSP().copy();
                        value = cpu.getSS(addr.add(-1));
                        cpu.decreaseSP();
                        newBlock = value.getBlockFromAddress();
                        cpu.setSI(NONE);
                        resourceDistributor.disengage(FROM_INTERRUPT, SWAPING, "SS", newBlock);
                        break;
                }
                break;
        }
    }

    enum State {
        HALT,
        TIMER,
        PRINTLINE,
        SWAPING,
        PRINTLINE_NEEDS_BUFFER,
    }
}