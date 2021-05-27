package Tools;

public class Constants {
    public static final int WORD_LENGTH = 4;
    public static final int BLOCK_LENGTH = 256;
    public static final int BLOCK_NUMBER = 256;
    public static final int WORD_NUMBER = 65535;

    public static final int STACK_SEGMENT = 0;
    public static final int DATA_SEGMENT = 7169;
    public static final int CODE_SEGMENT = 17410;

    public static final long MAX_NUMBER = 262147;


    public static final String PROGRAM_BEGIN = "$BGN";
    public static final String PROGRAM_END = "$END";
    public static final String PROGRAM_NAME = "$PRN";
    public static final String CODE_SEGMENT_NAME = "$BCS";
    public static final String DATA_SEGMENT_NAME = "$BDS";

    public static final long MAX_WORD_SIZE_NUMBER = 65536;


    public static final int F_VALUE = 16;
    public static final int FF_VALUE = 256;
    public static final int FFF_VALUE = 4096;


    public enum FLAGS {
        STATUS_FLAG_INDEX(3),
        CARRY_FLAG_INDEX(2),
        ZERO_FLAG_INDEX(1),
        OVERFLOW_FLAG_INDEX(0);

        private final int value;

        FLAGS(final int newValue) {
            value = newValue;
        }

        public int getValue() {
            return value;
        }
    }

    public enum SYSTEM_INTERRUPTION {
        NONE,
        TIMER,
        SWAPING_SS,
        SWAPING_DS,
        SWAPING_CS,
        PRINTLINE_GET,
        PRINTLINE_PUT,
        PRINTLINE_PUT_R,
        PRINTLINE_READING_DONE,
        HALT,
    }

    public enum PROGRAM_INTERRUPTION {
        NONE,
        WRONG_SP,
        NEGATIVE_SP,
        WRONG_IC,
        WRONG_SS_BLOCK_ADDRESS,
        WRONG_DS_BLOCK_ADDRESS,
        WRONG_CS_BLOCK_ADDRESS,
        DIVISION_BY_ZERO,

    }

    public enum SYSTEM_MODE {
        USER_MODE,
        SUPERVISOR_MODE,
    }

}
