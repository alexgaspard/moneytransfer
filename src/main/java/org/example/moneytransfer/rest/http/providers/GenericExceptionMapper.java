package org.example.moneytransfer.rest.http.providers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(new ErrorMessage(exception.getMessage())).type(MediaType.APPLICATION_JSON).build();
    }
}