package com.fw.listenup.services;

import ch.qos.logback.classic.Logger;

import java.sql.Blob;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fw.listenup.dao.UserDAO;
import com.fw.listenup.models.user.UserRecord;

@Service
public class UserService {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(UserService.class);
    private final UserDAO dao;

    @Autowired
    public UserService(UserDAO dao){
        this.dao = dao;
    }

    //Service call for getting the date a user joined
    public String getUserJoinedDate(String username){
        //Get the date value and format it
        Date userJoinedDate = dao.getUserJoinedDate(username);
        if(userJoinedDate != null){
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            String formattedDate = sdf.format(userJoinedDate);
            return formattedDate;
        } else{
            logger.error("Returning empty user joined date");
            return "";
        }
    }

    //Service call for getting the profile image of a user
    public Blob getUserProfilePicture(String username){
        logger.info("Inside of userProfilePicture service call");
        Blob profilePic = dao.getUserProfilePicture(username);
        return profilePic;
    }

    //Service call for setting a new profile picture for the user
    public boolean setUserProfilePicture(byte[] img, String username){
        if(img.length < 1){
            logger.error("The image string is empty");
            return false;
        }
        boolean picIsSet = dao.setUserProfilePicture(img, username);
        return picIsSet;
    }

    //Lists all user records in the database
    public ArrayList<UserRecord> getUserList(){
        return dao.getUserList();
    }
}
