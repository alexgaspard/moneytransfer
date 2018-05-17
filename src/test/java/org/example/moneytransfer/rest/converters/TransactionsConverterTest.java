package org.example.moneytransfer.rest.converters;

import org.example.moneytransfer.business.model.Transaction;
import org.example.moneytransfer.business.model.TransactionType;
import org.example.moneytransfer.rest.model.TransactionJSON;
import org.example.moneytransfer.rest.model.TransactionTypeJSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionsConverterTest {

    private TransactionTypesConverter transactionTypesConverter;
    private TransactionsConverter converter;

    @BeforeEach
    void setUp() {
        transactionTypesConverter = mock(TransactionTypesConverter.class);
        converter = new TransactionsConverter(transactionTypesConverter);
    }

    @Test
    void testConvertToJSONShouldConvertType() {
        TransactionType type = TransactionType.DEPOSIT;
        Transaction transaction = new Transaction(1, new BigDecimal(10), type, 2, new Date());
        converter.convertToJSON(transaction);
        verify(transactionTypesConverter).convertToJSON(type);
    }

    @Test
    void testConvertToJSONShouldNotFail() {
        int id = 1;
        BigDecimal amount = new BigDecimal(10);
        int otherAccountId = 2;
        Date transactionTime = new Date();
        TransactionTypeJSON type = TransactionTypeJSON.DEPOSIT;
        when(transactionTypesConverter.convertToJSON(any())).thenReturn(type);
        Transaction transaction = new Transaction(id, amount, TransactionType.DEPOSIT, otherAccountId, transactionTime);
        TransactionJSON transactionJSON = converter.convertToJSON(transaction);
        Assertions.assertEquals(id, transactionJSON.getId());
        Assertions.assertEquals(amount, transactionJSON.getAmount());
        Assertions.assertEquals(type, transactionJSON.getType());
        Assertions.assertEquals(otherAccountId, transactionJSON.getOtherAccount());
    }

    @Test
    void testConvertToBusinessShouldConvertType() {
        TransactionTypeJSON type = TransactionTypeJSON.DEPOSIT;
        TransactionJSON transaction = new TransactionJSON(1, new BigDecimal(10), type, 2, new Date());
        converter.convertToBusiness(transaction);
        verify(transactionTypesConverter).convertToBusiness(type);
    }

    @Test
    void testConvertToBusinessShouldNotFail() {
        int id = 1;
        BigDecimal amount = new BigDecimal(10);
        int otherAccountId = 2;
        Date transactionTime = new Date();
        TransactionType type = TransactionType.DEPOSIT;
        when(transactionTypesConverter.convertToBusiness(any())).thenReturn(type);
        TransactionJSON transactionJSON = new TransactionJSON(id, amount, TransactionTypeJSON.DEPOSIT, otherAccountId, transactionTime);
        Transaction transaction = converter.convertToBusiness(transactionJSON);
        Assertions.assertEquals(id, transaction.getId());
        Assertions.assertEquals(amount, transaction.getAmount());
        Assertions.assertEquals(type, transaction.getType());
        Assertions.assertEquals(otherAccountId, transaction.getOtherAccountId());
    }
}