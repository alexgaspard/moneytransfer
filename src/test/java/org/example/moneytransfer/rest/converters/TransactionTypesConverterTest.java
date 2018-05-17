package org.example.moneytransfer.rest.converters;

import org.example.moneytransfer.business.model.TransactionType;
import org.example.moneytransfer.rest.model.TransactionTypeJSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionTypesConverterTest {

    private TransactionTypesConverter converter;

    @BeforeEach
    void setUp() {
        converter = new TransactionTypesConverter();
    }

    @Test
    void testConvertToJSONWhenDepositShouldNotFail() {
        Assertions.assertEquals(TransactionTypeJSON.DEPOSIT, converter.convertToJSON(TransactionType.DEPOSIT));
    }

    @Test
    void testConvertToJSONWhenWithdrawalShouldNotFail() {
        Assertions.assertEquals(TransactionTypeJSON.WITHDRAWAL, converter.convertToJSON(TransactionType.WITHDRAWAL));
    }

    @Test
    void testConvertToBusinessWhenDepositShouldNotFail() {
        Assertions.assertEquals(TransactionType.DEPOSIT, converter.convertToBusiness(TransactionTypeJSON.DEPOSIT));
    }

    @Test
    void testConvertToBusinessWhenWithdrawalShouldNotFail() {
        Assertions.assertEquals(TransactionType.WITHDRAWAL, converter.convertToBusiness(TransactionTypeJSON.WITHDRAWAL));
    }
}