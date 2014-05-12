/**
 * Created by Nicklas on 2014-05-11.
 */
public class InfException extends java.sql.SQLException {

    private final String prefix = "[InformatikDB] ";
    private String msg;

    public InfException(Exception e){
        super(e);
        msg=super.getMessage();
    }
    public InfException(String e){
        super(e);
        msg=super.getMessage();
    }
    public String getMessage(){
        return prefix+msg;
    }
    public void printError(){
        System.err.println(prefix+msg);
    }
}