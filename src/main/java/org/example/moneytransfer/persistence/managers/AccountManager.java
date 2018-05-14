package org.example.moneytransfer.persistence.managers;

import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.example.moneytransfer.persistence.adapters.SQLAdapter;
import org.example.moneytransfer.persistence.model.Account;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class AccountManager {
    private SQLAdapter db;

    public AccountManager(SQLAdapter db) {
        this.db = db;
    }

    public void create(Account account) throws SQLException {
        db.insert(ImmutableMap.of("id", Integer.toString(account.getId()), "balance", account.getBalance().toString()));
    }

    public void add(int id, BigDecimal deposit) throws SQLException {
        // TODO Temporary implementation
//        List<Account> accounts = db.selectAll().stream().filter(values -> Integer.parseInt(values.get("id")) == id).map(values -> new Account(Integer.parseInt(values.get("id")), new BigDecimal(values.get("balance")))).collect(Collectors.toList());
        db.update(ImmutableMap.of("id", Integer.toString(id), "balance", deposit.toString()));
    }

    public List<Account> getAll() throws SQLException {
        return db.selectAll().stream().map(values -> new Account(Integer.parseInt(values.get("id")), new BigDecimal(values.get("balance")))).collect(Collectors.toList());
    }
}
