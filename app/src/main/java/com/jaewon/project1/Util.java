package com.jaewon.project1;

import android.util.Log;

/**
 * Created by won on 2015-07-25.
 */
public class Util {

    private static Util m_instance;
    private static Boolean DEBUG = true;
    private static String TAG = "Util";

    private static final Boolean DEBUGMODE = true;

    private Util() {
        printLog(DEBUG, TAG, "[Util]");
    }

    // DEBUG : Util [Util];

    public static Util getInstance() {
        if(m_instance== null) {
            m_instance = new Util();
        }
        return m_instance;
    }
    public void printLog(boolean bPrint, String tag, String msg) {
        if (DEBUGMODE) {
            if(bPrint) {
                Log.d(tag, msg);
            }
        }
    }
}
