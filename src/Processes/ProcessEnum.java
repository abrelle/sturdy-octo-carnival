package Processes;

public class ProcessEnum {

    public static final int REAL_MACHINE_PRIORITY = 1;
    public static final int PARSER_PRIORITY = 9;
    public static final int JOB_TO_SWAP_PRIORITY = 4;
    public static final int LOADER_PRIORITY = 3;
    public static final int SWAPPING_PRIORITY = 5;
    public static final int PRINT_LINE_PRIORITY = 6;
    public static final int INTERRUPT_PRIORITY = 7;
    public static final int MAIN_PROC_PRIORITY = 5;
    public static final int JOB_GOVERNOR_PRIORITY = 8;
    public static final int VIRTUAL_MACHINE_PRIORITY = 100;
    public static final int READ_FROM_INTERFACE_PRIORITY = 2;

    public enum Name {
        REAL_MACHINE,
        JOB_GOVERNOR,
        READ_FROM_INTERFACE,
        JOB_TO_SWAP,
        LOADER,
        MAIN_PROC,
        PARSER,
        PRINT_LINE,
        SWAPPING,
        VIRTUAL_MACHINE,
        INTERRUPT,
    }

    public enum State {
        BLOCKED,
        PREPARED,
        ACTIVE,
    }

}
