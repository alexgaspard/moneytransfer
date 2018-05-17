package org.example.moneytransfer.persistence.model;

public class AccountDB {
    private int id;

    public AccountDB(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AccountDB)) {
            return false;
        }
        AccountDB account = (AccountDB) obj;
        return account.getId() == id;
    }
}
