package org.example.moneytransfer.persistence.adapters;

import org.example.moneytransfer.persistence.Query;
import org.example.moneytransfer.persistence.exceptions.ConnectionException;
import org.example.moneytransfer.persistence.exceptions.DatabaseException;
import org.example.moneytransfer.persistence.exceptions.NoDataModificationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

// H2 In-Memory Database Example shows about storing the database contents into memory.

public class SQLJDBCAdapter {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    public List<Integer> write(List<Query> queries) throws DatabaseException {
        try (Connection connection = connect()) {
            connection.setAutoCommit(false);
            ArrayList<Integer> createdRowsNumbers = new ArrayList<>();
            int updatedRows = 0;
            try {
                for (Query query : queries) {
                    try (PreparedStatement statement = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS)) {
                        ListIterator<String> it = query.getValues().listIterator();
                        while (it.hasNext()) {
                            statement.setString(it.nextIndex() + 1, it.next());
                        }
                        updatedRows += statement.executeUpdate();
                        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                            if (generatedKeys.next()) { // Only 1 by 1 item creation is allowed
                                createdRowsNumbers.add(generatedKeys.getInt(1));
                            } else {
                                createdRowsNumbers.add(0);
                            }
                        }
                    }
                }
                connection.commit();
                if (updatedRows == 0) {
                    throw new NoDataModificationException();
                }
                return createdRowsNumbers;
            } catch (SQLException e) {
                connection.rollback();
                throw new ConnectionException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    public Collection<Map<String, String>> read(Query query, List<String> fields) throws DatabaseException {
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(query.toString())) {
            ListIterator<String> it = query.getValues().listIterator();
            while (it.hasNext()) {
                statement.setString(it.nextIndex() + 1, it.next());
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                Collection<Map<String, String>> results = new ArrayList<>();
                while (resultSet.next()) {
                    Map<String, String> line = new HashMap<>();
                    for (String field : fields) {
                        line.put(field, resultSet.getString(field));
                    }
                    results.add(line);
                }
                return results;
            }
        } catch (SQLException e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    public int write(Query query) throws DatabaseException {
        return write(Collections.singletonList(query)).get(0);
    }

    private Connection connect() throws DatabaseException {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new ConnectionException(e.getMessage());
        }
        try {
            return DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            throw new ConnectionException(e.getMessage());
        }
    }
}
