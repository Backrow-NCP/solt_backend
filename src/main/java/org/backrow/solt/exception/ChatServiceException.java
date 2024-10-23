package org.backrow.solt.exception;

public class ChatServiceException extends RuntimeException {
    public ChatServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatServiceException(String message) {
        super(message);
    }
}
