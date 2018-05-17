package org.example.moneytransfer.business.converters;

import org.example.moneytransfer.business.model.Transaction;
import org.example.moneytransfer.business.model.TransactionType;
import org.example.moneytransfer.persistence.model.AccountDB;
import org.example.moneytransfer.persistence.model.TransactionDB;

public class TransactionsDBConverter {

    public TransactionDB convertToDB(int accountId, Transaction transaction) {
        AccountDB fromAccountDB = new AccountDB(transaction.getOtherAccountId());
        AccountDB toAccountDB = new AccountDB(accountId);
        switch (transaction.getType()) {
            case DEPOSIT:
                fromAccountDB = new AccountDB(transaction.getOtherAccountId());
                toAccountDB = new AccountDB(accountId);
                break;
            case WITHDRAWAL:
                fromAccountDB = new AccountDB(accountId);
                toAccountDB = new AccountDB(transaction.getOtherAccountId());
                break;
        }
        return new TransactionDB(transaction.getId(), transaction.getAmount(), fromAccountDB, toAccountDB, transaction.getTransactionTime());
    }

    public Transaction convertToBusiness(int accountId, TransactionDB transactionDB) {
        TransactionType type = TransactionType.DEPOSIT;
        int otherAccountId = transactionDB.getFromAccount().getId();
        if (transactionDB.getFromAccount().getId() == accountId) {
            type = TransactionType.WITHDRAWAL;
            otherAccountId = transactionDB.getToAccount().getId();
        }
        return new Transaction(transactionDB.getId(), transactionDB.getAmount(), type, otherAccountId, transactionDB.getTransactionTime());
    }
}
