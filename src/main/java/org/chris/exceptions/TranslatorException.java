package org.chris.exceptions;

public class TranslatorException extends Exception {
    public TranslatorException(String message) {
        super(message);
    }
    public TranslatorException(String message, Throwable cause) {
        super(message, cause);
    }

}
