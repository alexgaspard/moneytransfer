package org.example.moneytransfer.rest.resources;

import org.example.moneytransfer.persistence.managers.AccountManager;
import org.example.moneytransfer.persistence.adapters.SQLAdapter;
import org.example.moneytransfer.persistence.model.Account;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@Path(AccountsResource.ACCOUNTS_PATH)
public class AccountsResource {
    static final String ACCOUNTS_PATH = "/accounts";
    private SQLAdapter sqlAdapter;

    @Inject
    public AccountsResource(SQLAdapter sqlAdapter) {
        this.sqlAdapter = sqlAdapter;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getMessage() throws SQLException {
        AccountManager accountManager = new AccountManager(this.sqlAdapter);
        accountManager.create(new Account(0, new BigDecimal(22.5)));
        List<Account> accounts = accountManager.getAll();
        StringBuilder response = new StringBuilder();
        for (Account account : accounts) {
            response.append("id: ").append(account.getId()).append(", balance: ").append(account.getBalance()).append("<br/>");
        }
        return response.toString();
    }
}