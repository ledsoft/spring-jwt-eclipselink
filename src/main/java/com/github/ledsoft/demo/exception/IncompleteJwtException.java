package com.github.ledsoft.demo.exception;

/**
 * Indicates that the authentication JSON Web token is missing something.
 */
public class IncompleteJwtException extends DemoException {

    public IncompleteJwtException(String message) {
        super(message);
    }
}
