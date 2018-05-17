package org.example.moneytransfer.persistence.managers;

import org.example.moneytransfer.persistence.Query;
import org.example.moneytransfer.persistence.adapters.SQLJDBCAdapter;
import org.example.moneytransfer.persistence.exceptions.DatabaseException;
import org.example.moneytransfer.persistence.exceptions.EntityNotFoundException;
import org.example.moneytransfer.persistence.exceptions.NotImplementedException;
import org.example.moneytransfer.persistence.model.AccountDB;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AccountsDBManager implements DBManager<AccountDB> {
    public static final String ACCOUNTS_TABLE = "accounts";
    private SQLJDBCAdapter db;

    public AccountsDBManager(SQLJDBCAdapter db) {
        this.db = db;
    }

    @Override
    public int create(AccountDB entity) throws DatabaseException {
        Query query = new Query();
        query.insert(ACCOUNTS_TABLE, Collections.emptyMap());
        return db.write(query);
    }

    @Override
    public Collection<AccountDB> getAll() throws DatabaseException {
        List<AccountDB> entities = new ArrayList<>();
        Query query = new Query();
        List<String> fields = Collections.singletonList(ID_FIELD);
        query.select(ACCOUNTS_TABLE, fields);
        for (Map<String, String> line : db.read(query, fields)) {
            entities.add(new AccountDB(Integer.parseInt(line.get(ID_FIELD))));
        }
        return entities;
    }

    @Override
    public List<AccountDB> getAll(Map<String, String> filters, List<String> sorts) throws DatabaseException {
        throw new NotImplementedException();
    }

    @Override
    public AccountDB get(int id) throws DatabaseException {
        Query query = new Query();
        List<String> fields = Collections.singletonList(ID_FIELD);
        query.select(ACCOUNTS_TABLE, fields);
        query.whereAndEquals(Collections.singletonMap(ID_FIELD, Integer.toString(id)));
        Collection<Map<String, String>> results = db.read(query, fields);
        if (results.isEmpty()) {
            throw new EntityNotFoundException("No account with id " + id);
        }
        Map<String, String> line = results.iterator().next();
        return new AccountDB(Integer.parseInt(line.get(ID_FIELD)));
    }
}
