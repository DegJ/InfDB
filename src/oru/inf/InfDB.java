package oru.inf;
import java.sql.*;
import java.util.*;

/**
 * <b>Class managing the underlying database (Firebird).</b>
 * Created by Nicklas Magnusson on 2014-05-11. Project: InfDB
 * @author Nicklas Magnusson nicmav141@studentmail.oru.se
 * @version 0.2
 */
public class InfDB {
    private Connection con;
    private String path;
    private HashMap<String,Object> param;
    private int advancedmode;

    /**
     * InfDB
     * Constructor for the DB class
     * initiates a default connection to the database, with SQL AS statement on (columnLabelForName).
     *
     * @param path Path to the Firebird DB, for example C:/DB.FDB or for Mac /User/DB.FDB
     * @throws InfException If the DB connection couldn't be established.
     */
    public InfDB(String path) throws InfException {
        this.path = path;
        advancedmode=0;
        try{
            initConnection();
        } catch (InfException e) {
            throw e;
        }
    }

    /**
     * InfDB
     * Constructor for the DB class
     * initiates a non default connection to the database.
     *
     * @param path Path to the Firebird DB, for example C:/DB.FDB or for Mac /User/DB.FDB
     * @param param Parameters used to establish a connection to the database (Use InfDBHelper getAdvanceParams())
     * @throws InfException If the DB connection couldn't be established.
     */
    public InfDB(String path, HashMap<String,Object> param) throws InfException {
        this.path = path;
        this.param = param;
        advancedmode=1;
        try {
            initConnection(param);
        } catch (InfException e) {
            throw e;
        }
    }

    /**
     * initConnection
     * opens a connection to the DB
     *
     * @throws InfException If the DB connection couldn't be established, the path to the DB isn't correct or the drivers for the database (jaybird-full-XX.jar) isn't found an error is thrown.
     */
    private void initConnection() throws InfException {
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            con = DriverManager.getConnection("jdbc:firebirdsql://localhost:3050/" + this.path +"?columnLabelForName=true", "SYSDBA", "masterkey");
        } catch (ClassNotFoundException e) {
            throw new InfException("Class/driver not found, add the library for Firebird (Jaybird-full-XX.jar");
        } catch (SQLException e) {
            throw new InfException("Couldn't open Firebird database, check your path. Make sure to use .FDB in the end");
        }
    }

    /**
     * initConnection
     * opens a connection to the database with advanced settings
     *
     * @param params the parameters used to connect to the database
     * @throws InfException
     */
    private void initConnection(HashMap<String, Object> params) throws InfException {
        try {
            InfDBHelper.advanceParmsCorrect(params);
        } catch (Exception e){
            throw new InfException(e);
        }

        StringBuilder conBuilder=new StringBuilder();
        conBuilder.append("jdbc:firebirdsql:");
        if((Boolean) params.get("EMBEDDED"))conBuilder.append("embedded:");
        conBuilder.append("//").append(params.get("HOST")).append("/").append(path);

        Properties props=new Properties();
        props.setProperty("user",(String)params.get("USER"));
        props.setProperty("password",(String)params.get("PASSWORD"));
        props.setProperty("encoding",(String)params.get("ENCODING"));
        if((Boolean) params.get("COLUMNLABELFORNAME"))props.setProperty("columnLabelForName","true");

        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            con = DriverManager.getConnection(conBuilder.toString(),props);
        } catch (ClassNotFoundException e) {
            throw new InfException("Class/driver not found, add the library for Firebird (Jaybird-full-XX.jar");
        } catch (SQLException e) {
            throw new InfException("Couldn't open Firebird database, check your path. Make sure to use .FDB in the end");
        }

    }

    /**
     * closeConnection
     * closes the DB connection
     *
     * @throws InfException If DB connection couldn't be closed an error is thrown.
     */
    private void closeConnection() throws InfException {
        try {
            if(con!=null) con.close();
        } catch (SQLException e) {
            throw new InfException("Couldn't close the connection to the database");
        }
    }

    /**
     * checkConnection
     * checks if the DB connection is closed and if so initiates it
     *
     * @throws InfException
     */
    private void checkConnection() throws InfException {
        try {
            if(con == null || con.isClosed()) {
                if (advancedmode == 0) {
                    initConnection();
                } else if (advancedmode == 1) {
                    initConnection(param);
                }
            }
        } catch (SQLException e) {
            throw new InfException("A checkConnection to the database failed");
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
            checkConnection();
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
        } finally {
            closeConnection();
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
            checkConnection();
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
        } finally {
            closeConnection();
        }
        return result;
    }

    /**
     * fetchRow
     * fetches one row from the DB, if the query has several rows the first one is selected.
     *
     * @param query SQL query for the DB
     * @return A HashMap containing the rows values with the columns "name" as their key.
     * @throws InfException If the query doesn't work an error is thrown.
     */
    public HashMap<String, String> fetchRow(String query) throws InfException {
        HashMap<String, String> result = null;
        try {
            checkConnection();
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
        } finally {
            closeConnection();
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
            checkConnection();
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
        } finally {
            closeConnection();
        }
        return result;
    }

    /**
     * getAutoIncrement
     * Retrieves and counts a number upwards for a column that contains Integers,
     * to simulate an incrementing number. Also works on columns containing letters+numbers(or numbers+letters),
     * by replicating the letters and counting the number upwards.
     *
     * @param table    The table where the number(ID) is located
     * @param attribut The column name in the table of the number(ID)
     * @return returns the number(ID) +1
     * @throws InfException If the query didn't work or a column contains something else than numbers or letters+numbers an error is thrown.
     */
    public String getAutoIncrement(String table, String attribut) throws InfException {
        String result = null;
        String query = "SELECT " + attribut + " FROM " + table + " ORDER BY " + attribut + " DESC";
        try {
            checkConnection();
            Statement sm = con.createStatement();
            ResultSet rs = sm.executeQuery(query);
            if (rs.next()) {
                String inc = rs.getString(1);
                if (inc.matches("\\D+\\d+") || inc.matches("\\d+\\D+")) {
                    String[] ar = inc.split("");
                    String letters = "";
                    String numbers = "";
                    for (int i = 0; i < ar.length; i++) {
                        if (ar[i].matches("\\D")) {
                            letters += ar[i];
                        } else if (ar[i].matches("\\d")) {
                            numbers += ar[i];
                        }
                    }
                    if (numbers.matches("\\d+")) {
                        int lastInt = Integer.parseInt(numbers);
                        lastInt++;
                        if (inc.matches("\\D+\\d+")) {
                            result = letters + lastInt;
                        } else {
                            result = lastInt + letters;
                        }
                    }
                } else if (inc.matches("\\d+")) {
                    int lastInt = Integer.parseInt(inc);
                    lastInt++;
                    result = "" + lastInt;
                }
            }
        } catch (SQLException e) {
            throw new InfException("getAutoIncrement statement didn't work - check your query, works with columns containing numbers, letters+numbers or numbers+letters");
        } finally {
            closeConnection();
        }
        return result;
    }

    /**
     * mod
     * Modifies the DB with any SQL DDL or SQL DML INSERT, UPDATE, DELETE
     *
     * @param query SQL query for the DB
     * @throws InfException If the DB reported an error with the query an error is thrown containing a message why the error occurred
     */
    private void mod(String query) throws InfException {
        try {
            checkConnection();
            Statement sm = con.createStatement();
            sm.executeUpdate(query);
        } catch (SQLException e) {
            throw new InfException(e);
        } finally {
            closeConnection();
        }
    }

    /**
     * insert
     * Inserting a row into the DB using SQL syntax INSERT INTO
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
     * Deleting a row from the DB using SQL syntax DELETE FROM
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
     * Updating a row or value in the DB using SQL syntax UPDATE ... SET
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