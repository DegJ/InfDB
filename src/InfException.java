/**
 * Created by Nicklas on 2014-05-11.
 */
public class InfException extends java.sql.SQLException {

    private final String prefix = "[InformatikDB] ";
    private String msg;

    public InfException(Exception e){
        super(e);
        msg=super.getMessage();
        System.err.println(prefix+"Something went wrong with the database: cause ->");
        System.err.println(prefix+getMsg());

    }
    public InfException(String e){
        super(e);
        msg=super.getMessage();
        System.err.println(prefix+"Something went wrong with the database: cause ->");
        System.err.println(prefix+getMsg());

    }
    public String getMsg(){
        return msg;
    }
}