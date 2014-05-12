package oru.inf;

/**
 * Created by Nicklas Magnusson on 2014-05-11. Project: InfDB
 * @author Nicklas Magnusson <nicmav141@studentmail.oru.se>
 * @version 0.1
 */
public class InfException extends java.sql.SQLException {

    private final String prefix = "[InformatikDB] ";
    private String msg;

    /**
     * InfException
     * Constructor for exception
     *
     * @param e Exception object
     */
    public InfException(Exception e) {
        super(e);
        msg = super.getMessage();
    }

    /**
     * InfException
     * Constructor for exception
     *
     * @param e Exception String
     */
    public InfException(String e) {
        super(e);
        msg = super.getMessage();
    }

    /**
     * getMessage
     * Message of the error
     *
     * @return String of the error message
     */
    public String getMessage() {
        return prefix + msg;
    }

    /**
     * printError
     * prints the error to the system.err console
     */
    public void printError() {
        System.err.println(prefix + msg);
    }
}