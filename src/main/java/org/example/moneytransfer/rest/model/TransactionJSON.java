package org.example.moneytransfer.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionJSON extends EntityJSON {

    private static final String AMOUNT = "amount";
    private static final String TYPE = "type";
    private static final String RELATED = "related";
    private static final String DATE = "date";

    private BigDecimal amount;
    private TransactionTypeJSON type;
    private int otherAccount;
    private Date transactionTime;

    @SuppressWarnings("unused")
    private TransactionJSON() {
        super(0);
    }

    public TransactionJSON(int id, BigDecimal amount, TransactionTypeJSON type, int otherAccount, Date transactionTime) {
        super(id);
        this.amount = amount;
        this.type = type;
        this.otherAccount = otherAccount;
        this.transactionTime = transactionTime;
    }

    @JsonProperty(AMOUNT)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public BigDecimal getAmount() {
        return amount;
    }

    @JsonProperty(TYPE)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public TransactionTypeJSON getType() {
        return type;
    }

    @JsonProperty(RELATED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public int getOtherAccount() {
        return otherAccount;
    }

    @JsonProperty(DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss a z")
    public Date getTransactionTime() {
        return transactionTime;
    }
}