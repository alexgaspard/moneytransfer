package org.example.moneytransfer.rest.http.providers;

import org.example.moneytransfer.rest.exceptions.HttpException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class HttpExceptionMapper implements ExceptionMapper<HttpException> {

    @Override
    public Response toResponse(HttpException exception) {
        return Response.status(exception.getCode()).entity(new ErrorMessage(exception.getMessage())).type(MediaType.APPLICATION_JSON).build();
    }
}