package org.example.moneytransfer.rest.converters;

import org.example.moneytransfer.business.model.Transaction;
import org.example.moneytransfer.rest.model.TransactionJSON;

import java.util.Date;

public class TransactionsConverter {
    private TransactionTypesConverter converter;

    public TransactionsConverter(TransactionTypesConverter converter) {
        this.converter = converter;
    }

    public TransactionJSON convertToJSON(Transaction entity) {
        return new TransactionJSON(entity.getId(), entity.getAmount(), converter.convertToJSON(entity.getType()), entity.getOtherAccountId(), entity.getTransactionTime());
    }

    public Transaction convertToBusiness(TransactionJSON json) {
        return new Transaction(json.getId(), json.getAmount(), converter.convertToBusiness(json.getType()), json.getOtherAccount(), new Date());
    }
}
