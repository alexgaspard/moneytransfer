package org.example.moneytransfer.rest.http.providers;

public class ErrorMessage {
    private String message;

    @SuppressWarnings("unused")
    public ErrorMessage() { // Needed for Jersey
    }

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
