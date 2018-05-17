package org.example.moneytransfer.persistence.exceptions;


public abstract class DatabaseException extends Exception {

    private String message;

    DatabaseException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

