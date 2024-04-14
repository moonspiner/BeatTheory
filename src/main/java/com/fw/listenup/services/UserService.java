package com.fw.listenup.services;

import ch.qos.logback.classic.Logger;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fw.listenup.dao.UserDAO;

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
}
