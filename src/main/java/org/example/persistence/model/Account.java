package org.example.persistence.model;

import java.math.BigDecimal;

public class Account {
    private int id;
    private BigDecimal balance;

    public Account(int id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
