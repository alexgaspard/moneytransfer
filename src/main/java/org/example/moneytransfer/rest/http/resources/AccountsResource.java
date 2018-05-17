package org.example.moneytransfer.rest.http.resources;

import org.example.moneytransfer.business.exceptions.NotExists;
import org.example.moneytransfer.business.exceptions.UnknownError;
import org.example.moneytransfer.business.managers.AccountsManager;
import org.example.moneytransfer.rest.adapters.ResponseBuilder;
import org.example.moneytransfer.rest.converters.AccountsConverter;
import org.example.moneytransfer.rest.exceptions.HttpException;
import org.example.moneytransfer.rest.exceptions.InternalServerError;
import org.example.moneytransfer.rest.exceptions.NotFound;
import org.example.moneytransfer.rest.model.AccountJSON;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.example.moneytransfer.rest.model.EntityJSON.ID;

@Path(AccountsResource.ACCOUNTS_PATH)
public class AccountsResource {
    static final String ACCOUNTS_PATH = "/accounts";
    static final String ID_PATH = "/{" + ID + "}";
    private AccountsManager manager;
    private AccountsConverter converter;
    private ResponseBuilder responseBuilder;

    @Inject
    AccountsResource(AccountsManager manager, AccountsConverter converter, ResponseBuilder responseBuilder) {
        this.manager = manager;
        this.converter = converter;
        this.responseBuilder = responseBuilder;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post() throws HttpException {
        try {
            int resourceId = manager.createAccount();
            return responseBuilder.buildCreated(ACCOUNTS_PATH, Integer.toString(resourceId));
        } catch (UnknownError e) {
            throw new InternalServerError(e.getMessage());
        }
    }

    @GET
    @Path(ID_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public AccountJSON get(@PathParam(ID) int id) throws HttpException {
        try {
            return converter.convertToJSON(manager.getAccount(id));
        } catch (NotExists e) {
            throw new NotFound(e.getMessage());
        } catch (UnknownError e) {
            throw new InternalServerError(e.getMessage());
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<AccountJSON> getAll() throws HttpException {
        try {
            return manager.getAccounts().stream().map(converter::convertToJSON).collect(Collectors.toList());
        } catch (UnknownError e) {
            throw new InternalServerError(e.getMessage());
        }
    }
}