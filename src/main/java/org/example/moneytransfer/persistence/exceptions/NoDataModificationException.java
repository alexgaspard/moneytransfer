package org.example.moneytransfer.persistence.exceptions;

public class NoDataModificationException extends DatabaseException {

    public NoDataModificationException() {
        super("No data modification");
    }
}