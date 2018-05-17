package org.example.moneytransfer.business.managers;

import jersey.repackaged.com.google.common.collect.ImmutableList;
import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.example.moneytransfer.business.converters.AccountsDBConverter;
import org.example.moneytransfer.business.converters.TransactionsDBConverter;
import org.example.moneytransfer.business.exceptions.MaximumOverdraftReached;
import org.example.moneytransfer.business.exceptions.NotExists;
import org.example.moneytransfer.business.exceptions.UnknownError;
import org.example.moneytransfer.business.model.Account;
import org.example.moneytransfer.business.model.Transaction;
import org.example.moneytransfer.business.model.TransactionType;
import org.example.moneytransfer.persistence.exceptions.ConnectionException;
import org.example.moneytransfer.persistence.exceptions.DatabaseException;
import org.example.moneytransfer.persistence.exceptions.EntityNotFoundException;
import org.example.moneytransfer.persistence.managers.AccountsDBManager;
import org.example.moneytransfer.persistence.managers.TransactionsDBManager;
import org.example.moneytransfer.persistence.model.AccountDB;
import org.example.moneytransfer.persistence.model.TransactionDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.example.moneytransfer.persistence.model.TransactionDB.RELATED_ACCOUNT_ID;
import static org.example.moneytransfer.persistence.model.TransactionDB.TRANSACTION_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountsManagerTest {
    private AccountsDBManager accountsDBManager;
    private TransactionsDBManager transactionsDBManager;
    private AccountsDBConverter accountsDBConverter;
    private TransactionsDBConverter transactionsDBConverter;
    private AccountsManager manager;

    @BeforeEach
    void setUp() {
        accountsDBManager = mock(AccountsDBManager.class);
        transactionsDBManager = mock(TransactionsDBManager.class);
        accountsDBConverter = mock(AccountsDBConverter.class);
        transactionsDBConverter = mock(TransactionsDBConverter.class);
        manager = new AccountsManager(accountsDBManager, transactionsDBManager, accountsDBConverter, transactionsDBConverter);
    }

    @Test
    void testCreateAccountShouldCreate() throws UnknownError, DatabaseException {
        manager.createAccount();
        verify(accountsDBManager).create(new AccountDB(0));
    }

    @Test
    void testCreateAccountShouldNotFail() throws UnknownError, DatabaseException {
        int id = 1;
        when(accountsDBManager.create(any())).thenReturn(id);
        assertEquals(id, manager.createAccount());
    }

    @Test
    void testCreateAccountWhenDatabaseExceptionShouldThrowException() throws DatabaseException {
        when(accountsDBManager.create(any())).thenThrow(new ConnectionException(""));
        assertThrows(UnknownError.class, () -> manager.createAccount());
    }

    @Test
    void testGetAccountsShouldGetAll() throws UnknownError, DatabaseException {
        manager.getAccounts();
        verify(accountsDBManager).getAll();
    }

    @Test
    void testGetAccountsShouldGetAllTransactions() throws UnknownError, DatabaseException {
        int id = 1;
        when(accountsDBManager.getAll()).thenReturn(Collections.singletonList(new AccountDB(id)));
        manager.getAccounts();
        verify(transactionsDBManager).getAll(ImmutableMap.of(RELATED_ACCOUNT_ID, Integer.toString(id)), ImmutableList.of(TRANSACTION_TIME));
    }

    @Test
    void testGetAccountsShouldConvertToBusiness() throws UnknownError, DatabaseException {
        AccountDB accountDB = new AccountDB(2);
        when(accountsDBManager.getAll()).thenReturn(Collections.singletonList(accountDB));
        TransactionDB transactionDB = new TransactionDB(3, new BigDecimal(10), new AccountDB(4), new AccountDB(5), new Date());
        TransactionDB transactionDB2 = new TransactionDB(4, new BigDecimal(20), new AccountDB(6), new AccountDB(7), new Date());
        List<TransactionDB> transactionDBS = Arrays.asList(transactionDB, transactionDB2);
        when(transactionsDBManager.getAll(anyMapOf(String.class, String.class), anyListOf(String.class))).thenReturn(transactionDBS);
        manager.getAccounts();
        verify(accountsDBConverter).convertToBusiness(accountDB, transactionDBS);
    }

    @Test
    void testGetAccountsShouldNotFail() throws UnknownError, DatabaseException {
        when(accountsDBManager.getAll()).thenReturn(Collections.singletonList(new AccountDB(0)));
        Account account = new Account(0, new BigDecimal(0), Collections.emptyList());
        when(accountsDBConverter.convertToBusiness(any(), any())).thenReturn(account);
        Collection<Account> results = manager.getAccounts();
        assertEquals(Collections.singletonList(account), results);
    }

    @Test
    void testGetAccountsWhenDatabaseExceptionShouldThrowException() throws DatabaseException {
        when(accountsDBManager.getAll()).thenThrow(new ConnectionException(""));
        assertThrows(UnknownError.class, () -> manager.getAccounts());
    }

    @Test
    void testGetAccountShouldGet() throws UnknownError, DatabaseException {
        int id = 1;
        manager.getAccount(id);
        verify(accountsDBManager).get(id);
    }

    @Test
    void testGetAccountShouldGetAll() throws UnknownError, DatabaseException {
        int id = 1;
        manager.getAccount(id);
        verify(transactionsDBManager).getAll(ImmutableMap.of(RELATED_ACCOUNT_ID, Integer.toString(id)), ImmutableList.of(TRANSACTION_TIME));
    }

    @Test
    void testGetAccountShouldConvertToBusiness() throws UnknownError, DatabaseException {
        AccountDB accountDB = new AccountDB(2);
        when(accountsDBManager.get(anyInt())).thenReturn(accountDB);
        TransactionDB transactionDB = new TransactionDB(3, new BigDecimal(10), new AccountDB(4), new AccountDB(5), new Date());
        TransactionDB transactionDB2 = new TransactionDB(4, new BigDecimal(20), new AccountDB(6), new AccountDB(7), new Date());
        List<TransactionDB> transactionDBS = Arrays.asList(transactionDB, transactionDB2);
        when(transactionsDBManager.getAll(anyMapOf(String.class, String.class), anyListOf(String.class))).thenReturn(transactionDBS);
        manager.getAccount(0);
        verify(accountsDBConverter).convertToBusiness(accountDB, transactionDBS);
    }

    @Test
    void testGetAccountShouldNotFail() throws UnknownError, DatabaseException {
        when(accountsDBManager.get(anyInt())).thenReturn(new AccountDB(0));
        Account account = new Account(0, new BigDecimal(0), Collections.emptyList());
        when(accountsDBConverter.convertToBusiness(any(), any())).thenReturn(account);
        Account result = manager.getAccount(0);
        assertEquals(account, result);
    }

    @Test
    void testGetAccountWhenNotFoundShouldThrowException() throws DatabaseException {
        when(accountsDBManager.get(anyInt())).thenThrow(new EntityNotFoundException(""));
        assertThrows(NotExists.class, () -> manager.getAccount(0));
    }

    @Test
    void testGetAccountWhenDatabaseExceptionShouldThrowException() throws DatabaseException {
        when(accountsDBManager.get(anyInt())).thenThrow(new ConnectionException(""));
        assertThrows(UnknownError.class, () -> manager.getAccount(0));
    }

    @Test
    void testCreateTransactionShouldGetFirstAccount() throws UnknownError, DatabaseException {
        when(transactionsDBConverter.convertToDB(anyInt(), any())).thenReturn(new TransactionDB(0, new BigDecimal(0), new AccountDB(0), new AccountDB(0), null));
        when(accountsDBConverter.convertToBusiness(any(), anyListOf(TransactionDB.class))).thenReturn(new Account(0, new BigDecimal(0), Collections.emptyList()));
        int id = 1;
        manager.createTransaction(new Transaction(2, new BigDecimal(10), TransactionType.DEPOSIT, 3, new Date()), id);
        verify(accountsDBManager).get(id);
    }

    @Test
    void testCreateTransactionShouldGetOtherAccount() throws UnknownError, DatabaseException {
        when(transactionsDBConverter.convertToDB(anyInt(), any())).thenReturn(new TransactionDB(0, new BigDecimal(0), new AccountDB(0), new AccountDB(0), null));
        when(accountsDBConverter.convertToBusiness(any(), anyListOf(TransactionDB.class))).thenReturn(new Account(0, new BigDecimal(0), Collections.emptyList()));
        int otherAccountId = 3;
        manager.createTransaction(new Transaction(2, new BigDecimal(10), TransactionType.DEPOSIT, otherAccountId, new Date()), 1);
        verify(accountsDBManager).get(otherAccountId);
    }

    @Test
    void testCreateTransactionWhenNotFoundShouldThrowException() throws DatabaseException {
        when(accountsDBManager.get(anyInt())).thenThrow(new EntityNotFoundException(""));
        assertThrows(NotExists.class, () -> manager.createTransaction(null, 0));
    }

    @Test
    void testCreateTransactionWhenDatabaseExceptionShouldThrowException() throws DatabaseException {
        when(accountsDBManager.get(anyInt())).thenThrow(new ConnectionException(""));
        assertThrows(UnknownError.class, () -> manager.createTransaction(null, 0));
    }

    @Test
    void testCreateTransactionShouldConvertToDB() throws UnknownError {
        when(transactionsDBConverter.convertToDB(anyInt(), any())).thenReturn(new TransactionDB(0, new BigDecimal(0), new AccountDB(0), new AccountDB(0), null));
        when(accountsDBConverter.convertToBusiness(any(), anyListOf(TransactionDB.class))).thenReturn(new Account(0, new BigDecimal(0), Collections.emptyList()));
        Transaction transaction = new Transaction(2, new BigDecimal(10), TransactionType.DEPOSIT, 0, new Date());
        int accountId = 1;
        manager.createTransaction(transaction, accountId);
        verify(transactionsDBConverter).convertToDB(accountId, transaction);
    }

    @Test
    void testCreateTransactionWhenMaximumOverdraftReachedShouldThrowException() {
        when(transactionsDBConverter.convertToDB(anyInt(), any())).thenReturn(new TransactionDB(0, new BigDecimal(0), new AccountDB(0), new AccountDB(0), null));
        when(accountsDBConverter.convertToBusiness(any(), anyListOf(TransactionDB.class))).thenReturn(new Account(0, new BigDecimal(-90), Collections.emptyList()));
        assertThrows(MaximumOverdraftReached.class, () -> manager.createTransaction(new Transaction(2, new BigDecimal(20), TransactionType.DEPOSIT, 0, new Date()), 1));
    }

    @Test
    void testCreateTransactionShouldCreate() throws UnknownError, DatabaseException {
        TransactionDB transactionDB = new TransactionDB(0, new BigDecimal(0), new AccountDB(0), new AccountDB(0), null);
        when(transactionsDBConverter.convertToDB(anyInt(), any())).thenReturn(transactionDB);
        when(accountsDBConverter.convertToBusiness(any(), anyListOf(TransactionDB.class))).thenReturn(new Account(0, new BigDecimal(0), Collections.emptyList()));
        manager.createTransaction(new Transaction(2, new BigDecimal(10), TransactionType.DEPOSIT, 0, new Date()), 1);
        verify(transactionsDBManager).create(transactionDB);
    }

    @Test
    void testCreateTransactionShouldNotFail() throws UnknownError, DatabaseException {
        when(transactionsDBConverter.convertToDB(anyInt(), any())).thenReturn(new TransactionDB(0, new BigDecimal(0), new AccountDB(0), new AccountDB(0), null));
        when(accountsDBConverter.convertToBusiness(any(), anyListOf(TransactionDB.class))).thenReturn(new Account(0, new BigDecimal(0), Collections.emptyList()));
        int id = 1;
        when(transactionsDBManager.create(any())).thenReturn(id);
        assertEquals(id, manager.createTransaction(new Transaction(0, new BigDecimal(10), TransactionType.DEPOSIT, 0, new Date()), 0));
    }

    @Test
    void testCreateTransactionWhenDatabaseExceptionOnCreateShouldThrowException() throws DatabaseException {
        when(transactionsDBConverter.convertToDB(anyInt(), any())).thenReturn(new TransactionDB(0, new BigDecimal(0), new AccountDB(0), new AccountDB(0), null));
        when(accountsDBConverter.convertToBusiness(any(), anyListOf(TransactionDB.class))).thenReturn(new Account(0, new BigDecimal(-90), Collections.emptyList()));
        when(transactionsDBManager.create(any())).thenThrow(new ConnectionException(""));
        assertThrows(UnknownError.class, () -> manager.createTransaction(new Transaction(0, new BigDecimal(20), TransactionType.DEPOSIT, 0, new Date()), 0));
    }

    @Test
    void testGetTransactionShouldGet() throws UnknownError, DatabaseException {
        int accountId = 1;
        when(transactionsDBManager.get(anyInt())).thenReturn(new TransactionDB(0, new BigDecimal(0), new AccountDB(accountId), new AccountDB(0), null));
        int id = 2;
        manager.getTransaction(id, accountId);
        verify(transactionsDBManager).get(id);
    }

    @Test
    void testGetTransactionWhenNotFromAccountShouldThrowException() throws DatabaseException {
        when(transactionsDBManager.get(anyInt())).thenReturn(new TransactionDB(0, new BigDecimal(0), new AccountDB(0), new AccountDB(0), null));
        assertThrows(NotExists.class, () -> manager.getTransaction(0, 1));
    }

    @Test
    void testGetTransactionShouldConvertToBusiness() throws UnknownError, DatabaseException {
        int accountId = 1;
        TransactionDB transactionDB = new TransactionDB(0, new BigDecimal(0), new AccountDB(accountId), new AccountDB(0), null);
        when(transactionsDBManager.get(anyInt())).thenReturn(transactionDB);
        manager.getTransaction(2, accountId);
        verify(transactionsDBConverter).convertToBusiness(accountId, transactionDB);
    }

    @Test
    void testGetTransactionShouldNotFail() throws UnknownError, DatabaseException {
        int accountId = 1;
        when(transactionsDBManager.get(anyInt())).thenReturn(new TransactionDB(0, new BigDecimal(0), new AccountDB(accountId), new AccountDB(0), null));
        Transaction transaction = new Transaction(0, new BigDecimal(0), TransactionType.DEPOSIT, 0, new Date());
        when(transactionsDBConverter.convertToBusiness(anyInt(), any())).thenReturn(transaction);
        Transaction result = manager.getTransaction(2, accountId);
        assertEquals(transaction, result);
    }
}