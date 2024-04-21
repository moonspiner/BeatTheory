package com.fw.listenup.util;

import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.sql.rowset.serial.SerialBlob;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

//Collection of static helper methods to be leveraged across the program
public class CommonUtil {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(CommonUtil.class);
    //Checks if string is empty or null
    public static boolean isEmpty(String str){
        return str.equals("") || str == null;
    }

    //Validates token
    public static boolean tokenIsValid(Timestamp ts){
        Date currentTime = new Date();
        return currentTime.before(ts);
    }

    //Convert a string to a blob object
    public static Blob convertStringToBlob(String str){
        try{
            byte[] blobBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(str);
            // InputStream inputStream = new ByteArrayInputStream(blobBytes);
            Blob blob = new SerialBlob(blobBytes);
            return blob;
        } catch(SQLException e){
            logger.error("There was an error with converting the string to a blob: " + e.toString());
        }
        return null;
    }
}
