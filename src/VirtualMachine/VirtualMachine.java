package VirtualMachine;

import Components.CPU;
import Components.SupervisorMemory;
import Processes.JobGorvernor;
import Processes.ProcessEnum;
import Processes.ProcessInterface;
import Processes.ProcessPlanner;
import Processes.RealMachine;
import Resources.ResourceDistributor;

import Tools.Constants.PROGRAM_INTERRUPTION;
import Tools.Exceptions.ProgramInteruptionException;
import Tools.Exceptions.WrongAddressException;
import Tools.Word;

import java.util.ArrayList;

import static Processes.ProcessEnum.Name.VIRTUAL_MACHINE;
import static Processes.ProcessEnum.VIRTUAL_MACHINE_PRIORITY;
import static Resources.ResourceEnum.Name.PROCESS_INTERRUPT;
import static Tools.Constants.SYSTEM_INTERRUPTION.NONE;
import static Tools.Constants.SYSTEM_INTERRUPTION.PRINTLINE_READING_DONE;
import static Tools.Constants.SYSTEM_INTERRUPTION.TIMER;
import static Tools.Constants.SYSTEM_MODE.USER_MODE;

public class VirtualMachine extends ProcessInterface {

    public static int VirtualMachinePriority = VIRTUAL_MACHINE_PRIORITY;
    private static int Timer = 10;
    private final String name;
    private final CPU cpu;
    private final int taskID;
    private final SupervisorMemory supervisorMemory;
    private final ArrayList<BufferElements> inputBuffer;
    private final ArrayList<BufferElements> outputBuffer;
    private RealMachine realMachine;
    private Interpreter interpreter;
    private boolean launchedFirstTime = true;
    private int wordsRead = 0;

    public VirtualMachine(
            JobGorvernor father,
            ProcessPlanner processPlanner,
            ResourceDistributor resourceDistributor,
            String name, int ID) {

        super(father,
                ProcessEnum.State.ACTIVE,
                VirtualMachinePriority,
                VIRTUAL_MACHINE,
                processPlanner,
                resourceDistributor);

        VirtualMachinePriority++;

        this.name = name;
        this.taskID = ID;
        this.cpu = father.getCpu();
        inputBuffer = new ArrayList<>();
        outputBuffer = new ArrayList<>();

        interpreter = new Interpreter(cpu);
        supervisorMemory = father.getRealMachine().getSupervisorMemory();
    }

    public BufferElements bufferElementsFactory(Word address, Word value) {
        return new BufferElements(address, value);
    }

    public ArrayList<BufferElements> getInputBuffer() {
        return inputBuffer;
    }

    public ArrayList<BufferElements> getOutputBuffer() {
        return outputBuffer;
    }

    public CPU getCpu() {
        return cpu;
    }

    private void prepareRegisters() {
        try {
            System.out.println("---------------> prepareRegisters");

            cpu.setIC(new Word(0));
            cpu.setSP(new Word(0));
            cpu.setTI(Timer);
            cpu.setMODE(USER_MODE);
            cpu.setPI(PROGRAM_INTERRUPTION.NONE);
            launchedFirstTime = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void testTimer() {
        cpu.decTI();
        if (cpu.getTI() == 0) {
            cpu.setTI(Timer);
            cpu.setSI(TIMER);
        }
    }

    public Interpreter getInterpretator() {
        return interpreter;
    }

    private boolean writeFromBuffer() {
        if (inputBuffer.size() > 0) {
            while (inputBuffer.size() != 0) {
                BufferElements e = inputBuffer.get(0);
                try {
                    cpu.setDS(e.getAddress(), e.getValue());
                } catch (WrongAddressException ex) {
                    ex.printStackTrace();
                }
                if (! cpu.getSI().equals(NONE)) {
                    return true;
                }
                inputBuffer.remove(0);
            }
        }
        return false;
    }

    private boolean readToBuffer() {
        if (outputBuffer.size() > 0) {
            while (outputBuffer.size() != wordsRead) {
                BufferElements e = outputBuffer.get(0);
                try {
                    wordsRead++;
                    e.value = cpu.getDS(e.getAddress());
                } catch (WrongAddressException ex) {
                    ex.printStackTrace();
                }
                System.out.println( "READ BUFFER -----> " + e.address + "<---->" + e.value );
                if (! cpu.getSI().equals(NONE)) {
                    return true;
                }
            }
            wordsRead = 0;
            cpu.setSI(PRINTLINE_READING_DONE);
            return true;
        }
        return false;
    }

    @Override
    public void executeTask() throws Exception {
        System.out.println("\n------------VIRTUAL MACHINE------------\n");
        super.executeTask();

        if (launchedFirstTime) {
            prepareRegisters();
        }

        cpu.setMODE(USER_MODE);
       // cpu.refresh();

        try {
            while (true) {
                if (writeFromBuffer()) break;
                if (readToBuffer()) break;

                Word address = cpu.getIC().copy();
                Word value = cpu.getCS(address);
                if (value != null) {
                    String command = value.getASCIIFormat();
                    System.out.println( "---------------------------------------  > " + command);
                    interpreter.execute(command);
                    //System.out.println(ANSI_RED + " RL: " + cpu.getRL() + "  RH:  " + cpu.getRH() + " C: " + cpu.getC() + ANSI_BLACK);
                    if (!cpu.getSI().equals(NONE)) {
                        break;
                    }
                    testTimer();
                    cpu.increaseIC();
                }
            }
        } catch (ProgramInteruptionException e) {
            e.printStackTrace();
        }
        resourceDistributor.disengage(PROCESS_INTERRUPT, this);
    }

    public String getName() {
        return name;
    }

    public int getTaskID() {
        return taskID;
    }

    public class BufferElements {
        Word address;
        Word value;

        BufferElements(Word address, Word value) {
            this.address = address;
            this.value = value;
        }

        public Word getAddress() {
            return address;
        }

        public Word getValue() {
            return value;
        }
    }
}
