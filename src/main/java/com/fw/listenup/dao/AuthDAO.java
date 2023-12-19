package com.fw.listenup.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.fw.listenup.models.auth.UserAuthenticationDetail;

//DAO class that handles db transactions related to authentication
@Repository
public class AuthDAO extends DAOBase{

    //Returns the username and password of a user based on email 
    public UserAuthenticationDetail getUserAuthenticationDetail(String email){
        UserAuthenticationDetail uad = null;
        try(Connection con = getConnection()){
            PreparedStatement stmt = con.prepareStatement("select email, password from user where email = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            //Should only return one value since email is a unique field
            if(rs.next()){
                String username = rs.getString("email");
                String pw = rs.getString("password");
                uad = new UserAuthenticationDetail(username, pw);
            }
        } catch(SQLException e){
            System.out.println("Error with opening connection: " + e.toString());
        }

        return uad;
    }

    //Returns the username and email from the user table
    public Map<String, String> getExistingEmailAndUsername(String email, String username){
        Map<String, String> credMap = new HashMap<String, String>();
        String existingEmail = "";
        String existingUsername = "";
        try(Connection con = getConnection()){
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

            System.out.println("Existing email and username: " + existingEmail + " : " + existingUsername);
        } catch(SQLException e){
            System.out.println("Error with opening connection " + e.toString());
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
                System.out.println("User was inserted");
                res = true;
            } else {
                System.out.println("Something went wrong, no user was inserted");
            }

        } catch(SQLException e){
            System.out.println("Error with executing insert for new user: " + e.toString());
        }

        return res;
    }
}
