package org.example.moneytransfer.persistence.managers;

import org.example.moneytransfer.persistence.exceptions.DatabaseException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface DBManager<Entity> {
    String ID_FIELD = "id";

    int create(Entity entity) throws DatabaseException;

    Collection<Entity> getAll() throws DatabaseException;

    List<Entity> getAll(Map<String, String> filters, List<String> sorts) throws DatabaseException;

    Entity get(int id) throws DatabaseException;
}

