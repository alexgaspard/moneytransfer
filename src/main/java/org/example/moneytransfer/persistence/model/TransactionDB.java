package org.example.moneytransfer.persistence.model;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionDB {

    public static final String RELATED_ACCOUNT_ID = "RELATED_ACCOUNT_ID";
    public static final String TRANSACTION_TIME = "TRANSACTION_TIME";
    private int id;
    private BigDecimal amount;
    private AccountDB fromAccount;
    private AccountDB toAccount;
    private Date transactionTime;

    public TransactionDB(int id, BigDecimal amount, AccountDB fromAccount, AccountDB toAccount, Date transactionTime) {
        this.id = id;
        this.amount = amount;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.transactionTime = transactionTime;
    }

    public int getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public AccountDB getFromAccount() {
        return fromAccount;
    }

    public AccountDB getToAccount() {
        return toAccount;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TransactionDB)) {
            return false;
        }
        TransactionDB transaction = (TransactionDB) obj;
        return transaction.getId() == id && transaction.getAmount().equals(amount) && transaction.getFromAccount().equals(fromAccount) && transaction.getToAccount().equals(toAccount) && transaction.getTransactionTime().equals(transactionTime);
    }
}
