package com.fw.listenup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.fw.listenup.models.auth.UserAuthenticationDetail;

import ch.qos.logback.classic.Logger;

//DAO class that handles db transactions related to authentication
@Repository
public class AuthDAO extends DAOBase{
    private static final Logger logger = (Logger) LoggerFactory.getLogger(AuthDAO.class);

    //Returns the username and password of a user based on email 
    public UserAuthenticationDetail getUserAuthenticationDetail(String email){
        UserAuthenticationDetail uad = null;
        try(Connection con = getConnection()){
            logger.info("Calling db for user authentication details");
            PreparedStatement stmt = con.prepareStatement("select email, password from user where email = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            //Should only return one value since email is a unique field
            if(rs.next()){
                String username = rs.getString("email");
                String pw = rs.getString("password");
                uad = new UserAuthenticationDetail(username, pw);
            } else{
                logger.error("No credential match found, invalid auth attempt");
            }
        } catch(SQLException e){
            logConnectionError(e);
        }

        return uad;
    }

    //Returns the username and email from the user table
    public Map<String, String> getExistingEmailAndUsername(String email, String username){
        Map<String, String> credMap = new HashMap<String, String>();
        String existingEmail = "";
        String existingUsername = "";
        try(Connection con = getConnection()){
            logger.info("Calling db for credential lookup");
            PreparedStatement emailStmt = con.prepareStatement("select email from user where email = ?");
            emailStmt.setString(1, email);
            
            ResultSet rs = emailStmt.executeQuery();
            if(rs.next()){
                existingEmail = rs.getString("email");
            }

            PreparedStatement usernameStmt = con.prepareStatement("select username from user where username = ?");
            usernameStmt.setString(1, username);
            
            ResultSet rs2 = usernameStmt.executeQuery();
            if(rs2.next()){
                existingUsername = rs2.getString("username");
            }

            logger.info("Existing email and username: " + existingEmail + " : " + existingUsername);
        } catch(SQLException e){
            logConnectionError(e);
        }

        credMap.put("email", existingEmail);
        credMap.put("username", existingUsername);
        return credMap;
    }

    //Makes insert statement into user table with new credentials
    public boolean registerNewUser(String email, String username, String password){
        //Predefined fields for new accounts
        boolean res = false;
        int role = 0;
        int rankId = 1;
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        int status = 1;

        try(Connection con = getConnection()){
            logger.info("Attempting to insert new user into db");
            String insertQuery = "insert into user (username, password, email, role, rank_id, created_at, status) " +
                                 "values (?,?,?,?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(insertQuery);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setInt(4, role);
            stmt.setInt(5, rankId);
            stmt.setTimestamp(6, createdAt);
            stmt.setInt(7, status);

            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected > 0){
                logger.info("New user was successfully inserted!");
                res = true;
            } else {
               logger.error("Something went wrong, no user was inserted");
            }

        } catch(SQLException e){
            logConnectionError(e);
        }

        return res;
    }

    //Makes insert statement into login_attempts table with new credentials
    public boolean logAuthAttempt(String email, String ip, boolean valid){
        boolean res = false;
        //Predefined values
        Timestamp logTime = new Timestamp(System.currentTimeMillis());

        try(Connection con = getConnection()){
            logger.info("Logging auth attempt in db");
            String query = "insert into login_attempts (login_time, ip_address, email, valid_auth) values (?,?,?,?)";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setTimestamp(1, logTime);
            stmt.setString(2, ip);
            stmt.setString(3, email);
            stmt.setBoolean(4, valid);

            int rowsAffected = stmt.executeUpdate();
            if(rowsAffected > 0){
                logger.info("Auth attempt was successful logged");
                res = true;
            } else{
                logger.error("Something went wrong, the auth attempt was not logged");
            }
        } catch(SQLException e){
            logConnectionError(e);
        }

        return res;
    }
}
