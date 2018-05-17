package org.example.moneytransfer.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class EntityJSON {

    public static final String ID = "id";
    private int id;

    EntityJSON(int id) {
        this.id = id;
    }

    @JsonProperty(ID)
    public int getId() {
        return id;
    }
}
