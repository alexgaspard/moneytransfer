package org.example.moneytransfer.persistence.managers;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.example.moneytransfer.persistence.Query;
import org.example.moneytransfer.persistence.adapters.SQLJDBCAdapter;
import org.example.moneytransfer.persistence.exceptions.DatabaseException;
import org.example.moneytransfer.persistence.exceptions.EntityNotFoundException;
import org.example.moneytransfer.persistence.exceptions.NotImplementedException;
import org.example.moneytransfer.persistence.model.AccountDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.example.moneytransfer.persistence.managers.AccountsDBManager.ACCOUNTS_TABLE;
import static org.example.moneytransfer.persistence.managers.DBManager.ID_FIELD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountsDBManagerTest {
    private SQLJDBCAdapter db;
    private AccountsDBManager manager;

    @BeforeEach
    void setUp() {
        db = mock(SQLJDBCAdapter.class);
        manager = new AccountsDBManager(db);
    }

    @Test
    void testGetShouldRead() throws DatabaseException {
        ArrayList<Map<String, String>> rows = new ArrayList<>();
        rows.add(ImmutableMap.of(ID_FIELD, Integer.toString(0)));
        when(db.read(any(Query.class), anyListOf(String.class))).thenReturn(rows);
        int id = 1;
        manager.get(id);
        Query query = new Query();
        List<String> fields = Collections.singletonList(ID_FIELD);
        query.select(ACCOUNTS_TABLE, fields);
        query.whereAndEquals(Collections.singletonMap(ID_FIELD, Integer.toString(id)));
        verify(db).read(query, fields);
    }

    @Test
    void testGetShouldNotFail() throws DatabaseException {
        int id = 1;
        ArrayList<Map<String, String>> rows = new ArrayList<>();
        rows.add(ImmutableMap.of(ID_FIELD, Integer.toString(id)));
        when(db.read(any(Query.class), anyListOf(String.class))).thenReturn(rows);
        AccountDB result = manager.get(0);
        assertEquals(id, result.getId());
    }

    @Test
    void testGetWhenEmptyResultsShouldThrowException() throws DatabaseException {
        when(db.read(any(Query.class), anyListOf(String.class))).thenReturn(Collections.emptyList());
        assertThrows(EntityNotFoundException.class, () -> manager.get(0));
    }

    @Test
    void testGetAllWithFiltersShouldThrowException() {
        assertThrows(NotImplementedException.class, () -> manager.getAll(Collections.emptyMap(), Collections.emptyList()));
    }

    @Test
    void testGetAllShouldRead() throws DatabaseException {
        manager.getAll();
        Query query = new Query();
        List<String> fields = Collections.singletonList(ID_FIELD);
        query.select(ACCOUNTS_TABLE, fields);
        verify(db).read(query, fields);
    }

    @Test
    void testGetAllShouldNotFail() throws DatabaseException {
        int id = 1;
        ArrayList<Map<String, String>> rows = new ArrayList<>();
        rows.add(ImmutableMap.of(ID_FIELD, Integer.toString(id)));
        when(db.read(any(Query.class), anyListOf(String.class))).thenReturn(rows);
        Collection<AccountDB> results = manager.getAll();
        assertEquals(1, results.size());
        assertEquals(id, results.iterator().next().getId());
    }

    @Test
    void testCreateShouldInsert() throws DatabaseException {
        manager.create(new AccountDB(0));
        Query query = new Query();
        query.insert(ACCOUNTS_TABLE, Collections.emptyMap());
        verify(db).write(query);
    }

    @Test
    void testCreateShouldNotFail() throws DatabaseException {
        int id = 1;
        when(db.write(any(Query.class))).thenReturn(id);
        int result = manager.create(new AccountDB(0));
        assertEquals(id, result);
    }
}