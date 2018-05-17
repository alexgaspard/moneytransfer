package org.example.moneytransfer.rest.exceptions;

import javax.ws.rs.core.Response;


public class InternalServerError extends HttpException {

    public InternalServerError(String message) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), message);
    }
}
