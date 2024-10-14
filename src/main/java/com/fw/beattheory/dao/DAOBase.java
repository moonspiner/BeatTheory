package com.fw.beattheory.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import ch.qos.logback.classic.Logger;

public class DAOBase {
    protected final static Logger logger = (Logger) LoggerFactory.getLogger(DAOBase.class);
    @Value("${spring.datasource.url}")
    protected String url;

    @Value("${spring.datasource.username}")
    protected String root;

    @Value("${spring.datasource.password}")
    protected String db_pw;

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
