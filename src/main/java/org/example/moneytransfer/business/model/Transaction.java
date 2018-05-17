package org.example.moneytransfer.business.model;

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {
    private int id;
    private BigDecimal amount;
    private TransactionType type;
    private int otherAccountId;
    private Date transactionTime;

    public Transaction(int id, BigDecimal amount, TransactionType type, int otherAccountId, Date transactionTime) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.otherAccountId = otherAccountId;
        this.transactionTime = transactionTime;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public int getOtherAccountId() {
        return otherAccountId;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Transaction)) {
            return false;
        }
        Transaction transaction = (Transaction) obj;
        return transaction.getId() == id && transaction.getAmount().equals(amount) && transaction.getType().equals(type) && transaction.getOtherAccountId() == otherAccountId && transaction.getTransactionTime().equals(transactionTime);
    }
}
