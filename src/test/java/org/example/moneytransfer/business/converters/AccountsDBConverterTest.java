package org.example.moneytransfer.business.converters;

import org.example.moneytransfer.business.model.Account;
import org.example.moneytransfer.business.model.Transaction;
import org.example.moneytransfer.business.model.TransactionType;
import org.example.moneytransfer.persistence.model.AccountDB;
import org.example.moneytransfer.persistence.model.TransactionDB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountsDBConverterTest {

    private TransactionsDBConverter transactionsDBConverter;
    private AccountsDBConverter converter;

    @BeforeEach
    void setUp() {
        transactionsDBConverter = mock(TransactionsDBConverter.class);
        converter = new AccountsDBConverter(transactionsDBConverter);
    }

    @Test
    void testConvertToBusinessShouldConvert() {
        when(transactionsDBConverter.convertToBusiness(anyInt(), any())).thenReturn(new Transaction(0, new BigDecimal(0), TransactionType.DEPOSIT, 0, null));
        int accountId = 1;
        TransactionDB transactionDB = new TransactionDB(2, new BigDecimal(10), new AccountDB(4), new AccountDB(accountId), new Date());
        TransactionDB transactionDB2 = new TransactionDB(3, new BigDecimal(10), new AccountDB(accountId), new AccountDB(5), new Date());
        converter.convertToBusiness(new AccountDB(accountId), Arrays.asList(transactionDB, transactionDB2));
        verify(transactionsDBConverter).convertToBusiness(accountId, transactionDB);
        verify(transactionsDBConverter).convertToBusiness(accountId, transactionDB2);
    }

    @Test
    void testConvertToBusinessShouldNotFail() {
        int accountId = 1;
        BigDecimal amount = new BigDecimal(10);
        BigDecimal amount2 = new BigDecimal(3);
        int otherAccountId = 4;
        Transaction transaction = new Transaction(2, amount, TransactionType.DEPOSIT, otherAccountId, new Date());
        Transaction transaction2 = new Transaction(3, amount2, TransactionType.WITHDRAWAL, otherAccountId, new Date());
        when(transactionsDBConverter.convertToBusiness(anyInt(), any())).thenReturn(transaction).thenReturn(transaction2);
        Account result = converter.convertToBusiness(new AccountDB(accountId), Arrays.asList(new TransactionDB(0, null, null, null, null), new TransactionDB(0, null, null, null, null)));
        Assertions.assertEquals(accountId, result.getId());
        Assertions.assertEquals(amount.subtract(amount2), result.getBalance());
        Assertions.assertEquals(Arrays.asList(transaction, transaction2), result.getTransactions());
    }
}