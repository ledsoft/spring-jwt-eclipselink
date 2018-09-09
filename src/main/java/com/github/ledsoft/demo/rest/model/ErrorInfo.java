package com.github.ledsoft.demo.rest.model;

public class ErrorInfo {

    private String requestUri;
    private String message;

    public ErrorInfo() {
    }

    public ErrorInfo(String requestUri, String message) {
        this.requestUri = requestUri;
        this.message = message;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
