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
import org.example.moneytransfer.persistence.exceptions.DatabaseException;
import org.example.moneytransfer.persistence.exceptions.EntityNotFoundException;
import org.example.moneytransfer.persistence.managers.AccountsDBManager;
import org.example.moneytransfer.persistence.managers.TransactionsDBManager;
import org.example.moneytransfer.persistence.model.AccountDB;
import org.example.moneytransfer.persistence.model.TransactionDB;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.example.moneytransfer.persistence.model.TransactionDB.RELATED_ACCOUNT_ID;
import static org.example.moneytransfer.persistence.model.TransactionDB.TRANSACTION_TIME;

public class AccountsManager {

    static final int MAXIMUM_OVERDRAFT = 100;

    private AccountsDBManager accountsDBManager;
    private TransactionsDBManager transactionsDBManager;
    private AccountsDBConverter accountsDBConverter;
    private TransactionsDBConverter transactionsDBConverter;

    public AccountsManager(AccountsDBManager accountsDBManager, TransactionsDBManager transactionsDBManager, AccountsDBConverter accountsDBConverter, TransactionsDBConverter transactionsDBConverter) {
        this.accountsDBManager = accountsDBManager;
        this.transactionsDBManager = transactionsDBManager;
        this.accountsDBConverter = accountsDBConverter;
        this.transactionsDBConverter = transactionsDBConverter;
    }

    public int createAccount() throws UnknownError {
        try {
            return accountsDBManager.create(new AccountDB(0));
        } catch (DatabaseException e) {
            throw new UnknownError(e.getMessage());
        }
    }

    public Account getAccount(int accountId) throws UnknownError {
        try {
            AccountDB accountDB = accountsDBManager.get(accountId);
            List<TransactionDB> transactionDBs = transactionsDBManager.getAll(ImmutableMap.of(RELATED_ACCOUNT_ID, Integer.toString(accountId)), ImmutableList.of(TRANSACTION_TIME));
            return accountsDBConverter.convertToBusiness(accountDB, transactionDBs);
        } catch (EntityNotFoundException e) {
            throw new NotExists(e.getMessage());
        } catch (DatabaseException e) {
            throw new UnknownError(e.getMessage());
        }
    }

    public int createTransaction(Transaction transaction, int accountId) throws UnknownError {
        try {
            accountsDBManager.get(accountId);
            accountsDBManager.get(transaction.getOtherAccountId());
        } catch (EntityNotFoundException e) {
            throw new NotExists(e.getMessage());
        } catch (DatabaseException e) {
            throw new UnknownError(e.getMessage());
        }
        TransactionDB transactionDB = transactionsDBConverter.convertToDB(accountId, transaction);
        Account fromAccount = getAccount(transactionDB.getFromAccount().getId());
        if (fromAccount.getBalance().subtract(transaction.getAmount()).compareTo(new BigDecimal(MAXIMUM_OVERDRAFT).negate()) < 0) {
            throw new MaximumOverdraftReached("Account with id " + fromAccount.getId() + " would reach maximum overdraft with this transaction");
        }
        try {
            return transactionsDBManager.create(transactionDB);
        } catch (DatabaseException e) {
            throw new UnknownError(e.getMessage());
        }
    }

    public Transaction getTransaction(int transactionId, int accountId) throws UnknownError {
        try {
            TransactionDB transactionDB = transactionsDBManager.get(transactionId);
            if (transactionDB.getFromAccount().getId() != accountId && transactionDB.getToAccount().getId() != accountId) {
                throw new NotExists("No transaction found");
            }
            return transactionsDBConverter.convertToBusiness(accountId, transactionDB);
        } catch (EntityNotFoundException e) {
            throw new NotExists(e.getMessage());
        } catch (DatabaseException e) {
            throw new UnknownError(e.getMessage());
        }
    }

    public Collection<Account> getAccounts() throws UnknownError {
        try {
            List<Account> accounts = new ArrayList<>();
            Collection<AccountDB> accountDBs = accountsDBManager.getAll();
            for (AccountDB accountDB : accountDBs) {
                List<TransactionDB> transactionDBs = transactionsDBManager.getAll(ImmutableMap.of(RELATED_ACCOUNT_ID, Integer.toString(accountDB.getId())), ImmutableList.of(TRANSACTION_TIME));
                accounts.add(accountsDBConverter.convertToBusiness(accountDB, transactionDBs));
            }
            return accounts;
        } catch (DatabaseException e) {
            throw new UnknownError(e.getMessage());
        }
    }
}
