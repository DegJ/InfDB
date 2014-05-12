import oru.inf.InfDB;
import oru.inf.InfException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nicklas on 2014-05-11.
 */
public class Test {
    private InfDB db;

    public Test(){
        try {
            db=new InfDB("/Users/Nicklas/Skolan/SUP/ECA.FDB");
            System.out.println("worked [from test.java]");
        } catch (InfException e) {
            e.printStackTrace();
            System.out.println("didnt work [from test.java]");
        }
    }
    public void testFetchSingle(){
        String text="";
        try{
            text=db.fetchSingle("SELECT namn from AGENT where AID=1");
        } catch(InfException e){
            e.getMessage();
        }
        if(text!=null) System.out.println(text);
    }
    private void testFetchColumn() {
        ArrayList<String> arryn = new ArrayList<String>();
        try {
            arryn = db.fetchColumn("Select namn from Agent");
        } catch (InfException e) {
            e.getMessage();
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

    private void testInsert(){
        try{
            db.insert("insert into agent values ("+db.getAutoIncrement("agent","aid")+",'name',13371337,'a@a.com')");
            System.out.println("inserted");
        } catch (InfException e) {
            e.printError();
        }
    }

    private void testDelete(){
        try{
            db.delete("delete from agent where aid=6");
            System.out.println("deleted");
        } catch (InfException e) {
            e.printStackTrace();
        }
    }
    private void testUpdate(){
        try{
            db.update("Update agent set namn='Pelle' where aid=5");
            System.out.println("updated");
        } catch (InfException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Test something=new Test();
       something.testFetchSingle();
       something.testFetchColumn();
       something.testFetchRow();
       something.testFetchRows();
       something.testGetAutoIncrement();
       something.testInsert();
       something.testDelete();
       something.testUpdate();
    }




}
