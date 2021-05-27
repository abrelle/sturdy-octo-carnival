package Resources;

public class ResourceEnum {

    public enum Name {

        MOS_END,
        FROM_KEYBOARD,
        SUPERVISOR_MEMORY,
        TASK_IN_SUPERVISOR_MEMORY,

        TASK_PARAMETERS_IN_SUPERVISOR_MEMORY,
        EXTERNAL_MEMORY,
        CHANNEL_DEVICE,
        TASK_IN_DRUM,
        LOADING_PACKAGE,
        FROM_LOADER,
        INTERNAL_MEMORY,
        FROM_INTERRUPT,


        PROCESS_INTERRUPT,



        UPLOAD_VIRTUAL_MACHINE,

        TASK_COMPLETED,

        SWAPPING,

        PRINTLINE,


        START_EXECUTION,

        FROM_PRINTLINE,

        EXTERNAL_MEMORY_DISENGAGED,
        FROM_SWAPING,
        WAIT_UNTIL_DESTRUCTION,
//
    }

    public enum Type {
        STATIC,
        DYNAMIC
    }
}
