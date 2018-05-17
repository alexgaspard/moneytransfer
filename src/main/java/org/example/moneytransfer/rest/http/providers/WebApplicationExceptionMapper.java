package org.example.moneytransfer.rest.http.providers;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    @Override
    public Response toResponse(WebApplicationException exception) {
        Response.StatusType statusInfo = exception.getResponse().getStatusInfo();
        return Response.status(statusInfo.getStatusCode()).entity(new ErrorMessage(statusInfo.getReasonPhrase())).type(MediaType.APPLICATION_JSON).build();
    }
}