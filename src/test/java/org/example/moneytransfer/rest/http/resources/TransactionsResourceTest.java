package org.example.moneytransfer.rest.http.resources;

import org.example.moneytransfer.business.exceptions.MaximumOverdraftReached;
import org.example.moneytransfer.business.exceptions.NotExists;
import org.example.moneytransfer.business.exceptions.UnknownError;
import org.example.moneytransfer.business.managers.AccountsManager;
import org.example.moneytransfer.business.model.Transaction;
import org.example.moneytransfer.business.model.TransactionType;
import org.example.moneytransfer.rest.adapters.ResponseBuilder;
import org.example.moneytransfer.rest.converters.TransactionsConverter;
import org.example.moneytransfer.rest.exceptions.BadRequest;
import org.example.moneytransfer.rest.exceptions.HttpException;
import org.example.moneytransfer.rest.exceptions.InternalServerError;
import org.example.moneytransfer.rest.exceptions.NotFound;
import org.example.moneytransfer.rest.model.TransactionJSON;
import org.example.moneytransfer.rest.model.TransactionTypeJSON;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.example.moneytransfer.rest.http.resources.TransactionsResource.TRANSACTIONS_PATH;
import static org.example.moneytransfer.rest.model.EntityJSON.ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionsResourceTest {

    private AccountsManager manager;
    private TransactionsConverter converter;
    private ResponseBuilder responseBuilder;
    private TransactionsResource resource;

    @BeforeEach
    void setUp() {
        manager = mock(AccountsManager.class);
        converter = mock(TransactionsConverter.class);
        responseBuilder = mock(ResponseBuilder.class);
        resource = new TransactionsResource(manager, converter, responseBuilder);
    }

    @Test
    void testGetWhenUnknownErrorShouldThrowException() throws UnknownError {
        when(manager.getTransaction(anyInt(), anyInt())).thenThrow(new UnknownError(""));
        assertThrows(InternalServerError.class, () -> resource.get(0, 0));
    }

    @Test
    void testGetWhenNotExistsShouldThrowException() throws UnknownError {
        when(manager.getTransaction(anyInt(), anyInt())).thenThrow(new NotExists(""));
        assertThrows(NotFound.class, () -> resource.get(0, 0));
    }

    @Test
    void testGetShouldNotFail() throws HttpException {
        TransactionJSON transaction = new TransactionJSON(0, null, null, 0, null);
        when(converter.convertToJSON(any(Transaction.class))).thenReturn(transaction);
        TransactionJSON result = resource.get(0, 0);
        assertEquals(transaction, result);
    }

    @Test
    void testGetShouldConvert() throws HttpException, UnknownError {
        Transaction transaction = new Transaction(0, null, null, 0, null);
        when(manager.getTransaction(anyInt(), anyInt())).thenReturn(transaction);
        resource.get(0, 0);
        verify(converter).convertToJSON(transaction);
    }

    @Test
    void testGetShouldGetTransaction() throws HttpException, UnknownError {
        int id = 1;
        int id2 = 2;
        resource.get(id, id2);
        verify(manager).getTransaction(id2, id);
    }

    @Test
    void testPostShouldBuildCreated() throws HttpException, UnknownError {
        int resourceId = 2;
        when(manager.createTransaction(any(Transaction.class), anyInt())).thenReturn(resourceId);
        int id = 1;
        resource.post(id, new TransactionJSON(id, new BigDecimal(0), TransactionTypeJSON.DEPOSIT, 0, new Date()));
        verify(responseBuilder).buildCreated(TRANSACTIONS_PATH.replace("{" + ID + "}", Integer.toString(id)), Integer.toString(resourceId));
    }

    @Test
    void testPostShouldCreateTransaction() throws HttpException, UnknownError {
        Transaction transaction = new Transaction(1, new BigDecimal(1), TransactionType.DEPOSIT, 2, new Date());
        when(converter.convertToBusiness(any(TransactionJSON.class))).thenReturn(transaction);
        int accountId = 2;
        resource.post(accountId, new TransactionJSON(0, new BigDecimal(0), TransactionTypeJSON.DEPOSIT, 0, new Date()));
        verify(manager).createTransaction(transaction, accountId);
    }

    @Test
    void testPostShouldConvert() throws HttpException {
        TransactionJSON json = new TransactionJSON(1, new BigDecimal(1), TransactionTypeJSON.DEPOSIT, 2, new Date());
        resource.post(0, json);
        verify(converter).convertToBusiness(json);
    }

    @Test
    void testPostWhenOverdraftLimitReachedShouldThrowException() throws UnknownError {
        when(manager.createTransaction(any(), anyInt())).thenThrow(new MaximumOverdraftReached(""));
        assertThrows(BadRequest.class, () -> resource.post(0, new TransactionJSON(0, null, null, 0, null)));
    }

    @Test
    void testPostWhenUnknownErrorShouldThrowException() throws UnknownError {
        when(manager.createTransaction(any(), anyInt())).thenThrow(new UnknownError(""));
        assertThrows(InternalServerError.class, () -> resource.post(0, new TransactionJSON(0, new BigDecimal(0), null, 0, null)));
    }

    @Test
    void testPostWhenNotExistsShouldThrowException() throws UnknownError {
        when(manager.createTransaction(any(), anyInt())).thenThrow(new NotExists(""));
        assertThrows(NotFound.class, () -> resource.post(0, new TransactionJSON(0, new BigDecimal(0), null, 0, null)));
    }

    @Test
    void testPostWhenNullBodyShouldThrowException() {
        assertThrows(BadRequest.class, () -> resource.post(0, null));
    }

    @Test
    void testPostWhenNullAmountShouldThrowException() {
        assertThrows(BadRequest.class, () -> resource.post(0, new TransactionJSON(0, null, null, 0, null)));
    }

    @Test
    void testPostWhenNegativeAmountShouldThrowException() {
        assertThrows(BadRequest.class, () -> resource.post(0, new TransactionJSON(0, new BigDecimal(-1), null, 0, null)));
    }
}