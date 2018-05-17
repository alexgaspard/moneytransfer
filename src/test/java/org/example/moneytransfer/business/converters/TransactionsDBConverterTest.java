package org.example.moneytransfer.business.converters;

import org.example.moneytransfer.business.model.Transaction;
import org.example.moneytransfer.business.model.TransactionType;
import org.example.moneytransfer.persistence.model.AccountDB;
import org.example.moneytransfer.persistence.model.TransactionDB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TransactionsDBConverterTest {

    private TransactionsDBConverter converter;

    @BeforeEach
    void setUp() {
        converter = new TransactionsDBConverter();
    }

    @Test
    void testConvertToDBWhenDepositShouldNotFail() {
        int accountId = 1;
        int id = 2;
        BigDecimal amount = new BigDecimal(10);
        int otherAccountId = 3;
        Date transactionTime = new Date();
        TransactionDB result = converter.convertToDB(accountId, new Transaction(id, amount, TransactionType.DEPOSIT, otherAccountId, transactionTime));
        Assertions.assertEquals(id, result.getId());
        Assertions.assertEquals(otherAccountId, result.getFromAccount().getId());
        Assertions.assertEquals(accountId, result.getToAccount().getId());
        Assertions.assertEquals(amount, result.getAmount());
        Assertions.assertEquals(transactionTime, result.getTransactionTime());
    }

    @Test
    void testConvertToDBWhenWithdrawalShouldNotFail() {
        int accountId = 1;
        int id = 2;
        BigDecimal amount = new BigDecimal(10);
        int otherAccountId = 3;
        Date transactionTime = new Date();
        TransactionDB result = converter.convertToDB(accountId, new Transaction(id, amount, TransactionType.WITHDRAWAL, otherAccountId, transactionTime));
        Assertions.assertEquals(id, result.getId());
        Assertions.assertEquals(accountId, result.getFromAccount().getId());
        Assertions.assertEquals(otherAccountId, result.getToAccount().getId());
        Assertions.assertEquals(amount, result.getAmount());
        Assertions.assertEquals(transactionTime, result.getTransactionTime());
    }

    @Test
    void testConvertToBusinessWhenDepositShouldNotFail() {
        int accountId = 1;
        int id = 2;
        BigDecimal amount = new BigDecimal(10);
        int otherAccountId = 3;
        Date transactionTime = new Date();
        Transaction result = converter.convertToBusiness(accountId, new TransactionDB(id, amount, new AccountDB(otherAccountId), new AccountDB(accountId), transactionTime));
        Assertions.assertEquals(id, result.getId());
        Assertions.assertEquals(TransactionType.DEPOSIT, result.getType());
        Assertions.assertEquals(otherAccountId, result.getOtherAccountId());
        Assertions.assertEquals(amount, result.getAmount());
        Assertions.assertEquals(transactionTime, result.getTransactionTime());
    }

    @Test
    void testConvertToBusinessWhenWithdrawalShouldNotFail() {
        int accountId = 1;
        int id = 2;
        BigDecimal amount = new BigDecimal(10);
        int otherAccountId = 3;
        Date transactionTime = new Date();
        Transaction result = converter.convertToBusiness(accountId, new TransactionDB(id, amount, new AccountDB(accountId), new AccountDB(otherAccountId), transactionTime));
        Assertions.assertEquals(id, result.getId());
        Assertions.assertEquals(TransactionType.WITHDRAWAL, result.getType());
        Assertions.assertEquals(otherAccountId, result.getOtherAccountId());
        Assertions.assertEquals(amount, result.getAmount());
        Assertions.assertEquals(transactionTime, result.getTransactionTime());
    }
}