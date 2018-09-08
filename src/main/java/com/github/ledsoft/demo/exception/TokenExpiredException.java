package com.github.ledsoft.demo.exception;

/**
 * Indicates that user's authentication token has expired.
 */
public class TokenExpiredException extends DemoException {

    public TokenExpiredException(String message) {
        super(message);
    }
}
