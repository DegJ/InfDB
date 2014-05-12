/**
 * Created by Nicklas Magnusson on 2014-05-11. Project: InfDB
 */
package oru.inf;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class InfDB {
    private Connection con;
    private String path;

    /** InfDB
     *  Constructor for the DB class
     * @param path Path to the Firebird DB, for example C:/DB.FDB or for Mac /User/DB.FDB
     * @throws InfException
     */
    public InfDB(String path) throws InfException {
        try {
            this.path = path;
            initConnection();
        } catch (InfException e) {
            throw new InfException(e);
        }
    }

    /** initConnection
     * opens a connection to the DB
     * @throws InfException If the DB connection couldn't be established, the path to the DB isn't correct or the drivers for the database (jaybird-full-XX.jar) isn't found an error is thrown.
     */
    private void initConnection() throws InfException {
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            con = DriverManager.getConnection("jdbc:firebirdsql:localhost/3050:" + this.path, "SYSDBA", "masterkey");
        } catch (ClassNotFoundException e) {
            throw new InfException("Class not found, add the library for Firebird");
        } catch (SQLException e) {
            throw new InfException("Couldn't open Firebird database, check your path. Make sure to use .FDB in the end");
        }
    }

    /** closeConnection
     * closes the DB connection
     * @throws InfException If DB connection couldn't be closed an error is thrown.
     */
    private void closeConnection() throws InfException {
        try {
            con.close();
        } catch (SQLException e) {
            throw new InfException("Couldn't close the connection to the database");
        }
    }

    /**
     * fetchSingle
     * fetches a single value from the DB, if the query returns several values, the first one is selected.
     *
     * @param query SQL query for the DB
     * @return a String containing the value.
     * @throws InfException If the query doesn't work an error is thrown.
     */
    public String fetchSingle(String query) throws InfException {
        String result = null;
        try {
            Statement sm = con.createStatement();
            boolean hasRS = sm.execute(query);
            if (hasRS) {
                ResultSet rs = sm.getResultSet();
                if (rs.next()) {
                    result = rs.getString(1);
                }
            }
        } catch (SQLException e) {
            throw new InfException("fetchSingle statement didn't work - check your query");
        }
        return result;
    }

    /**
     * fetchColumn
     * fetches a column from the DB, if the query returns several columns, the first one is selected.
     *
     * @param query SQL query for the DB
     * @return An ArrayList containing the columns row values.
     * @throws InfException If the query doesn't work an error is thrown.
     */
    public ArrayList<String> fetchColumn(String query) throws InfException {
        ArrayList<String> result = null;
        try {
            Statement sm = con.createStatement();
            ResultSet rs = sm.executeQuery(query);
            while (rs.next()) {
                if (result == null) {
                    result = new ArrayList<String>();
                }
                result.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new InfException("fetchColumn statement didn't work - check your query");
        }
        return result;
    }

    /**
     * fetchRow
     * fetches 1 row from the DB, if the query has several rows the first one is selected.
     *
     * @param query SQL query for the DB
     * @return A HashMap containing the rows values with the columns "name" as their key.
     * @throws InfException If the query doesn't work an error is thrown.
     */
    public HashMap<String, String> fetchRow(String query) throws InfException {
        HashMap<String, String> result = null;
        try {
            Statement sm = con.createStatement();
            ResultSet rs = sm.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int countColumns = rsmd.getColumnCount();
            int i = 1;
            if (rs.next()) {
                if (result == null) {
                    result = new HashMap<String, String>();
                }
                while (i <= countColumns) {
                    result.put(rsmd.getColumnName(i), rs.getString(i));
                    i++;
                }
            }
        } catch (SQLException e) {
            throw new InfException("fetchRow statement didn't work - check your query");
        }
        return result;
    }

    /**
     * fetchRows
     * fetches 1 to N number of rows from the DB into an ArrayList of HashMaps
     * where the ArrayList index is the row and the HashMap contains the rows values with the columns "name" as its key.
     *
     * @param query SQL query for the DB
     * @return An ArrayList of HashMaps where the ArrayList is the rows number and the HashMap the rows values with the columns "name" as their key.
     * @throws InfException If the query doesn't work an error is thrown.
     */
    public ArrayList<HashMap<String, String>> fetchRows(String query) throws InfException {
        ArrayList<HashMap<String, String>> result = null;
        try {
            Statement sm = con.createStatement();
            ResultSet rs = sm.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int countColumns = rsmd.getColumnCount();
            while (rs.next()) {
                if (result == null) {
                    result = new ArrayList<HashMap<String, String>>();
                }
                int i = 1;
                HashMap<String, String> tempHM = new HashMap<String, String>();
                while (i <= countColumns) {
                    tempHM.put(rsmd.getColumnName(i), rs.getString(i));
                    i++;
                }
                result.add(tempHM);
            }
        } catch (SQLException e) {
            throw new InfException("fetchRows statement didn't work - check your query");
        }
        return result;
    }

    /**
     * getAutoIncrement
     * Retrieves and counts a number upwards for a column that contains Integers,
     * to simulate an incrementing number.
     *
     * @param table    The table where the number(ID) is located
     * @param attribut The column name in the table of the number(ID)
     * @return returns the number(ID) +1
     * @throws InfException If the query didn't work or a column contains something else than numbers an error is thrown.
     */
    public String getAutoIncrement(String table, String attribut) throws InfException {
        String result = null;
        String query = "SELECT " + attribut + " FROM " + table + " ORDER BY " + attribut + " DESC";
        try {
            Statement sm = con.createStatement();
            ResultSet rs = sm.executeQuery(query);
            if (rs.next()) {
                String inc = rs.getString(1);
                if (inc.matches("\\d+")) {
                    int lastInt = Integer.parseInt(inc);
                    lastInt++;
                    result = "" + lastInt;
                }
            }
        } catch (SQLException e) {
            throw new InfException("getAutoIncrement statement didn't work - check your query, only works with columns containing only numbers");
        }
        return result;
        // @todo use String.split() to be able to use whatever + a number for increment
    }

    /**
     * mod
     * Modifies the DB with any SQL DDL or SQL DML INSERT, UPDATE, DELETE
     *
     * @param query SQL query for the DB
     * @throws InfException If the DB reported an error with the query an error is thrown containing a message why the error occurred
     */
    public void mod(String query) throws InfException {
        try {
            Statement sm = con.createStatement();
            sm.executeUpdate(query);
        } catch (SQLException e) {
            throw new InfException(e);
        }
    }

    /**
     * insert
     * Inserting a row into the DB
     * here for legacy reasons, otherwise use mod() for modifications to DB
     *
     * @param query SQL query for the DB
     * @throws InfException If insert isn't correctly formatted throws an error
     */
    public void insert(String query) throws InfException {
        if (query.toLowerCase().startsWith("insert into")) {
            mod(query);
        } else throw new InfException("Not valid INSERT query - check your query");
    }

    /**
     * delete
     * Deleting a row from the DB
     * here for legacy reasons, otherwise use mod() for modifications to DB
     *
     * @param query SQL query for the DB
     * @throws InfException If delete isn't correctly formatted throws an error
     */
    public void delete(String query) throws InfException {
        if (query.toLowerCase().startsWith("delete from")) {
            mod(query);
        } else throw new InfException("Not valid DELETE query - check your query");
    }

    /**
     * update
     * Updating a row or value in the DB
     * here for legacy reasons, otherwise use mod() for modifications to DB
     *
     * @param query SQL query for the DB
     * @throws InfException If update isn't correctly formatted throws an error
     */
    public void update(String query) throws InfException {
        if (query.toLowerCase().startsWith("update") && query.toLowerCase().contains("set")) {
            mod(query);
        } else throw new InfException("Not valid UPDATE query - check your query");
    }

}