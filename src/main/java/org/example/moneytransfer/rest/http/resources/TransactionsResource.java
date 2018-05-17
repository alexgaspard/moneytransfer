package org.example.moneytransfer.rest.http.resources;

import org.example.moneytransfer.business.exceptions.MaximumOverdraftReached;
import org.example.moneytransfer.business.exceptions.NotExists;
import org.example.moneytransfer.business.exceptions.UnknownError;
import org.example.moneytransfer.business.managers.AccountsManager;
import org.example.moneytransfer.rest.adapters.ResponseBuilder;
import org.example.moneytransfer.rest.converters.TransactionsConverter;
import org.example.moneytransfer.rest.exceptions.BadRequest;
import org.example.moneytransfer.rest.exceptions.HttpException;
import org.example.moneytransfer.rest.exceptions.InternalServerError;
import org.example.moneytransfer.rest.exceptions.NotFound;
import org.example.moneytransfer.rest.model.TransactionJSON;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.example.moneytransfer.rest.http.resources.AccountsResource.ACCOUNTS_PATH;
import static org.example.moneytransfer.rest.http.resources.AccountsResource.ID_PATH;
import static org.example.moneytransfer.rest.model.EntityJSON.ID;

@Path(TransactionsResource.TRANSACTIONS_PATH)
public class TransactionsResource {
    static final String TRANSACTIONS_PATH = ACCOUNTS_PATH + ID_PATH + "/transactions";
    private static final String ID2 = ID + "2";
    private static final String ID2_PATH = "/{" + ID2 + "}";
    private AccountsManager manager;
    private TransactionsConverter converter;
    private ResponseBuilder responseBuilder;

    @Inject
    TransactionsResource(AccountsManager manager, TransactionsConverter converter, ResponseBuilder responseBuilder) {
        this.manager = manager;
        this.converter = converter;
        this.responseBuilder = responseBuilder;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(@PathParam(ID) int id, TransactionJSON json) throws HttpException {
        if (json == null) {
            throw new BadRequest("Missing body");
        }
        try {
            int resourceId = manager.createTransaction(converter.convertToBusiness(json), id);
            return responseBuilder.buildCreated(TRANSACTIONS_PATH.replace("{" + ID + "}", Integer.toString(id)), Integer.toString(resourceId));
        } catch (NotExists e) {
            throw new NotFound(e.getMessage());
        } catch (MaximumOverdraftReached e) {
            throw new BadRequest(e.getMessage());
        } catch (UnknownError e) {
            throw new InternalServerError(e.getMessage());
        }
    }

    @GET
    @Path(ID2_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionJSON get(@PathParam(ID) int id, @PathParam(ID2) int id2) throws HttpException {
        try {
            return converter.convertToJSON(manager.getTransaction(id2, id));
        } catch (NotExists e) {
            throw new NotFound(e.getMessage());
        } catch (UnknownError e) {
            throw new InternalServerError(e.getMessage());
        }
    }
}