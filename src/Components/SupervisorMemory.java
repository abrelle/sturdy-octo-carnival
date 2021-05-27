package Components;

import Processes.Parser;
import Processes.ProcessInterface;
import Resources.Resource;
import Resources.ResourceEnum;
import Tools.Constants;
import Tools.Exceptions;
import Tools.Word;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;


public class SupervisorMemory extends Resource
{

    private CPU cpu;
    private final Deque<String> fileNames;

    private final HashMap<String, ArrayList<Parser.Command>> dataSegs;
    private final HashMap<String, ArrayList<Parser.Command>> codeSegs;



    public SupervisorMemory(ProcessInterface father){
        super(father, ResourceEnum.Name.SUPERVISOR_MEMORY, ResourceEnum.Type.STATIC);
        setAvailability(true);

        fileNames = new ArrayDeque<>(10);
        dataSegs = new HashMap<>(100);
        codeSegs = new HashMap<>(100);

    }

    public Deque<String> getFileList()
    {
        return fileNames;
    }

    public HashMap<String, ArrayList<Parser.Command>> getDataSegs() {
        return dataSegs;
    }
    public HashMap<String, ArrayList<Parser.Command>> getCodeSegs() {
        return codeSegs;
    }




    enum Type{
        RM,
        VM,
        ALL
    }

    class Registers{

        private Word IC = null;
        private  Word SP = null;
        private byte SR = 0;


        private  Word PTR = null;
        private  Word SS = null;
        private  Word DS = null;
        private  Word CS = null;
        private Constants.SYSTEM_INTERRUPTION SI;

        private final Type type;

        Registers(Type type){
            this.type = type;
            switch (type){
                case RM:
                    saveRM();
                    break;
                case VM:
                    saveVM();
                    break;
                case ALL:
                    saveRM();
                    saveVM();
                    break;
            }
        }

        private void saveVM(){

            IC = cpu.getIC().copy();
            SP = cpu.getSP().copy();
            SR = cpu.getSR();
        }

        private void saveRM(){
            PTR = cpu.getPTR().copy();
            SS = cpu.getSS().copy();
            DS = cpu.getDS().copy();
            CS = cpu.getCS().copy();
            SI = cpu.getSI();
        }

        private void restoreVM(){
            try {

                cpu.setIC(IC);
                cpu.setSP(SP);
                cpu.setSR(SR);
            } catch (Exceptions.InstructionPointerException e) {
                //Imposible because it was already saved one time
                e.printStackTrace();
            }

        }

        private void restoreRM(){
            cpu.setPTR(PTR);
            cpu.setSS(SS);
            cpu.setDS(DS);
            cpu.setCS(CS);
            cpu.setSI(SI);
        }


        public void restoreCPUState() {
            switch (type){
                case RM:
                    restoreRM();
                    break;
                case VM:
                    restoreVM();
                    break;
                case ALL:
                    restoreRM();
                    restoreVM();
                    break;
            }
        }
    }

}