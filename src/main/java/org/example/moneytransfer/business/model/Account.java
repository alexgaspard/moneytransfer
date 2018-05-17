package org.example.moneytransfer.business.model;

import java.math.BigDecimal;
import java.util.List;

public class Account {
    private int id;
    private BigDecimal balance;
    private List<Transaction> transactions;

    public Account(int id, BigDecimal balance, List<Transaction> transactions) {
        this.id = id;
        this.balance = balance;
        this.transactions = transactions;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Account)) {
            return false;
        }
        Account account = (Account) obj;
        return account.getId() == id && account.getBalance().equals(balance) && account.getTransactions().equals(transactions);
    }
}
