package Processes;

import Components.CPU;
import Components.SupervisorMemory;
import Resources.Resource;
import Resources.ResourceDistributor;
import Resources.ResourceEnum;
import Tools.Word;
import VirtualMachine.VirtualMachine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import static Processes.ProcessEnum.Name.PRINT_LINE;
import static Processes.ProcessEnum.PRINT_LINE_PRIORITY;
import static Resources.ResourceEnum.Name.FROM_PRINTLINE;
import static Resources.ResourceEnum.Name.PRINTLINE;
import static Resources.ResourceEnum.Type.*;
import static Tools.Word.WORD_TYPE.NUMERIC;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class PrintLine extends ProcessInterface {

    public static final int MULTIPLE = 1;
    private final RealMachine realMachine;
    private final Boolean waitingForInput = true;
    private final SupervisorMemory supervisorMemory;

    public PrintLine(RealMachine father, ProcessPlanner processPlanner, ResourceDistributor resourceDistributor) {

        super(father, ProcessEnum.State.BLOCKED, PRINT_LINE_PRIORITY, PRINT_LINE, processPlanner, resourceDistributor);
        this.realMachine = father;
        supervisorMemory = realMachine.getSupervisorMemory();

        new Resource(this, PRINTLINE, DYNAMIC);
        new Resource(this, FROM_PRINTLINE, DYNAMIC);


    }

    @Override
    public void executeTask() throws Exception {
        System.out.println("\n------------PRINTLINE------------\n");
        super.executeTask();

        switch (IC) {
            case 0:
                IC++;
                resourceDistributor.ask(PRINTLINE, this);
                break;
            case 1:
                IC = 0;
                Resource resource = resourceDistributor.get(PRINTLINE);
                VirtualMachine virtualMachine = (VirtualMachine) resource.get(0);
                List<Object> elements = (List<Object>) resource.get(1);
                String state = elements.get(0).toString();
                switch (state) {
                    case "INPUT":
                        String address = (String) elements.get(1);
                        read();
                        for (int i = 0; i < MULTIPLE; i++) {
                            int addr = Integer.parseInt(address, 16) + i;
                            virtualMachine.getInputBuffer().add(virtualMachine.bufferElementsFactory(new Word(addr)
                                    , new Word(inputLines.get(i), NUMERIC)));
                        }
                        resourceDistributor.disengage(FROM_PRINTLINE);
                        break;
                    case "OUTPUT":
                        String nextState = elements.get(1).toString();
                        switch (nextState) {
                            case "WORDS":
                                virtualMachine.getOutputBuffer().clear();
                                break;
                            case "REGISTERS":
                                CPU cpu = virtualMachine.getCpu();
                                break;
                        }
                        resourceDistributor.disengage(FROM_PRINTLINE);
                        break;
                }
                break;
        }

    }



    public void read() {
        synchronized (waitingForInput) {
            try {
                waitingForInput.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> inputLines;


    private static final String NO_INPUT = "NO INPUT";
    private static final String NOT_MULTIPLE_OF_16 = "NOT MULTIPLE OF 16";
    private static final String WORD_LENGTH_MORE_THAN_6_OR_LESS_THAN_1 = "WORD LENGTH MORE THAN 6 OR LESS THAN 1";
    private static final String ALL_GOOD = "ALL GOOD";

//    private boolean validation(List<String> input) {
//        String command = "READ";
//        if (input.size() == 0) { // no input
//            System.out.println(NO_INPUT);
//            outputScreen.append(command + " ----------------- > " + NO_INPUT + '\n');
//            return false;
//        } else if (input.size() % MULTIPLE != 0) { // not multiple of 16
//            System.out.println(NOT_MULTIPLE_OF_16);
//            outputScreen.append(command + " ----------------- > " + NOT_MULTIPLE_OF_16 + '\n');
//            return false;
//        } else if (checkWordLength(input)) { // word length more than 6
//            System.out.println(WORD_LENGTH_MORE_THAN_6_OR_LESS_THAN_1);
//            outputScreen.append(command + " ----------------- > " + WORD_LENGTH_MORE_THAN_6_OR_LESS_THAN_1 + '\n');
//            return false;
//        } else {
//            System.out.println(ALL_GOOD);
//            outputScreen.append(command + " ----------------- > " + ALL_GOOD + '\n');
//            return true;
//        }
//    }

    private boolean checkWordLength(List<String> input) {
        return input.stream()
                .filter(a -> a.length() > 6 || a.isEmpty())
                .collect(toList())
                .size() != 0;
    }

//    private void writeToDataSegment(List<String> lines, long address) {
//
//        try {
//            for (int i = 0; i < lines.size(); i++) {
//                String line = lines.get(i);
//                if (line.length() != 6) {
//                    while (line.length() != 6) {
//                        line = line + " ";
//                    }
//                }
//                System.out.println("line " + line);
//                int value = Integer.parseInt(line.replaceAll("\\s+",""),16);
//                cpu.setDS(new Word(address), new Word(value));
//            }
//        } catch (Exceptions.WrongAddressException e) {
//            e.printStackTrace();
//            Constants.PROGRAM_INTERRUPTION interruption =  e.getReason();
//            cpu.setPI(interruption);
//        }
//        System.out.println("Finish writing");
//    }
}


//    public void print(Word address) {
//        String command  = "PRINT";
//
////        try {
////            for (int i = 0; i<16; i++){
////                //String value =  cpu.getDS(new Word(address.getNumber()+i)).toString();
////                //outputScreen.append(command + " ----------------- > " + value +'\n');
////            }
////        } catch (Exceptions.WrongAddressException e) {
////            e.printStackTrace();
////            Constants.PROGRAM_INTERRUPTION interruption =  e.getReason();
////            cpu.setPI(interruption);
////        }
//    }
//
//
//    public void printRegisters() {
//        outputScreen.append("RL ---> " +  cpu.getRL().toString() +'\n');
//        outputScreen.append("RH ---> " +  cpu.getRH().toString() +'\n');
//        outputScreen.append("C --->  " + cpu.getC().toString() +'\n');
//    }
