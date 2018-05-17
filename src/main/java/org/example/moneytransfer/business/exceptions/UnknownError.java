package org.example.moneytransfer.business.exceptions;

public class UnknownError extends Exception {

    private String message;

    public UnknownError(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

