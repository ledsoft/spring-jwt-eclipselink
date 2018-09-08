package com.github.ledsoft.demo.exception;

/**
 * Base for all application-specific exceptions.
 */
public class DemoException extends RuntimeException {

    public DemoException() {
    }

    public DemoException(String message) {
        super(message);
    }

    public DemoException(String message, Throwable cause) {
        super(message, cause);
    }

    public DemoException(Throwable cause) {
        super(cause);
    }
}
