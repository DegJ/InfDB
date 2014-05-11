/**
 * Created by Nicklas on 2014-05-11.
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class InfDB {
    private Connection con;
    private String path;

    public InfDB(String path) throws InfException {
        try {
            this.path = path;
            initConnection();
        } catch (InfException e) {
            throw new InfException(e);
        } finally {
            if (con != null) {
                System.out.println("worked DB [from infdb.java]");
            } else {
                System.out.println("didnt work [from infdb.java]");
            }
        }
    }

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

    private void closeConnection() {
    }

    public String fetchSingle(String query) throws InfException {
        String result = null;
        try {
            Statement sm = con.createStatement();
            ResultSet rs = sm.executeQuery(query);
            //rs.next();
            if (rs.next()) {
                result = rs.getString(1);
            }
        } catch (SQLException e) {
            throw new InfException("fetchSingle statement didn't work - check your query");
        }
        return result;
    }

    public ArrayList<String> fetchColumn(String query) throws InfException {
        ArrayList<String> result = null;
        try {
            Statement sm = con.createStatement();
            ResultSet rs = sm.executeQuery(query);
            while (rs.next()) {
                if(result==null){
                    result=new ArrayList<String>();
                }
                result.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new InfException("fetchColumn statement didn't work - check your query");
        }
        return result;
    }

    public HashMap<String, String> fetchRow(String query) throws InfException {
        HashMap<String, String> result = null;
        try {
            Statement sm = con.createStatement();
            ResultSet rs = sm.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int countColumns = rsmd.getColumnCount();
            int i = 1;
            if (rs.next()) {
                if(result==null) {
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

    public ArrayList<HashMap<String, String>> fetchRows(String query) throws InfException {
        ArrayList<HashMap<String, String>> result = null;
        try {
            Statement sm = con.createStatement();
            ResultSet rs = sm.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int countColumns = rsmd.getColumnCount();
            while (rs.next()) {
                if (result==null){
                    result=new ArrayList<HashMap<String, String>>();
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

    public String getAutoIncrement(String table,String attribut) throws InfException{
        String result=null;
        String query="SELECT "+attribut+" FROM "+table+" ORDER BY "+attribut+" DESC";
        try{
            Statement sm=con.createStatement();
            ResultSet rs=sm.executeQuery(query);
            if(rs.next()){
                String inc=rs.getString(1);
                if(inc.matches("\\d+")) {
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
}