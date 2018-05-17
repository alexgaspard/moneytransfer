package org.example.moneytransfer.rest.converters;

import org.example.moneytransfer.business.model.TransactionType;
import org.example.moneytransfer.rest.model.TransactionTypeJSON;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionTypesConverter {
    private Map<TransactionType, TransactionTypeJSON> transactionsTypesToJSONMap;
    private Map<TransactionTypeJSON, TransactionType> transactionsTypesToBusinessMap;

    public TransactionTypesConverter() {
        transactionsTypesToJSONMap = new HashMap<>();
        transactionsTypesToJSONMap.put(TransactionType.DEPOSIT, TransactionTypeJSON.DEPOSIT);
        transactionsTypesToJSONMap.put(TransactionType.WITHDRAWAL, TransactionTypeJSON.WITHDRAWAL);
        transactionsTypesToBusinessMap = transactionsTypesToJSONMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    public TransactionTypeJSON convertToJSON(TransactionType entity) {
        return transactionsTypesToJSONMap.get(entity);
    }

    public TransactionType convertToBusiness(TransactionTypeJSON json) {
        return transactionsTypesToBusinessMap.get(json);
    }
}
