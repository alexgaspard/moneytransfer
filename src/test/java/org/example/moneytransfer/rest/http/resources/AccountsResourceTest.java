package org.example.moneytransfer.rest.http.resources;

import org.example.moneytransfer.business.exceptions.NotExists;
import org.example.moneytransfer.business.exceptions.UnknownError;
import org.example.moneytransfer.business.managers.AccountsManager;
import org.example.moneytransfer.business.model.Account;
import org.example.moneytransfer.rest.adapters.ResponseBuilder;
import org.example.moneytransfer.rest.converters.AccountsConverter;
import org.example.moneytransfer.rest.exceptions.HttpException;
import org.example.moneytransfer.rest.exceptions.InternalServerError;
import org.example.moneytransfer.rest.exceptions.NotFound;
import org.example.moneytransfer.rest.model.AccountJSON;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

import static org.example.moneytransfer.rest.http.resources.AccountsResource.ACCOUNTS_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountsResourceTest {

    private AccountsManager manager;
    private AccountsConverter converter;
    private ResponseBuilder responseBuilder;
    private AccountsResource resource;

    @BeforeEach
    void setUp() {
        manager = mock(AccountsManager.class);
        converter = mock(AccountsConverter.class);
        responseBuilder = mock(ResponseBuilder.class);
        resource = new AccountsResource(manager, converter, responseBuilder);
    }

    @Test
    void testGetWhenUnknownErrorShouldThrowException() throws UnknownError {
        when(manager.getAccount(anyInt())).thenThrow(new UnknownError(""));
        assertThrows(InternalServerError.class, () -> resource.get(0));
    }

    @Test
    void testGetWhenNotExistsShouldThrowException() throws UnknownError {
        when(manager.getAccount(anyInt())).thenThrow(new NotExists(""));
        assertThrows(NotFound.class, () -> resource.get(0));
    }

    @Test
    void testGetShouldNotFail() throws HttpException {
        AccountJSON account = new AccountJSON(0, null, Collections.emptyList());
        when(converter.convertToJSON(any())).thenReturn(account);
        AccountJSON result = resource.get(0);
        assertEquals(account, result);
    }

    @Test
    void testGetShouldConvert() throws HttpException, UnknownError {
        Account account = new Account(0, null, Collections.emptyList());
        when(manager.getAccount(anyInt())).thenReturn(account);
        resource.get(0);
        verify(converter).convertToJSON(account);
    }

    @Test
    void testGetShouldGetAccount() throws HttpException, UnknownError {
        int id = 1;
        resource.get(id);
        verify(manager).getAccount(id);
    }

    @Test
    void testPostShouldBuildCreated() throws HttpException, UnknownError {
        int resourceId = 2;
        when(manager.createAccount()).thenReturn(resourceId);
        resource.post();
        verify(responseBuilder).buildCreated(ACCOUNTS_PATH, Integer.toString(resourceId));
    }

    @Test
    void testPostShouldCreateAccount() throws HttpException, UnknownError {
        resource.post();
        verify(manager).createAccount();
    }

    @Test
    void testPostWhenUnknownErrorShouldThrowException() throws UnknownError {
        when(manager.createAccount()).thenThrow(new UnknownError(""));
        assertThrows(InternalServerError.class, () -> resource.post());
    }

    @Test
    void testGetAllWhenUnknownErrorShouldThrowException() throws UnknownError {
        when(manager.getAccounts()).thenThrow(new UnknownError(""));
        assertThrows(InternalServerError.class, () -> resource.getAll());
    }

    @Test
    void testGetAllShouldNotFail() throws HttpException, UnknownError {
        when(manager.getAccounts()).thenReturn(Collections.singletonList(new Account(0, new BigDecimal(0), Collections.emptyList())));
        AccountJSON account = new AccountJSON(0, null, Collections.emptyList());
        when(converter.convertToJSON(any())).thenReturn(account);
        Collection<AccountJSON> results = resource.getAll();
        assertEquals(Collections.singletonList(account), results);
    }

    @Test
    void testGetAllShouldConvert() throws HttpException, UnknownError {
        Account account = new Account(0, null, Collections.emptyList());
        when(manager.getAccounts()).thenReturn(Collections.singletonList(account));
        resource.getAll();
        verify(converter).convertToJSON(account);
    }

    @Test
    void testGetAllShouldGetAccount() throws HttpException, UnknownError {
        resource.getAll();
        verify(manager).getAccounts();
    }
}