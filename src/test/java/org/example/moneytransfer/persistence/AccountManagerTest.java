package org.example.moneytransfer.persistence;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.example.moneytransfer.persistence.model.Account;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountManagerTest {
    private SQLAdapter sqlAdapter;
    private AccountManager manager;

    @Before
    public void setUp() throws Exception {
        sqlAdapter = mock(SQLAdapter.class);
        manager = new AccountManager(sqlAdapter);
    }

    @Test
    public void testCreateShouldNotFail() throws SQLException {
        int id = 1;
        BigDecimal balance = new BigDecimal(3.14);
        manager.create(new Account(id, balance));
        verify(sqlAdapter, times(1)).insert(ImmutableMap.of("id", Integer.toString(id), "balance", balance.toString()));
    }

    @Test
    public void testGetAllShouldSelectAll() throws SQLException {
        manager.getAll();
        verify(sqlAdapter, times(1)).selectAll();
    }

    @Test
    public void testGetAllShouldNotFail() throws SQLException {
        String id = "1";
        String balance = "3.14";
        when(sqlAdapter.selectAll()).thenReturn(Collections.singletonList(ImmutableMap.of("id", id, "balance", balance)));
        List<Account> accounts = manager.getAll();
        assertEquals(1, accounts.size());
        assertEquals(Integer.parseInt(id), accounts.get(0).getId());
        assertEquals(new BigDecimal(balance), accounts.get(0).getBalance());
    }
}