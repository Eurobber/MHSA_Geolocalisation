package com.ece.iceageophone.main.exception;

public class MessageFormatException extends Exception {

    public MessageFormatException(String message) {
        super(message);
    }

    public MessageFormatException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
