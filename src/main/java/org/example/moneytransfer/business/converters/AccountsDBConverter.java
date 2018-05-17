package org.example.moneytransfer.business.converters;

import org.example.moneytransfer.business.model.Account;
import org.example.moneytransfer.business.model.Transaction;
import org.example.moneytransfer.business.model.TransactionType;
import org.example.moneytransfer.persistence.model.AccountDB;
import org.example.moneytransfer.persistence.model.TransactionDB;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class AccountsDBConverter {
    private TransactionsDBConverter transactionsDBConverter;

    public AccountsDBConverter(TransactionsDBConverter transactionsDBConverter) {
        this.transactionsDBConverter = transactionsDBConverter;
    }

    public Account convertToBusiness(AccountDB accountDB, List<TransactionDB> transactionDBs) {
        List<Transaction> transactions = transactionDBs.stream().map(transaction -> transactionsDBConverter.convertToBusiness(accountDB.getId(), transaction)).collect(Collectors.toList());
        BigDecimal balance = transactions.stream().map(transaction -> {
            if (transaction.getType().equals(TransactionType.DEPOSIT)) {
                return transaction.getAmount();
            }
            return transaction.getAmount().negate();
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Account(accountDB.getId(), balance, transactions);
    }
}
