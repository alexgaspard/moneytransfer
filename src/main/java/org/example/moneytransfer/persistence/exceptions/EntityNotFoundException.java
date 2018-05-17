package org.example.moneytransfer.persistence.exceptions;

public class EntityNotFoundException extends DatabaseException {

    public EntityNotFoundException(String message) {
        super(message);
    }
}
