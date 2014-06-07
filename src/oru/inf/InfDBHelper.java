package oru.inf;

import java.util.HashMap;

/**
 * Created by Nicklas on 2014-06-05. Project: InfDB.
 */
public final class InfDBHelper {

    private InfDBHelper(){
    // we cant instance this class
    }

    static public HashMap<String, Object> getAdvanceProperties(){

        HashMap<String,Object> res=new HashMap<>();

        res.put("EMBEDDED", new Boolean(false));
        res.put("COLUMNLABELFORNAME", new Boolean(false));
        res.put("USER", "SYSDBA");
        res.put("PASSWORD", "masterkey");
        res.put("HOST","LOCALHOST");

        return res;
    }
}
