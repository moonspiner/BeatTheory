package com.fw.listenup.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class DAOBase {
    protected final static Logger logger = (Logger) LoggerFactory.getLogger(DAOBase.class);
    protected final String url = "jdbc:mysql://localhost:3306/lusit";
    protected final String root = "root";
    protected final String db_pw = "istyv123";

    protected Connection getConnection(){
        try{
            Connection con = DriverManager.getConnection(url, root, db_pw);
            return con;
        } catch(SQLException e){
            logConnectionError(e);
            return null;
        }
    }

    protected void logConnectionError(SQLException e){
        logger.error("Error with opening connection: " + e.toString());
    }
}
