package oru.inf;

import java.util.HashMap;

/**
 * Created by Nicklas on 2014-06-05. Project: InfDB.
 */
public final class InfDBHelper {

    private InfDBHelper(){
    // we cant instance this class
    }

    static public HashMap<String, Object> getAdvanceParams(){

        HashMap<String,Object> res=new HashMap<>();

        res.put("EMBEDDED", new Boolean(false));
        res.put("COLUMNLABELFORNAME", new Boolean(true));
        res.put("ENCODING","UTF8");
        res.put("USER", "SYSDBA");
        res.put("PASSWORD", "masterkey");
        res.put("HOST","localhost:3050");

        return res;
    }

    static public boolean advanceParmsCorrect(){
        boolean res=false;
        return res;
    }
}
