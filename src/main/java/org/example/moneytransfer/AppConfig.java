package org.example.moneytransfer;

import org.example.moneytransfer.business.converters.AccountsDBConverter;
import org.example.moneytransfer.business.converters.TransactionsDBConverter;
import org.example.moneytransfer.business.managers.AccountsManager;
import org.example.moneytransfer.persistence.Query;
import org.example.moneytransfer.persistence.adapters.SQLJDBCAdapter;
import org.example.moneytransfer.persistence.exceptions.DatabaseException;
import org.example.moneytransfer.persistence.exceptions.NoDataModificationException;
import org.example.moneytransfer.persistence.managers.AccountsDBManager;
import org.example.moneytransfer.persistence.managers.TransactionsDBManager;
import org.example.moneytransfer.rest.adapters.ResponseBuilder;
import org.example.moneytransfer.rest.converters.AccountsConverter;
import org.example.moneytransfer.rest.converters.TransactionTypesConverter;
import org.example.moneytransfer.rest.converters.TransactionsConverter;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.Arrays;
import java.util.Collections;

import static org.example.moneytransfer.persistence.managers.AccountsDBManager.ACCOUNTS_TABLE;
import static org.example.moneytransfer.persistence.managers.DBManager.ID_FIELD;
import static org.example.moneytransfer.persistence.managers.TransactionsDBManager.AMOUNT_FIELD;
import static org.example.moneytransfer.persistence.managers.TransactionsDBManager.DATETIME_FIELD;
import static org.example.moneytransfer.persistence.managers.TransactionsDBManager.FROM_ACCOUNT_FIELD;
import static org.example.moneytransfer.persistence.managers.TransactionsDBManager.TO_ACCOUNT_FIELD;
import static org.example.moneytransfer.persistence.managers.TransactionsDBManager.TRANSACTIONS_TABLE;

public class AppConfig extends ResourceConfig {
    public AppConfig() {
        AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
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

                AccountsDBManager accountsDBManager = new AccountsDBManager(sqlAdapter);
                TransactionsDBManager transactionsDBManager = new TransactionsDBManager(sqlAdapter);
                TransactionsDBConverter transactionsDBConverter = new TransactionsDBConverter();
                AccountsDBConverter accountsDBConverter = new AccountsDBConverter(transactionsDBConverter);
                AccountsManager accountsManager = new AccountsManager(accountsDBManager, transactionsDBManager, accountsDBConverter, transactionsDBConverter);
                TransactionsConverter transactionsConverter = new TransactionsConverter(new TransactionTypesConverter());
                AccountsConverter accountsConverter = new AccountsConverter(transactionsConverter);
                ResponseBuilder responseBuilder = new ResponseBuilder();

                bind(accountsManager).to(AccountsManager.class);
                bind(accountsConverter).to(AccountsConverter.class);
                bind(transactionsConverter).to(TransactionsConverter.class);
                bind(responseBuilder).to(ResponseBuilder.class);
            }
        };
        register(binder);
        packages("org.example.moneytransfer.rest.http");
    }
}
