package org.example.moneytransfer.rest.exceptions;

public abstract class HttpException extends Exception {

    private int code;
    private String message;

    HttpException(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
