package org.example.moneytransfer.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class AccountJSON extends EntityJSON {

    private static final String BALANCE = "balance";
    private static final String TRANSACTIONS = "transactions";
    private BigDecimal balance;
    private List<TransactionJSON> transactions;

    @SuppressWarnings("unused")
    private AccountJSON() {
        super(0);
    }

    public AccountJSON(int id, BigDecimal balance, List<TransactionJSON> transactions) {
        super(id);
        this.balance = balance;
        this.transactions = transactions;
    }

    @JsonProperty(BALANCE)
    public BigDecimal getBalance() {
        return balance;
    }

    @JsonProperty(TRANSACTIONS)
    public List<TransactionJSON> getTransactions() {
        return transactions;
    }
}