package org.example.moneytransfer.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Query {

    private StringBuilder queryBuilder;
    private List<String> values;

    public Query() {
        queryBuilder = new StringBuilder();
        values = new ArrayList<>();
    }

    @Override
    public String toString() {
        return queryBuilder.toString();
    }

    public List<String> getValues() {
        return values;
    }

    public void insert(String table, Map<String, String> parameters) {
        insertOrIgnore(table, parameters, false);
    }

    public void insertOrIgnore(String table, Map<String, String> parameters) {
        insertOrIgnore(table, parameters, true);
    }

    private void insertOrIgnore(String table, Map<String, String> parameters, boolean ignore) {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, String> pair : parameters.entrySet()) {
            keys.add(pair.getKey());
            values.add(pair.getValue());
        }
        queryBuilder.append("INSERT ");
        if (ignore) {
            queryBuilder.append("OR IGNORE ");
        }
        queryBuilder.append("INTO ");
        queryBuilder.append(table);
        queryBuilder.append("(");
        queryBuilder.append(String.join(", ", keys)).append(") VALUES (");
        queryBuilder.append(String.join(", ", Collections.nCopies(keys.size(), "?")));
        queryBuilder.append(")");
    }

    public void update(String table, Map<String, String> parameters) {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, String> pair : parameters.entrySet()) {
            keys.add(pair.getKey());
            values.add(pair.getValue());
        }
        queryBuilder.append("UPDATE ");
        queryBuilder.append(table);
        queryBuilder.append(" SET ");
        queryBuilder.append(keys.stream().map(key -> key + " = ?").collect(Collectors.joining(", ")));
    }

    public void select(String table, Collection<String> fields) {
        queryBuilder.append("SELECT ");
        queryBuilder.append(String.join(", ", fields));
        queryBuilder.append(" FROM ");
        queryBuilder.append(table);
    }

    private void whereEquals(Map<String, String> parameters, String operator) {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, String> pair : parameters.entrySet()) {
            keys.add(pair.getKey());
            values.add(pair.getValue());
        }
        queryBuilder.append(" WHERE ");
        queryBuilder.append(keys.stream().map(key -> key + " = ?").collect(Collectors.joining(" " + operator + " ")));
    }

    public void whereAndEquals(Map<String, String> parameters) {
        whereEquals(parameters, "AND");
    }

    public void whereOrEquals(Map<String, String> parameters) {
        whereEquals(parameters, "OR");
    }

    public void orderBy(List<String> fields, boolean ascending) {
        queryBuilder.append(" ORDER BY ");
        queryBuilder.append(fields.stream().collect(Collectors.joining(", ")));
        String orderWord = ascending ? "ASC" : "DESC";
        queryBuilder.append(" ").append(orderWord);
    }

    public void createTable(String tableName, List<String> columnsDefinitions) {
        queryBuilder.append("CREATE TABLE ");
        queryBuilder.append(tableName);
        queryBuilder.append(" ( ");
        queryBuilder.append(String.join(", \n", columnsDefinitions));
        queryBuilder.append(" )");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Query)) {
            return false;
        }
        return obj.toString().equals(toString()) && ((Query) obj).getValues().equals(getValues());
    }
}

