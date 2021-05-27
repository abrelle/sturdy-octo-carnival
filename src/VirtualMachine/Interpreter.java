package VirtualMachine;

import Components.CPU;
import Tools.Constants;
import Tools.Constants.PROGRAM_INTERRUPTION;
import Tools.Exceptions.InstructionPointerException;
import Tools.Exceptions.ProgramInteruptionException;
import Tools.Exceptions.WrongAddressException;
import Tools.Word;


import static Tools.Constants.FLAGS.*;
import static Tools.Constants.SYSTEM_INTERRUPTION.NONE;
import static Tools.Constants.SYSTEM_INTERRUPTION.*;
import static Tools.Word.WORD_TYPE.NUMERIC;
import static java.lang.Long.parseLong;

public class Interpreter {
    private final CPU cpu;

    public Interpreter(CPU cpu) {
        this.cpu = cpu;
    }

    public void execute(String command) {
        if (command.contains("AD")) {
            AD();
        } else if (command.contains("SB")) {
            SB();
        } else if (command.contains("ML")) {
            ML();
        } else if (command.contains("DV")) {
            DV();
        } else if (command.contains("MD")) {
            MD();
        }else if (command.contains("INC")) {
            INC();
        } else if (command.contains("DEC")) {
            DEC();
        } else if (command.contains("CLR")) {
            CLR();
        } else if (command.contains("CM")) {
            CM();
        } else if (command.contains("JH")) {
            JH();
        } else if (command.contains("JE")) {
            JE();
        } else if (command.contains("JN")) {
            JN();
        } else if (command.contains("JL")) {
            JL();
        } else if (command.contains("JM")) {
            JUMP();
        }  else if (command.contains("HALT")) {
            HALT();
        } else if (command.contains("PO")) {
            PO();
        } else if (command.contains("SP")) {
            SP();
        } else if (command.contains("GD")) {
            GD();
        } else if (command.contains("PD")) {
            PD();
        } else if (command.contains("AND")) {
            AND();
        } else if (command.contains("OR")) {
            OR();
        } else if (command.contains("XOR")) {
            XOR();
        } else if (command.contains("NOT")) {
            NOT();
        }
        else if (command.contains("SM")) {
            SM();
        }else {
            System.out.print("Not found ");
        }
    }

    public boolean PUSH(Word value) {
        Word address = cpu.getSP().copy();
        try {
            if(cpu.setSS(address, value))return true;
            cpu.increaseSP();
        } catch (ProgramInteruptionException e) {
            e.printStackTrace();
            PROGRAM_INTERRUPTION interruption = e.getReason();
            cpu.setPI(interruption);
        }
        return false;
    }

    public Word POP() {
        try {
            Word address = cpu.getSP().copy();
            Word value = cpu.getSS(address.add(-1));
            cpu.decreaseSP();
            return value;
        } catch (ProgramInteruptionException e) {
            e.printStackTrace();
            PROGRAM_INTERRUPTION interruption = e.getReason();
            cpu.setPI(interruption);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void AD() {
        System.out.println("AD");
        try {
            long op1 = POP().getNumber();
            long op2 = POP().getNumber();
            long result = op1 + op2;
            long manyF = parseLong("ffffff", 16);
            if (result <= manyF) {
                PUSH(new Word(result));

            } else {
                cpu.setSR(OVERFLOW_FLAG_INDEX, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SB() {
        System.out.println("SB");
        try {
            long op1 = POP().getNumber();
            long op2 = POP().getNumber();
            long result = op1 - op2;
            PUSH(new Word(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ML() {
        System.out.println("ML()");
        try {
            long op1 = POP().getNumber();
            long op2 = POP().getNumber();
            long result =  op1 * op2;
            if (result > Constants.MAX_WORD_SIZE_NUMBER) {
                cpu.setSR(OVERFLOW_FLAG_INDEX, 1);
            } else {
                PUSH(new Word((int) result));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void DV() {
        System.out.println("DV()");
        try {
            long op1 = POP().getNumber();
            long op2 = POP().getNumber();
            if (op2 == 0) {
                cpu.setSR(ZERO_FLAG_INDEX, 1);
            } else {
                long div = op1 / op2;
                PUSH(new Word(div));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void MD() {
        System.out.println("MD()");
        try {
            long op1 = POP().getNumber();
            long op2 = POP().getNumber();
            long mod = op1 % op2;
            PUSH(new Word(mod));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void INC() {
        System.out.println("INC()");
        try {
            long op1 = POP().getNumber();
            long result = op1 + 1;
            if (result > Constants.MAX_WORD_SIZE_NUMBER) {
                cpu.setSR(OVERFLOW_FLAG_INDEX, 1);
            } else {
               PUSH(new Word(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void DEC() {
        System.out.println("DEC()");
        try {
            long op1 = POP().getNumber();
            long result = op1 - 1;
            PUSH(new Word(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CLR() {
        System.out.println("CLR()");
        try {
            POP();
            PUSH(new Word(0));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CM() {
        System.out.println("CM()");
        try {
            Word w1 = POP();
            Word w2 = POP();

            if (w1.getNumber() == w2.getNumber()) {
                cpu.setSR(ZERO_FLAG_INDEX, 1);
            } else if (w1.getNumber() > w2.getNumber()) {
                cpu.setSR(ZERO_FLAG_INDEX, 0);
                cpu.setSR(CARRY_FLAG_INDEX, 0);
            } else {
                cpu.setSR(ZERO_FLAG_INDEX, 0);
                cpu.setSR(CARRY_FLAG_INDEX, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void AND() {
        System.out.println("AND()");
        try {
            long op1 = POP().getNumber();
            long op2 = POP().getNumber();
            PUSH(new Word(op1 & op2));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void OR() {
        System.out.println("OR()");
        try {
            long op1 = POP().getNumber();
            long op2 = POP().getNumber();
            PUSH(new Word(op1 | op2));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void XOR() {
        System.out.println("XOR()");
        try {
            long op1 = POP().getNumber();
            long op2 = POP().getNumber();
            PUSH(new Word(op1 ^ op2));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void NOT() {
        System.out.println("NOT()");
        try {
            long op1 = POP().getNumber();
            PUSH(new Word(~op1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void JUMP() {
        System.out.println("JUMP()");
        try {
            cpu.setIC(POP());
        } catch (InstructionPointerException e) {
            e.printStackTrace();
            PROGRAM_INTERRUPTION interruption = e.getReason();
            cpu.setPI(interruption);
        }
    }

    public String getVirtualAddress() {
        Word address = cpu.getIC().copy();
        Word value = null;
        try {
            value = cpu.getCS(address);
            return value.getASCIIFormat().substring(2);
        } catch (WrongAddressException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void JH() {
        System.out.println("JH()");
        try {
            byte SR = cpu.getSR();
            if (((SR >> CARRY_FLAG_INDEX.getValue()) & 1) == 0 && ((SR >> ZERO_FLAG_INDEX.getValue()) & 1) == 0) {
                JUMP();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void JE() {
        System.out.println("JE()");
        try {
            byte SR = cpu.getSR();
            if (((SR >> ZERO_FLAG_INDEX.getValue()) & 1) == 1) {
                JUMP();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void JN() {
        System.out.println("JN()");
        try {
            byte SR = cpu.getSR();
            if (((SR >> ZERO_FLAG_INDEX.getValue()) & 1) == 0) {
                JUMP();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void JL() {
        System.out.println("JL()");
        try {
            byte SR = cpu.getSR();

            if (((SR >> CARRY_FLAG_INDEX.getValue()) & 1) == 1 && ((SR >> ZERO_FLAG_INDEX.getValue()) & 1) == 0) {
                JUMP();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SP() {
        System.out.println("SP()");
        try {
            PUSH(cpu.getSP());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PO() {
        System.out.println("PO()");
        try {
            POP();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void SWAP() {
//        System.out.println("SWAP()");
//        Word rh = new Word(cpu.getRH().getNumber());
//        cpu.setRH(cpu.getRL());
//        cpu.setRL(rh);
//
//    }

    private void SM() {
        System.out.println("SM()");
        try {
            String virtualAddress = getVirtualAddress();
            Word address = new Word(virtualAddress, NUMERIC);
            Word value = cpu.getDS(address);
            if (value == null) return;
            PUSH(value);
        } catch (ProgramInteruptionException e) {
            e.printStackTrace();
            PROGRAM_INTERRUPTION interruption = e.getReason();
            cpu.setPI(interruption);
        }
    }


//    private void SAVE() {
//        System.out.println("SAVE()");
//        try {
//            String virtualAddress = getVirtualAddress();
//            Word address = new Word(virtualAddress, NUMERIC);
//            Word value = cpu.getRL().copy();
//            cpu.setDS(address, value);
//        } catch (ProgramInteruptionException e) {
//            e.printStackTrace();
//            PROGRAM_INTERRUPTION interruption = e.getReason();
//            cpu.setPI(interruption);
//        }
//    }

    boolean loaded = false;
    Word address;
    Word value;

    private void SAVES() {
        System.out.println("SAVES()");
        try {
            if(!loaded){
                long op1 = POP().getNumber();
                long op2 = POP().getNumber();
                address = new Word(op1);
                value = new Word(op2);
                loaded= true;
            }

            cpu.setDS(address, value);
            if(cpu.getSI()==NONE)
            {
                loaded = false;
            }
        } catch (ProgramInteruptionException e) {
            e.printStackTrace();
            PROGRAM_INTERRUPTION interruption = e.getReason();
            cpu.setPI(interruption);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GD() {
        System.out.println("GET()");
        cpu.setSI(PRINTLINE_GET);
    }

    private void PD() {
        System.out.println("PUT()");
        cpu.setSI(PRINTLINE_PUT);
    }

    private void HALT() {
        System.out.println("HALT()");
        cpu.setSI(HALT);
    }

}
