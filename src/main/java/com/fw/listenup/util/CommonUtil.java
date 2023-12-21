package com.fw.listenup.util;

import java.sql.Timestamp;
import java.util.Date;

//Collection of static helper methods to be leveraged across the program
public class CommonUtil {
    public static boolean isEmpty(String str){
        return str.equals("") || str == null;
    }

    public static boolean tokenIsValid(Timestamp ts){
        Date currentTime = new Date();
        return currentTime.before(ts);
    }
}
