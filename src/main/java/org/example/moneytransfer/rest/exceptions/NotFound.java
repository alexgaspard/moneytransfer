package org.example.moneytransfer.rest.exceptions;

import javax.ws.rs.core.Response;

public class NotFound extends HttpException {

    public NotFound(String message) {
        super(Response.Status.NOT_FOUND.getStatusCode(), message);
    }
}
