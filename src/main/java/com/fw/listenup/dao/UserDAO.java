package com.fw.listenup.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import ch.qos.logback.classic.Logger;

@Repository
public class UserDAO extends DAOBase{
    private static final Logger logger = (Logger) LoggerFactory.getLogger(GameDAO.class);

    public Date getUserJoinedDate(String username) {
        logger.info("Attempting to get date joined for user " + username);
        try(Connection con = getConnection()){
            String query = "select created_at from user where username = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                Date res = rs.getDate("created_at");
                return res;
            }

        } catch(SQLException e){
            logConnectionError(e);
        }
        logger.error("No date joined found for user " + username);
        return null;
    }

}
