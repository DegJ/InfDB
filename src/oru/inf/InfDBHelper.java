package oru.inf;

import java.util.HashMap;

/**
 * <b>Helper class for InfDB, used to get the advanced parameters when connecting to the firebird database.</b>
 * Created by Nicklas on 2014-06-05. Project: InfDB.
 * @author Nicklas Magnusson nicmav141@studentmail.oru.se
 * @version 0.1
 */
public final class InfDBHelper {

    private InfDBHelper(){
    // we cant instance this class
    }

    /**
     * Get a HashMap containing parameters that is used to connect to a firebird database a non-default way.
     * Usage: .put with new value overriding old one.
     * Parameters:
     * COLUMNLABELFORNAME, Boolean(True|False)
     * ENCODING, String(UTF8)
     * USER, String(SYSDBA)
     * PASSWORD, String(masterkey)
     * HOST, String(localhost:3050) (host:port)
     * @return HashMap with parameters
     */
    static public HashMap<String, Object> getAdvanceParams(){

        HashMap<String,Object> res=new HashMap<String,Object>();

        //res.put("EMBEDDED", new Boolean(false));
        res.put("COLUMNLABELFORNAME", new Boolean(true));
        res.put("ENCODING","UTF8");
        res.put("USER", "SYSDBA");
        res.put("PASSWORD", "masterkey");
        res.put("HOST","localhost:3050");

        return res;
    }

    /**
     * Checks if the params instanced from getAdvanceParams are there.
     * @param params HashMap of params to check
     * @return boolean true if they were all there and were alright
     * @throws Exception if they weren't there
     */
    static public boolean advanceParmsCorrect(HashMap<String, Object> params) throws Exception{
        boolean b=params.keySet().containsAll(InfDBHelper.getAdvanceParams().keySet());
        if(!b){
            throw new Exception("Missing parameters from the map, instance defaults from InfDBHelper.getAdvanceParams()");
        }

        // basic check so the params are of the correct class

        //boolean emcheck=params.get("EMBEDDED") instanceof Boolean;
        boolean clfncheck=params.get("COLUMNLABELFORNAME") instanceof Boolean;
        boolean enccheck=params.get("ENCODING") instanceof String;
        boolean ucheck=params.get("USER") instanceof String;
        boolean pcheck=params.get("PASSWORD") instanceof String;
        boolean hcheck=params.get("HOST") instanceof String;

        boolean isok=clfncheck&&enccheck&&ucheck&&pcheck&&hcheck&&b;

        if(!isok){
            throw new Exception("Check the parameters, one or more is not correct");
        }

        return isok;
    }
}
