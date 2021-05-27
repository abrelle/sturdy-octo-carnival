package Processes;

import Components.SupervisorMemory;
import Resources.Resource;
import Resources.ResourceDistributor;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import static Processes.ProcessEnum.Name.PARSER;
import static Processes.ProcessEnum.PARSER_PRIORITY;
import static Processes.ProcessEnum.State.BLOCKED;
import static Resources.ResourceEnum.Name.*;
import static Resources.ResourceEnum.Type.DYNAMIC;
import static Tools.Constants.*;

public class Parser extends ProcessInterface {

    private final int COMMAND_LENGTH = 6;



    private final ArrayList<Command> dataSegment;
    private final ArrayList<Command> codeSegment;

    public Parser(ProcessInterface father, ProcessPlanner planner, ResourceDistributor distributor) {

        super(father, BLOCKED, PARSER_PRIORITY, PARSER, planner, distributor);

        new Resource(this, TASK_PARAMETERS_IN_SUPERVISOR_MEMORY, DYNAMIC);

        dataSegment = new ArrayList<Command>(100);
        codeSegment = new ArrayList<Command>(100);

    }

    public void parseFile(String fileName) {

        dataSegment.clear();
        codeSegment.clear();
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            parse(scanner, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parsMODE(String fileName) {
        dataSegment.clear();
        codeSegment.clear();
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            parse(scanner, fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String checkCommand(String command) {
        if (command.length() < COMMAND_LENGTH) {
            for (int i = command.length(); i < COMMAND_LENGTH; i++) {
                command += "0";
            }
        }
        return command;
    }

    private void parse(Scanner scanner, String programName) throws Exception {
        ArrayList<String> fileContent = new ArrayList<String>(100);
        ArrayList<String> program ;


        while (scanner.hasNext()) {
            fileContent.add(scanner.next());
        }

        if (!(fileContent.contains(programName)
                && commandExists(fileContent, fileContent.indexOf(programName) - 1, PROGRAM_NAME)
                && commandExists(fileContent, fileContent.indexOf(programName) - 2, PROGRAM_BEGIN)))
        {
            throw new Exception("No program");
        }

        program = getProgram(fileContent, programName);
        int codeSegmentIndex = program.indexOf(CODE_SEGMENT_NAME);
        int dataSegmentIndex = program.indexOf(DATA_SEGMENT_NAME);

        for (int i = dataSegmentIndex + 1; i < codeSegmentIndex; i++) {
            String parsed = checkCommand(program.get(i));
            dataSegment.add(new Command(i - 1, parsed));
        }
        int address = 0;
        for (int i = codeSegmentIndex + 1; i < program.size(); i++) {
            String parsed = checkCommand(program.get(i));
            codeSegment.add(new Command(address, parsed));
            address++;
        }
    }

    public boolean commandExists(ArrayList<String> code, int index, String name) {
        return code.get(index).equals(name);
    }

    public ArrayList<String> getProgram(ArrayList<String> code, String name){

        int nameIndex = code.indexOf(name);
        ArrayList<String> programCode = new ArrayList<>(code.subList(nameIndex + 1, code.size()));
        int indexOfEnd = programCode.indexOf(PROGRAM_END);

        return new ArrayList<>(programCode.subList(0, indexOfEnd));
    }


    @Override
    public void executeTask() throws Exception {
        super.executeTask();

        System.out.println("\n------------PARSER------------\n");

        switch (IC) {
            case 0:
                IC++;
                resourceDistributor.ask(TASK_IN_SUPERVISOR_MEMORY, this);
                break;
            case 1:
                IC++;
                SupervisorMemory supervisorMemory = (SupervisorMemory) resourceDistributor.get(SUPERVISOR_MEMORY);
                String fileName = supervisorMemory.getFileList().getFirst();
                parseFile(fileName);
                if (dataSegment.size() > 0) {
                    supervisorMemory.getDataSegs().put(fileName, dataSegment);
                    if (codeSegment.size() > 0) {
                        IC = 0;
                        supervisorMemory.getCodeSegs().put(fileName, codeSegment);
                        resourceDistributor.disengage(TASK_PARAMETERS_IN_SUPERVISOR_MEMORY, fileName);
                    } else {
                        IC = 0;
                        resourceDistributor.disengage(TASK_COMPLETED, " Nekorektiškas užduoties failas");
                    }
                } else {
                    IC = 0;
                    resourceDistributor.disengage(TASK_COMPLETED, " Nekorektiškas užduoties failas");
                }
                break;
        }
    }

    public static class Command {
        int position;
        String value;

        Command(int pos, String val) {
            position = pos;
            value = val;
        }

        public int getPosition() {
            return position;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return position + " : " + value;
        }
    }
}