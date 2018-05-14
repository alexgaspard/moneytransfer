package org.example.persistence;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// H2 In-Memory Database Example shows about storing the database contents into memory.

public class SQLAdapter {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_CONNECTION = "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";


    void update(Map<String, String> values) throws SQLException {
        Statement stmt = null;
        try (Connection connection = getDBConnection()) {
            connection.setAutoCommit(false);
            stmt = connection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS ACCOUNT(id int primary key AUTO_INCREMENT, balance decimal)");
            ResultSet rs = stmt.executeQuery("select * from ACCOUNT WHERE id = " + values.get("id"));
            BigDecimal deposit = new BigDecimal(values.get("balance"));
            rs.next();
            BigDecimal origin = rs.getBigDecimal("balance");
            stmt.execute("UPDATE ACCOUNT SET balance = " + origin.add(deposit) + " WHERE id = " + values.get("id"));

            rs = stmt.executeQuery("select * from ACCOUNT");
            System.out.println("H2 In-Memory Database inserted through Statement");
            while (rs.next()) {
                System.out.println("Id " + rs.getInt("id") + " Name " + rs.getString("balance"));
            }

            stmt.close();
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void insert(Map<String, String> values) throws SQLException {
        Statement stmt = null;
        try (Connection connection = getDBConnection()) {
            connection.setAutoCommit(false);
            stmt = connection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS ACCOUNT(id int primary key AUTO_INCREMENT, balance decimal)");
            stmt.execute("INSERT INTO ACCOUNT(balance) VALUES(" + values.get("balance") + ")");

            ResultSet rs = stmt.executeQuery("select * from ACCOUNT");
            System.out.println("H2 In-Memory Database inserted through Statement");
            while (rs.next()) {
                System.out.println("Id " + rs.getInt("id") + " Name " + rs.getString("balance"));
            }

            stmt.close();
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, String>> selectAll() throws SQLException {
        Statement stmt = null;
        try (Connection connection = getDBConnection()) {
            connection.setAutoCommit(false);
            stmt = connection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS ACCOUNT(id int primary key AUTO_INCREMENT, balance decimal)");
//            stmt.execute("INSERT INTO ACCOUNT(id, name) VALUES(1, 'Anju')");

            ResultSet rs = stmt.executeQuery("select * from ACCOUNT");
            System.out.println("H2 In-Memory Database inserted through Statement");
            ArrayList<Map<String, String>> rows = new ArrayList<>();
            while (rs.next()) {
                HashMap<String, String> values = new HashMap<>();
                System.out.println("Id " + rs.getInt("id") + " Name " + rs.getString("balance"));
                values.put("id", rs.getString("id"));
                values.put("balance", rs.getString("balance"));
                rows.add(values);
            }


            stmt.close();
            connection.commit();
            return rows;
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void insertWithPreparedStatement() throws SQLException {
        Connection connection = getDBConnection();
        PreparedStatement createPreparedStatement = null;
        PreparedStatement insertPreparedStatement = null;
        PreparedStatement selectPreparedStatement = null;

        String CreateQuery = "CREATE TABLE PERSON(id int primary key, name varchar(255))";
        String InsertQuery = "INSERT INTO PERSON" + "(id, name) values" + "(?,?)";
        String SelectQuery = "select * from PERSON";

        try {
            connection.setAutoCommit(false);

            createPreparedStatement = connection.prepareStatement(CreateQuery);
            createPreparedStatement.executeUpdate();
            createPreparedStatement.close();

            insertPreparedStatement = connection.prepareStatement(InsertQuery);
            insertPreparedStatement.setInt(1, 1);
            insertPreparedStatement.setString(2, "Jose");
            insertPreparedStatement.executeUpdate();
            insertPreparedStatement.close();

            selectPreparedStatement = connection.prepareStatement(SelectQuery);
            ResultSet rs = selectPreparedStatement.executeQuery();
            System.out.println("H2 In-Memory Database inserted through PreparedStatement");
            while (rs.next()) {
                System.out.println("Id " + rs.getInt("id") + " Name " + rs.getString("name"));
            }
            selectPreparedStatement.close();

            connection.commit();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    public void insertWithStatement() throws SQLException {
        Connection connection = getDBConnection();
        Statement stmt = null;
        try {
            connection.setAutoCommit(false);
            stmt = connection.createStatement();
            stmt.execute("CREATE TABLE PERSON(id int primary key, name varchar(255))");
            stmt.execute("INSERT INTO PERSON(id, name) VALUES(1, 'Anju')");
            stmt.execute("INSERT INTO PERSON(id, name) VALUES(2, 'Sonia')");
            stmt.execute("INSERT INTO PERSON(id, name) VALUES(3, 'Asha')");

            ResultSet rs = stmt.executeQuery("select * from PERSON");
            System.out.println("H2 In-Memory Database inserted through Statement");
            while (rs.next()) {
                System.out.println("Id " + rs.getInt("id") + " Name " + rs.getString("name"));
            }

            stmt.execute("DROP TABLE PERSON");
            stmt.close();
            connection.commit();
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    private Connection getDBConnection() throws SQLException {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            return DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
