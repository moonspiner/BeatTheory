package com.fw.listenup.controllers;

import java.sql.Blob;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fw.listenup.services.UserService;
import com.fw.listenup.util.CommonUtil;

import ch.qos.logback.classic.Logger;

//Controller class for making user-related db transactions
@RestController
@RequestMapping("/api/v1/user/")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    private final UserService userService;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    //Returns the joined date for the specified user
    @GetMapping("{username}/profileDetails")
    public Map<String, Object> getUserProfileDetails(@PathVariable String username){
        logger.info("Request for user joined date has been made");
        Map<String, Object> res = new HashMap<String, Object>();
        String userJoinedDate = this.userService.getUserJoinedDate(username);
        Blob profilePic = this.userService.getUserProfilePicture(username);
        if (!CommonUtil.isEmpty(userJoinedDate)) logger.info("User joined date successfully retrieved");
        
        res.put("joinedDate", userJoinedDate);
        res.put("profilePicture", profilePic);
        return res;
    }

    //Uploads a new profile picture for the user
    public Map<String, Boolean> uploadProfilePicture(@RequestParam Blob newPic, @RequestParam String username){
        logger.info("Attempting to upload new profile picture for user " + username);
        Map<String, Boolean> res = new HashMap<String, Boolean>();
        boolean updateSuccessful = this.userService.setUserProfilePicture(newPic, username);
        if(updateSuccessful) logger.info("User profile picture change was successful");

        res.put("updateSuccessful", updateSuccessful);
        return res;
    }
}
