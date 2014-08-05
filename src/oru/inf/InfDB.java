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
     * <p>
     * Example:<br>
     *     private InfDB db;<br>
     *     db = new InfDB("path/to/db.fdb");
     *
     * @param path Path to the Firebird DB, for example C:/DB.FDB or for Mac /User/DB.FDB
     * @throws InfException If the DB connection couldn't be established.
     */
    public InfDB(String path) throws InfException {
        this.path = path;
        advancedmode = 0;

        try {
            loadDriver();
            initConnection();
        } catch (InfException e) {
            throw e;
        }

    }

    /**
     * InfDB
     * Constructor for the DB class
     * initiates a non default connection to the database.
     * <p>
     * Example:<br>
     *     private InfDB dba;<br>
     *     HashMap&lt;String,Object&gt; options = InfDBHelper.getAdvanceParams();<br>
     *     dba = new InfDB("path/to/db.fdb", options);
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
            loadDriver();
            initConnection();
        } catch (InfException e) {
            throw e;
        }

    }

    /**
     * Loads the driver for jdbc, the firebird database drivers.
     * @throws InfException if we dint find the driver in java.library.path.
     */
    private void loadDriver() throws InfException{
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
        } catch (ClassNotFoundException e) {
            throw new InfException("Class/driver not found, add the library for Firebird in your current path (jaybird-full-XX.jar)");
        }
    }

    /**
     * Opens a connection to the DB
     *
     * @throws InfException If the DB connection couldn't be established, the path to the DB isn't correct or the drivers for the database (jaybird-full-XX.jar) isn't found an error is thrown.
     */
    private void initConnection() throws InfException {
        if(advancedmode==1) {
            Object[] advCon = makeAdvanceConnect();
            try {
                con = DriverManager.getConnection(advCon[0].toString(),(Properties)advCon[1]);
            } catch (SQLException e) {
                throw new InfException("Couldn't open Firebird database, check your path. Make sure to use .FDB in the end");
            }
        } else {
            try {
                con = DriverManager.getConnection("jdbc:firebirdsql://localhost:3050/" + this.path + "?columnLabelForName=true", "SYSDBA", "masterkey");
            } catch (SQLException e) {
                throw new InfException("Couldn't open Firebird database, check your path. Make sure to use .FDB in the end");
            }
        }
    }

    /**
     * helper method to create the string used to connect and the properties used to connect
     * @return Array containing a String and Properties to make a connection to the DB
     * @throws InfException if the parameters wasn't correct
     */
    private Object[] makeAdvanceConnect() throws InfException {
        try {
            InfDBHelper.advanceParmsCorrect(param);
        } catch (Exception e){
            throw new InfException(e);
        }

        StringBuilder conBuilder=new StringBuilder();
        conBuilder.append("jdbc:firebirdsql:");
        conBuilder.append("//").append(param.get("HOST")).append("/").append(path);

        /* embedded code, commented out until i can figure out how the f* to get embedded to work
        if((Boolean) params.get("EMBEDDED"))conBuilder.append("embedded:");
        if(!(Boolean) params.get("EMBEDDED")){
            conBuilder.append("//").append(params.get("HOST")).append("/").append(path);
        } else {
            conBuilder.append(path);
        }
        */

        Properties props=new Properties();
        props.setProperty("user",(String)param.get("USER"));
        props.setProperty("password",(String)param.get("PASSWORD"));
        props.setProperty("encoding",(String)param.get("ENCODING"));
        if((Boolean) param.get("COLUMNLABELFORNAME"))props.setProperty("columnLabelForName","true");

        Object[] arr ={conBuilder,props};

        return arr;
    }

    /**
     * Closes the DB connection
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
     * Checks if the DB connection is closed and if so initiates it
     *
     * @throws InfException if the check failed
     */
    private void checkConnection() throws InfException {
        try {
            if(con == null || con.isClosed()) {
                    initConnection();
            }
        } catch (SQLException e) {
            throw new InfException("A checkConnection to the database failed");
        }
    }

    /**
     * Fetches a single value from the DB, if the query returns several values, the first one is selected.
     * <p>
     * Example:<br>
     *     String name = db.fetchSingle("select name from agent where id=1");
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
     * Fetches a column from the DB, if the query returns several columns, the first one is selected.
     * <p>
     *     Example:<br>
     *         ArrayList&lt;String&gt; names = db.fetchColumn("select name from agent");
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
                if(result==null)result = new ArrayList<String>();
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
     * Fetches one row from the DB, if the query has several rows the first one is selected.
     * <p>
     *     Example:<br>
     *         HashMap&lt;String,String&gt; agent = db.fetchRow("select * from agent where id=1");
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
                if(result==null)result = new HashMap<String, String>();
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
     * Fetches 1 to N number of rows from the DB into an ArrayList of HashMaps
     * where the ArrayList index is the row and the HashMap contains the rows values with the columns "NAME" as its key.
     * <p>
     *     Example:<br>
     *         ArrayList&lt;HashMap&lt;String,String&gt;&gt; allAgents = db.fetchRows("select * from agent");
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
     * Retrieves and counts a number upwards for a column that contains Integers,
     * to simulate an incrementing number. Also works on columns containing letters+numbers(or numbers+letters)
     * by replicating the letters and counting the number upwards.
     * <p>
     *     Example:<br>
     *         int nextId = db.getAutoIncrement("agent","id");
     * @param table    The table where the number(ID) is located
     * @param attribute The column name in the table of the number(ID)
     * @return returns the number(ID) +1
     * @throws InfException If the query didn't work or a column contains something else than numbers or letters+numbers an error is thrown.
     */
    public String getAutoIncrement(String table, String attribute) throws InfException {
        String result = null;
        String query = "SELECT " + attribute + " FROM " + table + " ORDER BY " + attribute + " DESC";
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
                    for (String anAr : ar) {
                        if (anAr.matches("\\D")) {
                            letters += anAr;
                        } else if (anAr.matches("\\d")) {
                            numbers += anAr;
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
     * Inserting a row into the DB using SQL syntax INSERT INTO.
     * <p>
     *     Example:<br>
     *         db.insert("insert into agent values(1,'Mr Wiggles')");
     * @param query SQL query for the DB
     * @throws InfException If insert isn't correctly formatted throws an error
     */
    public void insert(String query) throws InfException {
        if (query.toLowerCase().startsWith("insert into")) {
            mod(query);
        } else throw new InfException("Not valid INSERT query - check your query");
    }

    /**
     * Deleting a row from the DB using SQL syntax DELETE FROM.
     * <p>
     *     Example:<br>
     *         db.delete("delete from agent where id=1");
     * @param query SQL query for the DB
     * @throws InfException If delete isn't correctly formatted throws an error
     */
    public void delete(String query) throws InfException {
        if (query.toLowerCase().startsWith("delete from")) {
            mod(query);
        } else throw new InfException("Not valid DELETE query - check your query");
    }

    /**
     * Updating a row or value in the DB using SQL syntax UPDATE ... SET
     * <p>
     *     Example:<br>
     *         db.update("update agent set name='Scooter the Snail' where id=1");
     * @param query SQL query for the DB
     * @throws InfException If update isn't correctly formatted throws an error
     */
    public void update(String query) throws InfException {
        if (query.toLowerCase().startsWith("update") && query.toLowerCase().contains("set")) {
            mod(query);
        } else throw new InfException("Not valid UPDATE query - check your query");
    }

    /**
     * Get a ResultSet of the query. The ResultSet is scroll insensitive and is updatable.<br>
     * You must be in advanced mode to use this, connecting using InfDBHelper getAdvanceParams()<br>
     * Will return null if no results could be found with the query.<br>
     * Doesn't close the database connection after the ResultSet is returned.<br>
     * How to use: see Java API docs: http://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html<br>
     * <p>
     * Hints/examples:<br>
     * cursor moves: next(), previous(), absolute(**index**).<br>
     * getters: getString(**columnindex**), getInt(**columnindex**)<br>
     * updates: updateRow(), updateInt(), updateString().<br>
     * inserts: moveToInsertRow(), insertRow(). // after updating values with updates<br>
     * deletes: deleteRow()<br>
     * @param query the SQL query
     * @return ResultSet with the results of the query
     * @throws InfException if something went wrong with the query.
     */
    public ResultSet getResultSet(String query) throws InfException {
        if(advancedmode!=1)throw new InfException("To use getResultSet() you must use the advanced connection parameters");

        //need a separate connection as we don't want the ResultSet to close when we call another database query.
        Connection rscon;
        Object[] advCon = makeAdvanceConnect();
        try {
            rscon = DriverManager.getConnection(advCon[0].toString(),(Properties)advCon[1]);
        } catch (SQLException e) {
            throw new InfException("Couldn't open Firebird database, check your path. Make sure to use .FDB in the end");
        }

        ResultSet rs=null;
        try {
            Statement sm = rscon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE,ResultSet.HOLD_CURSORS_OVER_COMMIT);
            boolean hasRS = sm.execute(query);
            if (hasRS) {
                rs = sm.getResultSet();
            }
        } catch (SQLException e) {
            throw new InfException("Getting the ResultSet didn't work - check your query");
        }
        return rs;
    }

}