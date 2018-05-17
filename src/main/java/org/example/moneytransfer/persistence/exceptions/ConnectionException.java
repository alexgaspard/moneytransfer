package org.example.moneytransfer.persistence.exceptions;

public class ConnectionException extends DatabaseException {

    public ConnectionException(String message) {
        super(message);
    }
}