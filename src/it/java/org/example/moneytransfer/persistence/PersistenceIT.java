package org.example.moneytransfer.persistence;

import org.example.moneytransfer.persistence.adapters.SQLJDBCAdapter;
import org.example.moneytransfer.persistence.exceptions.DatabaseException;
import org.example.moneytransfer.persistence.exceptions.NoDataModificationException;
import org.example.moneytransfer.persistence.managers.AccountsDBManager;
import org.example.moneytransfer.persistence.model.AccountDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.example.moneytransfer.persistence.managers.AccountsDBManager.ACCOUNTS_TABLE;
import static org.example.moneytransfer.persistence.managers.DBManager.ID_FIELD;
import static org.example.moneytransfer.persistence.managers.TransactionsDBManager.AMOUNT_FIELD;
import static org.example.moneytransfer.persistence.managers.TransactionsDBManager.DATETIME_FIELD;
import static org.example.moneytransfer.persistence.managers.TransactionsDBManager.FROM_ACCOUNT_FIELD;
import static org.example.moneytransfer.persistence.managers.TransactionsDBManager.TO_ACCOUNT_FIELD;
import static org.example.moneytransfer.persistence.managers.TransactionsDBManager.TRANSACTIONS_TABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PersistenceIT {
    private AccountsDBManager manager;

    @BeforeEach
    void setUp() {
        SQLJDBCAdapter sqlAdapter = new SQLJDBCAdapter();
        try {
            Query createAccountsTableQuery = new Query();
            createAccountsTableQuery.createTable(ACCOUNTS_TABLE, Collections.singletonList(ID_FIELD + " int primary key auto_increment"));
            Query createTransactionsTableQuery = new Query();
            createTransactionsTableQuery.createTable(TRANSACTIONS_TABLE,
                    Arrays.asList(ID_FIELD + " int primary key auto_increment",
                            AMOUNT_FIELD + " decimal not null",
                            FROM_ACCOUNT_FIELD + " integer not null",
                            TO_ACCOUNT_FIELD + " integer not null",
                            DATETIME_FIELD + " bigint not null",
                            "foreign key(" + FROM_ACCOUNT_FIELD + ") references " + ACCOUNTS_TABLE + "(" + ID_FIELD + ")",
                            "foreign key(" + TO_ACCOUNT_FIELD + ") references " + ACCOUNTS_TABLE + "(" + ID_FIELD + ")"));
            sqlAdapter.write(Arrays.asList(createAccountsTableQuery, createTransactionsTableQuery));
        } catch (NoDataModificationException e) {
            // Creating table does not change columns
        } catch (DatabaseException e) {
            throw new RuntimeException(e.getMessage());
        }
        manager = new AccountsDBManager(sqlAdapter);
    }

    @Test
    void testCreateShouldNotFail() throws DatabaseException {
        Collection<AccountDB> emptyAccounts = manager.getAll();
        assertEquals(0, emptyAccounts.size());
        manager.create(new AccountDB(0));
        Collection<AccountDB> accounts = manager.getAll();
        assertEquals(1, accounts.size());
    }
}