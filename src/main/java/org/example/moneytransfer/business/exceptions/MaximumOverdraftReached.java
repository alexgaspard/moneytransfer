package org.example.moneytransfer.business.exceptions;

public class MaximumOverdraftReached extends UnknownError {

    public MaximumOverdraftReached(String message) {
        super(message);
    }
}
