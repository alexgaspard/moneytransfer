package org.example.moneytransfer.rest.converters;

import org.example.moneytransfer.business.model.Account;
import org.example.moneytransfer.business.model.Transaction;
import org.example.moneytransfer.business.model.TransactionType;
import org.example.moneytransfer.rest.model.AccountJSON;
import org.example.moneytransfer.rest.model.TransactionJSON;
import org.example.moneytransfer.rest.model.TransactionTypeJSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountsConverterTest {

    private TransactionsConverter transactionsConverter;
    private AccountsConverter converter;

    @BeforeEach
    void setUp() {
        transactionsConverter = mock(TransactionsConverter.class);
        converter = new AccountsConverter(transactionsConverter);
    }

    @Test
    void testConvertToJSONShouldConvertAccount() {
        Transaction transaction = new Transaction(1, new BigDecimal(10), TransactionType.DEPOSIT, 11, new Date());
        Transaction transaction2 = new Transaction(2, new BigDecimal(10), TransactionType.DEPOSIT, 12, new Date());
        Account account = new Account(3, new BigDecimal(20), Arrays.asList(transaction, transaction2));
        converter.convertToJSON(account);
        verify(transactionsConverter).convertToJSON(transaction);
        verify(transactionsConverter).convertToJSON(transaction2);
    }

    @Test
    void testConvertToJSONShouldNotFail() {
        TransactionJSON transactionJSON = new TransactionJSON(1, new BigDecimal(10), TransactionTypeJSON.DEPOSIT, 2, new Date());
        TransactionJSON transactionJSON2 = new TransactionJSON(2, new BigDecimal(10), TransactionTypeJSON.DEPOSIT, 4, new Date());
        when(transactionsConverter.convertToJSON(any())).thenReturn(transactionJSON).thenReturn(transactionJSON2);
        int id = 3;
        BigDecimal balance = new BigDecimal(20);
        Account account = new Account(id, balance, Arrays.asList(new Transaction(1, null, TransactionType.DEPOSIT, 0, null), new Transaction(0, null, TransactionType.DEPOSIT, 0, null)));
        AccountJSON accountJSON = converter.convertToJSON(account);
        Assertions.assertEquals(id, accountJSON.getId());
        Assertions.assertEquals(balance, accountJSON.getBalance());
        Assertions.assertEquals(2, accountJSON.getTransactions().size());
        Assertions.assertEquals(transactionJSON, accountJSON.getTransactions().get(0));
        Assertions.assertEquals(transactionJSON2, accountJSON.getTransactions().get(1));
    }

    @Test
    void testConvertToBusinessShouldConvertAccount() {
        TransactionJSON transaction = new TransactionJSON(1, new BigDecimal(10), TransactionTypeJSON.DEPOSIT, 11, new Date());
        TransactionJSON transaction2 = new TransactionJSON(2, new BigDecimal(10), TransactionTypeJSON.DEPOSIT, 12, new Date());
        AccountJSON account = new AccountJSON(3, new BigDecimal(20), Arrays.asList(transaction, transaction2));
        converter.convertToBusiness(account);
        verify(transactionsConverter).convertToBusiness(transaction);
        verify(transactionsConverter).convertToBusiness(transaction2);
    }

    @Test
    void testConvertToBusinessShouldNotFail() {
        Transaction transaction = new Transaction(1, new BigDecimal(10), TransactionType.DEPOSIT, 2, new Date());
        Transaction transaction2 = new Transaction(2, new BigDecimal(10), TransactionType.DEPOSIT, 4, new Date());
        when(transactionsConverter.convertToBusiness(any())).thenReturn(transaction).thenReturn(transaction2);
        int id = 3;
        BigDecimal balance = new BigDecimal(20);
        AccountJSON accountJSON = new AccountJSON(id, balance, Arrays.asList(new TransactionJSON(1, null, TransactionTypeJSON.DEPOSIT, 0, null), new TransactionJSON(0, null, TransactionTypeJSON.DEPOSIT, 0, null)));
        Account account = converter.convertToBusiness(accountJSON);
        Assertions.assertEquals(id, account.getId());
        Assertions.assertEquals(balance, account.getBalance());
        Assertions.assertEquals(2, account.getTransactions().size());
        Assertions.assertEquals(transaction, account.getTransactions().get(0));
        Assertions.assertEquals(transaction2, account.getTransactions().get(1));
    }
}