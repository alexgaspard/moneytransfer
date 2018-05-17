package org.example.moneytransfer.persistence.managers;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.example.moneytransfer.persistence.Query;
import org.example.moneytransfer.persistence.adapters.SQLJDBCAdapter;
import org.example.moneytransfer.persistence.exceptions.DatabaseException;
import org.example.moneytransfer.persistence.exceptions.EntityNotFoundException;
import org.example.moneytransfer.persistence.exceptions.NotImplementedException;
import org.example.moneytransfer.persistence.model.AccountDB;
import org.example.moneytransfer.persistence.model.TransactionDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.moneytransfer.persistence.managers.DBManager.ID_FIELD;
import static org.example.moneytransfer.persistence.managers.TransactionsDBManager.AMOUNT_FIELD;
import static org.example.moneytransfer.persistence.managers.TransactionsDBManager.DATETIME_FIELD;
import static org.example.moneytransfer.persistence.managers.TransactionsDBManager.FROM_ACCOUNT_FIELD;
import static org.example.moneytransfer.persistence.managers.TransactionsDBManager.TO_ACCOUNT_FIELD;
import static org.example.moneytransfer.persistence.managers.TransactionsDBManager.TRANSACTIONS_TABLE;
import static org.example.moneytransfer.persistence.model.TransactionDB.RELATED_ACCOUNT_ID;
import static org.example.moneytransfer.persistence.model.TransactionDB.TRANSACTION_TIME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionsDBManagerTest {
    private SQLJDBCAdapter db;
    private TransactionsDBManager manager;

    @BeforeEach
    void setUp() {
        db = mock(SQLJDBCAdapter.class);
        manager = new TransactionsDBManager(db);
    }

    @Test
    void testGetShouldRead() throws DatabaseException {
        Map<String, String> row = new HashMap<>();
        row.put(ID_FIELD, Integer.toString(0));
        row.put(AMOUNT_FIELD, "0");
        row.put(FROM_ACCOUNT_FIELD, "0");
        row.put(TO_ACCOUNT_FIELD, "0");
        row.put(DATETIME_FIELD, "1526558132214");
        when(db.read(any(Query.class), anyListOf(String.class))).thenReturn(Collections.singletonList(row));
        int id = 1;
        manager.get(id);
        Query query = new Query();
        List<String> fields = Arrays.asList(ID_FIELD, AMOUNT_FIELD, FROM_ACCOUNT_FIELD, TO_ACCOUNT_FIELD, DATETIME_FIELD);
        query.select(TRANSACTIONS_TABLE, fields);
        query.whereAndEquals(Collections.singletonMap(ID_FIELD, Integer.toString(id)));
        verify(db).read(query, fields);
    }

    @Test
    void testGetShouldNotFail() throws DatabaseException {
        int id = 1;
        BigDecimal amount = new BigDecimal(10);
        int fromAccountId = 2;
        int toAccountId = 3;
        Date transactionTime = new Date();
        Map<String, String> row = new HashMap<>();
        row.put(ID_FIELD, Integer.toString(id));
        row.put(AMOUNT_FIELD, amount.toString());
        row.put(FROM_ACCOUNT_FIELD, Integer.toString(fromAccountId));
        row.put(TO_ACCOUNT_FIELD, Integer.toString(toAccountId));
        row.put(DATETIME_FIELD, Long.toString(transactionTime.getTime()));
        when(db.read(any(Query.class), anyListOf(String.class))).thenReturn(Collections.singletonList(row));
        TransactionDB result = manager.get(0);
        assertEquals(id, result.getId());
        assertEquals(amount, result.getAmount());
        assertEquals(fromAccountId, result.getFromAccount().getId());
        assertEquals(toAccountId, result.getToAccount().getId());
        assertEquals(transactionTime, result.getTransactionTime());
    }

    @Test
    void testGetWhenEmptyResultsShouldThrowException() throws DatabaseException {
        when(db.read(any(Query.class), anyListOf(String.class))).thenReturn(Collections.emptyList());
        assertThrows(EntityNotFoundException.class, () -> manager.get(0));
    }

    @Test
    void testGetAllShouldThrowException() {
        assertThrows(NotImplementedException.class, () -> manager.getAll());
    }

    @Test
    void testGetAllWithFiltersShouldRead() throws DatabaseException {
        manager.getAll(Collections.emptyMap(), Collections.emptyList());
        Query query = new Query();
        List<String> fields = Arrays.asList(ID_FIELD, AMOUNT_FIELD, FROM_ACCOUNT_FIELD, TO_ACCOUNT_FIELD, DATETIME_FIELD);
        query.select(TRANSACTIONS_TABLE, fields);
        verify(db).read(query, fields);
    }

    @Test
    void testGetAllWithFiltersWhenRelatedFilterShouldRead() throws DatabaseException {
        String relatedAccountId = "relatedAccountId";
        manager.getAll(Collections.singletonMap(RELATED_ACCOUNT_ID, relatedAccountId), Collections.emptyList());
        Query query = new Query();
        List<String> fields = Arrays.asList(ID_FIELD, AMOUNT_FIELD, FROM_ACCOUNT_FIELD, TO_ACCOUNT_FIELD, DATETIME_FIELD);
        query.select(TRANSACTIONS_TABLE, fields);
        query.whereOrEquals(ImmutableMap.of(FROM_ACCOUNT_FIELD, relatedAccountId, TO_ACCOUNT_FIELD, relatedAccountId));
        verify(db).read(query, fields);
    }

    @Test
    void testGetAllWithFiltersWhenSortsShouldRead() throws DatabaseException {
        manager.getAll(Collections.emptyMap(), Collections.singletonList(TRANSACTION_TIME));
        Query query = new Query();
        List<String> fields = Arrays.asList(ID_FIELD, AMOUNT_FIELD, FROM_ACCOUNT_FIELD, TO_ACCOUNT_FIELD, DATETIME_FIELD);
        query.select(TRANSACTIONS_TABLE, fields);
        query.orderBy(Collections.singletonList(DATETIME_FIELD), true);
        verify(db).read(query, fields);
    }

    @Test
    void testGetAllWithFiltersShouldNotFail() throws DatabaseException {
        int id = 1;
        BigDecimal amount = new BigDecimal(10);
        int fromAccountId = 2;
        int toAccountId = 3;
        Date transactionTime = new Date();
        Map<String, String> row = new HashMap<>();
        row.put(ID_FIELD, Integer.toString(id));
        row.put(AMOUNT_FIELD, amount.toString());
        row.put(FROM_ACCOUNT_FIELD, Integer.toString(fromAccountId));
        row.put(TO_ACCOUNT_FIELD, Integer.toString(toAccountId));
        row.put(DATETIME_FIELD, Long.toString(transactionTime.getTime()));
        when(db.read(any(Query.class), anyListOf(String.class))).thenReturn(Collections.singletonList(row));
        Collection<TransactionDB> results = manager.getAll(Collections.emptyMap(), Collections.emptyList());
        assertEquals(1, results.size());
        TransactionDB transactionDB = results.iterator().next();
        assertEquals(id, transactionDB.getId());
        assertEquals(amount, transactionDB.getAmount());
        assertEquals(fromAccountId, transactionDB.getFromAccount().getId());
        assertEquals(toAccountId, transactionDB.getToAccount().getId());
        assertEquals(transactionTime, transactionDB.getTransactionTime());
    }

    @Test
    void testCreateShouldWrite() throws DatabaseException {
        BigDecimal amount = new BigDecimal(10);
        int fromAccountId = 2;
        int toAccountId = 3;
        Date transactionTime = new Date();
        manager.create(new TransactionDB(1, amount, new AccountDB(fromAccountId), new AccountDB(toAccountId), transactionTime));
        Query query = new Query();
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(AMOUNT_FIELD, amount.toString());
        parameters.put(FROM_ACCOUNT_FIELD, Integer.toString(fromAccountId));
        parameters.put(TO_ACCOUNT_FIELD, Integer.toString(toAccountId));
        parameters.put(DATETIME_FIELD, Long.toString(transactionTime.getTime()));
        query.insert(TRANSACTIONS_TABLE, parameters);
        verify(db).write(query);
    }

    @Test
    void testCreateShouldNotFail() throws DatabaseException {
        int id = 1;
        when(db.write(any(Query.class))).thenReturn(id);
        int result = manager.create(new TransactionDB(1, new BigDecimal(0), new AccountDB(0), new AccountDB(0), new Date()));
        assertEquals(id, result);
    }
}