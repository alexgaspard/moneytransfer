package org.example.persistence;

import org.example.persistence.model.Account;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PersistenceIT {
    private AccountManager manager;

    @Before
    public void setUp() throws Exception {
        SQLAdapter sqlAdapter = new SQLAdapter();
        manager = new AccountManager(sqlAdapter);
    }

    @Test
    public void testCreateShouldNotFail() throws SQLException {
        List<Account> emptyAccounts = manager.getAll();
        assertEquals(0, emptyAccounts.size());
        BigDecimal balance = new BigDecimal((float) 3.14);
        manager.create(new Account(0, balance));
        List<Account> accounts = manager.getAll();
        assertEquals(1, accounts.size());
        assertEquals(1, accounts.get(0).getId());
        assertEquals(balance, accounts.get(0).getBalance());
    }
}