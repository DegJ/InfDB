import se.oru.informatik.InformatikDB;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nicklas on 2014-05-11.
 */
public class Test {
    private InfDB db;
    private InformatikDB dba;

    public Test(){
        try {
            db=new InfDB("C:/Skolan/ECA.FDB");
            System.out.println("worked [from test.java]");
        } catch (InfException e) {
            //e.printStackTrace();
            System.out.println("didnt work [from test.java]");
        }
    }
    public void testFetchSingle(){
        String text="";
        try{
            text=db.fetchSingle("SELECT namn from AGENT where AID=1");
        } catch(InfException e){
            e.getMsg();
        }
        if(text!=null) System.out.println(text);
    }
    private void testFetchColumn() {
        ArrayList<String> arryn = new ArrayList<String>();
        try {
            arryn = db.fetchColumn("Select namn from Agent");
        } catch (InfException e) {
            e.getMsg();
        }
        for(String e:arryn){
            System.out.println(e);
        }
    }
    private void testFetchRow() {
        HashMap<String, String> hm = new HashMap<String, String>();
        try{
            hm= db.fetchRow("select * from agent where aid=1");
        } catch (InfException e) {
            e.printStackTrace();
        }
        for(String e:hm.values()){
            System.out.println(e);
        }
    }

    private void testFetchRows() {
        ArrayList<HashMap<String, String>> alhm = new ArrayList<HashMap<String, String>>();
        try{
            alhm=db.fetchRows("select * from agent");
        } catch (InfException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < alhm.size() ; i++) {
            HashMap<String, String> hm = alhm.get(i);
            for (String e : hm.values()) {
                System.out.println(e);
            }
        }

    }
    private void testGetAutoIncrement(){
        String res="";
        try{
            res=db.getAutoIncrement("agent", "aid");
        } catch (InfException e) {
            e.printStackTrace();
        }
        if(res!=null)System.out.println(res);
    }

    public static void main(String[] args) {
        Test something=new Test();
        //something.testFetchSingle();
       // something.testFetchColumn();
       // something.testFetchRow();
        something.testFetchRows();
        something.testGetAutoIncrement();
    }




}
