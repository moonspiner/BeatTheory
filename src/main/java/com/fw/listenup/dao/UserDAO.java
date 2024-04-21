package com.fw.listenup.dao;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import ch.qos.logback.classic.Logger;

//DAO class for making user-related database transactions
@Repository
public class UserDAO extends DAOBase{
    private static final Logger logger = (Logger) LoggerFactory.getLogger(GameDAO.class);

    //Gets the user's joined date
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

    //Gets the user's profile picture
    public Blob getUserProfilePicture(String username){
        logger.info("Attempting to get profile picture info for user " + username);
        try(Connection con = getConnection()){
            String query = "select profile_picture from user where username = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            logger.info("result set has been created");

            if(rs.next()){
                Blob res = rs.getBlob("profile_picture");
                logger.info("RES IS " + res.toString());
                return res;
            }

        } catch(SQLException e){
            logConnectionError(e);
        }
        logger.error("No profile picture found for user " + username);
        return null;
    }

    // private byte[] toByteArray(ByteArrayInputStream stream) {
    //     ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    //     int nRead;
    //     byte[] data = new byte[16384]; // You can adjust buffer size as needed

    //     try {
    //         while ((nRead = stream.read(data, 0, data.length)) != -1) {
    //             buffer.write(data, 0, nRead);
    //         }
    //         buffer.flush();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }

    //     return buffer.toByteArray();
    //}

    //Sets a new profile picture for the specified user
    public boolean setUserProfilePicture(byte[] img, String username){
        try(Connection con = getConnection()){
            logger.info("STRING VALUE IS " + img);
            String query = "update user set profile_picture = ? where username = ?";
            PreparedStatement stmt = con.prepareStatement(query);
            //Convert the string to a blob
            
            stmt.setBytes(1, img);
            stmt.setString(2, username);

            int rowsAffected = stmt.executeUpdate();
            System.out.println("ROWS AFFECTED IS " + rowsAffected);
            return rowsAffected > 0;
        } catch(SQLException e){
            logConnectionError(e);
            return false;
        }
    }

}