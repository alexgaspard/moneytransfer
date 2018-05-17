package org.example.moneytransfer.rest.exceptions;

import javax.ws.rs.core.Response;

public class BadRequest extends HttpException {

    public BadRequest(String message) {
        super(Response.Status.BAD_REQUEST.getStatusCode(), message);
    }
}
