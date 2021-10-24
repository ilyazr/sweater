package ru.zakharov.exceptions;

public class WallMessageException extends RuntimeException {

    public enum ExceptionType {
        NOT_FOUND("Message with id %d not found!");
        private final String errorMsg;
        ExceptionType(String errorMsg) {
            this.errorMsg = errorMsg;
        }
        public String getErrorMsg() {
            return errorMsg;
        }
    }

    public WallMessageException(String message) {
        super(message);
    }

    public WallMessageException(int id, ExceptionType type) {
        this(String.format(type.getErrorMsg(), id));
    }
}
