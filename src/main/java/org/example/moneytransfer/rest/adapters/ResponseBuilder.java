package org.example.moneytransfer.rest.adapters;

import javax.ws.rs.core.Response;
import java.net.URI;

public class ResponseBuilder {

    public Response buildCreated(String resourcePath, String resourceUuid) {
        return Response.created(URI.create("/api" + resourcePath + "/" + resourceUuid)).build();
    }

    public Response buildNoContent() {
        return Response.noContent().build();
    }
}
