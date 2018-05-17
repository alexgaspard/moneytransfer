package org.example.moneytransfer.persistence.managers;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.example.moneytransfer.persistence.Query;
import org.example.moneytransfer.persistence.adapters.SQLJDBCAdapter;
import org.example.moneytransfer.persistence.exceptions.DatabaseException;
import org.example.moneytransfer.persistence.exceptions.EntityNotFoundException;
import org.example.moneytransfer.persistence.exceptions.NotImplementedException;
import org.example.moneytransfer.persistence.model.AccountDB;
import org.example.moneytransfer.persistence.model.TransactionDB;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.moneytransfer.persistence.model.TransactionDB.RELATED_ACCOUNT_ID;
import static org.example.moneytransfer.persistence.model.TransactionDB.TRANSACTION_TIME;

public class TransactionsDBManager implements DBManager<TransactionDB> {
    public static final String TRANSACTIONS_TABLE = "transactions";
    public static final String AMOUNT_FIELD = "amount";
    public static final String FROM_ACCOUNT_FIELD = "from_account";
    public static final String TO_ACCOUNT_FIELD = "to_account";
    public static final String DATETIME_FIELD = "date_time";
    private SQLJDBCAdapter db;
    private Map<String, String> sortsMap;

    public TransactionsDBManager(SQLJDBCAdapter db) {
        this.db = db;
        sortsMap = new HashMap<>();
        sortsMap.put(TRANSACTION_TIME, DATETIME_FIELD);
    }

    @Override
    public int create(TransactionDB entity) throws DatabaseException {
        Query query = new Query();
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put(AMOUNT_FIELD, entity.getAmount().toString());
        parameters.put(FROM_ACCOUNT_FIELD, Integer.toString(entity.getFromAccount().getId()));
        parameters.put(TO_ACCOUNT_FIELD, Integer.toString(entity.getToAccount().getId()));
        parameters.put(DATETIME_FIELD, Long.toString(entity.getTransactionTime().getTime()));
        query.insert(TRANSACTIONS_TABLE, parameters);
        return db.write(query);
    }

    @Override
    public Collection<TransactionDB> getAll() throws DatabaseException {
        throw new NotImplementedException();
    }

    @Override
    public List<TransactionDB> getAll(Map<String, String> filters, List<String> sorts) throws DatabaseException {
        List<TransactionDB> entities = new ArrayList<>();
        Query query = new Query();
        List<String> fields = Arrays.asList(ID_FIELD, AMOUNT_FIELD, FROM_ACCOUNT_FIELD, TO_ACCOUNT_FIELD, DATETIME_FIELD);
        query.select(TRANSACTIONS_TABLE, fields);
        if (!filters.isEmpty() && filters.containsKey(RELATED_ACCOUNT_ID)) {
            // TODO Improve this
            String relatedAccountId = filters.get(RELATED_ACCOUNT_ID);
            query.whereOrEquals(ImmutableMap.of(FROM_ACCOUNT_FIELD, relatedAccountId, TO_ACCOUNT_FIELD, relatedAccountId));
        }
        if (!sorts.isEmpty()) {
            List<String> convertedSorts = sorts.stream().map(sortsMap::get).collect(Collectors.toList());
            query.orderBy(convertedSorts, true);
        }
        for (Map<String, String> line : db.read(query, fields)) {
            entities.add(new TransactionDB(Integer.parseInt(line.get(ID_FIELD)), new BigDecimal(line.get(AMOUNT_FIELD)), new AccountDB(Integer.parseInt(line.get(FROM_ACCOUNT_FIELD))),
                    new AccountDB(Integer.parseInt(line.get(TO_ACCOUNT_FIELD))), new Date(Long.parseLong(line.get(DATETIME_FIELD)))));
        }
        return entities;
    }

    @Override
    public TransactionDB get(int id) throws DatabaseException {
        Query query = new Query();
        List<String> fields = Arrays.asList(ID_FIELD, AMOUNT_FIELD, FROM_ACCOUNT_FIELD, TO_ACCOUNT_FIELD, DATETIME_FIELD);
        query.select(TRANSACTIONS_TABLE, fields);
        query.whereAndEquals(Collections.singletonMap(ID_FIELD, Integer.toString(id)));
        Collection<Map<String, String>> results = db.read(query, fields);
        if (results.isEmpty()) {
            throw new EntityNotFoundException("No transaction with id " + id);
        }
        Map<String, String> line = results.iterator().next();
        return new TransactionDB(Integer.parseInt(line.get(ID_FIELD)), new BigDecimal(line.get(AMOUNT_FIELD)), new AccountDB(Integer.parseInt(line.get(FROM_ACCOUNT_FIELD))),
                new AccountDB(Integer.parseInt(line.get(TO_ACCOUNT_FIELD))), new Date(Long.parseLong(line.get(DATETIME_FIELD))));
    }
}
