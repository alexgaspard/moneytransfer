package org.example.moneytransfer.rest.converters;

import org.example.moneytransfer.business.model.Account;
import org.example.moneytransfer.rest.model.AccountJSON;

import java.util.stream.Collectors;

public class AccountsConverter {
    private TransactionsConverter transactionsConverter;

    public AccountsConverter(TransactionsConverter transactionsConverter) {
        this.transactionsConverter = transactionsConverter;
    }

    public AccountJSON convertToJSON(Account entity) {
        return new AccountJSON(entity.getId(), entity.getBalance(), entity.getTransactions().stream().map(transactionsConverter::convertToJSON).collect(Collectors.toList()));
    }

    public Account convertToBusiness(AccountJSON json) {
        return new Account(json.getId(), json.getBalance(), json.getTransactions().stream().map(transactionsConverter::convertToBusiness).collect(Collectors.toList()));
    }
}
