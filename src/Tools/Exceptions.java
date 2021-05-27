package Tools;

import Tools.Constants.PROGRAM_INTERRUPTION;

public class Exceptions {

    public static class ProgramInteruptionException extends Exception {
        PROGRAM_INTERRUPTION interruption;

        public ProgramInteruptionException(PROGRAM_INTERRUPTION interruption) {
            super(interruption.name());
            this.interruption = interruption;
        }

        @Override
        public void printStackTrace() {
            super.printStackTrace();
        }

        public PROGRAM_INTERRUPTION getReason() {
            return interruption;
        }
    }

    public static class StackPointerException extends ProgramInteruptionException {
        public StackPointerException(PROGRAM_INTERRUPTION interruption) {
            super(interruption);
        }
    }

    public static class InstructionPointerException extends ProgramInteruptionException {
        public InstructionPointerException(PROGRAM_INTERRUPTION interruption) {
            super(interruption);
        }
    }

    public static class WrongAddressException extends ProgramInteruptionException {
        private final Word address;

        public WrongAddressException(PROGRAM_INTERRUPTION interruption, Word address) {
            super(interruption);
            this.address = address;
        }

        public Word getAddress() {
            return address;
        }
    }
}

