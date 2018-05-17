package org.example.moneytransfer.persistence.exceptions;

public class NotImplementedException extends DatabaseException {

    public NotImplementedException() {
        super("Method not implemented");
    }
}