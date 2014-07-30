import oru.inf.InfDB;
import oru.inf.InfDBHelper;
import oru.inf.InfException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by Nicklas on 2014-05-11.
 */
public class Test {
    private InfDB db;
    private InfDB dba;

    public Test() {
        try {
            db = new InfDB("/Users/Nicklas/Skolan/INFA/SUP/ECA.FDB");
            System.out.println("worked [from test.java]");
        } catch (InfException e) {
            e.printStackTrace();
            System.out.println("didnt work [from test.java]");
        }
    }

    public void testFetchSingle() {
        String text = "";
        try {
            text = db.fetchSingle("SELECT namn from AGENT where AID=1");
        } catch (InfException e) {
            e.getMessage();
        }
        if (text != null) System.out.println(text);
    }

    private void testFetchColumn() {
        ArrayList<String> arryn = new ArrayList<String>();
        try {
            arryn = db.fetchColumn("Select namn from Agent");
        } catch (InfException e) {
            e.getMessage();
        }
        for (String e : arryn) {
            System.out.println(e);
        }
    }

    private void testFetchRow() {
        HashMap<String, String> hm = new HashMap<String, String>();
        try {
            hm = db.fetchRow("select * from agent where aid=1");
        } catch (InfException e) {
            e.printStackTrace();
        }
        for (String e : hm.values()) {
            System.out.println(e);
        }
    }

    private void testFetchRows() {
        ArrayList<HashMap<String, String>> alhm = new ArrayList<HashMap<String, String>>();
        try {
            alhm = db.fetchRows("select * from agent");
        } catch (InfException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < alhm.size(); i++) {
            HashMap<String, String> hm = alhm.get(i);
            for (String e : hm.values()) {
                System.out.println(e);
            }
        }

    }

    private void testGetAutoIncrement() {
        String res = "";
        try {
            res = db.getAutoIncrement("agent", "aid");
        } catch (InfException e) {
            e.printStackTrace();
        }
        if (res != null) System.out.println(res);
    }

    private void testInsert() {
        try {
            db.insert("insert into agent values (" + db.getAutoIncrement("agent", "aid") + ",'name',13371337,'a@a.com')");
            System.out.println("inserted");
        } catch (InfException e) {
            e.printError();
        }
    }

    private void testDelete() {
        try {
            db.delete("delete from agent where aid=6");
            System.out.println("deleted");
        } catch (InfException e) {
            e.printStackTrace();
        }
    }

    private void testUpdate() {
        try {
            db.update("Update agent set namn='Pelle' where aid=5");
            System.out.println("updated");
        } catch (InfException e) {
            e.printStackTrace();
        }
    }

    private void props(){
        HashMap<String,Object> res=InfDBHelper.getAdvanceParams();
        res.put("EMBEDDED", new Boolean(false));
        //res.put("USER",new HashMap<String,String>());
        try {
            dba = new InfDB("/Users/Nicklas/Skolan/INFA/SUP/ECA.FDB",res);
            System.out.println("wokred props db");
        } catch (InfException e){
            System.out.println("props()  + "+e);
        }
        try{
            System.out.println(dba.fetchSingle("Select namn from agent"));
        } catch (InfException e) {
            e.printStackTrace();
        }

        ArrayList<HashMap<String, String>> alhm = new ArrayList<HashMap<String, String>>();
        try {
            alhm = dba.fetchRows("select * from agent");
        } catch (InfException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < alhm.size(); i++) {
            HashMap<String, String> hm = alhm.get(i);
            for (String e : hm.values()) {
                System.out.println(e);
            }
        }
    }

    public void rstest(){
        ResultSet results= null;
        try {
            results=db.getResultSet("select * from agent");
        } catch (InfException e) {
            e.printStackTrace();
        }
        if (results != null) {
            try {
                ResultSetMetaData rsmd = results.getMetaData();
                int countColumns = rsmd.getColumnCount();

                int a=results.getRow();
                String b=results.getCursorName();
                results.next();
                int d=results.getRow();

                String c=results.getString(2);
                results.updateString(2,c+"2");
                String e=results.getString(2);
                results.refreshRow();
                String f=results.getString(2);
                results.updateString(2,c+"2");
                String g=results.getString(2);
                results.updateRow();
                results.absolute(3);
                String h=results.getString(2);
                results.previous();
                String i=results.getString(2);
                String j=results.getCursorName();

                String x=null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Test something = new Test();
        something.testFetchSingle();
        /*
        something.testFetchColumn();
        something.testFetchRow();
        something.testFetchRows();
        something.testGetAutoIncrement();
        something.testInsert();
        something.testDelete();
        something.testUpdate();
        */

        /*
        Properties p = System.getProperties();

        java.util.Enumeration keys = p.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = (String)p.get(key);
            System.out.println(key + ": " + value);
        }
        */
        something.rstest();
        /*
        System.out.println("----");
        System.out.println(System.getProperties().getProperty ("jdbc.drivers"));
        something.props();
        */
    }


}
/*
http://stackoverflow.com/questions/5419039/is-djava-library-path-equivalent-to-system-setpropertyjava-library-path
 */
/*
@todo return ResultSet method
 */