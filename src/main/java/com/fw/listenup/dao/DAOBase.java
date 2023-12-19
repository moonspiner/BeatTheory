package com.fw.listenup.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAOBase {
    protected final String url = "jdbc:mysql://localhost:3306/lusit";
    protected final String root = "root";
    protected final String db_pw = "3y9buy2PjDpKToVf9T~~";

    protected Connection getConnection(){
        try{
            Connection con = DriverManager.getConnection(url, root, db_pw);
            return con;
        } catch(SQLException e){
            System.out.println("There was an error with opening the connection: " + e.toString());
            return null;
        }
    }
}
