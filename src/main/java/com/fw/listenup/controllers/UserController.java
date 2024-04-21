package com.fw.listenup.controllers;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

        if(profilePic != null){
            try {
                byte[] profilePicBytes = StreamUtils.copyToByteArray(profilePic.getBinaryStream());
                res.put("profilePicture", profilePicBytes);
            } catch (SQLException | IOException e) {
                logger.error("Error converting profile picture to byte array", e);
                res.put("profilePicture", "");
            }
        }
        return res;
    }

    //Uploads a new profile picture for the user
    @PostMapping("setProfilePicture/{username}")
    public Map<String, Boolean> uploadProfilePicture(@RequestBody byte[] img, @PathVariable String username){
        System.out.println("username is " + username);
        System.out.println("Blob is " + img.toString());
        logger.info("Attempting to upload new profile picture for user " + username);
        Map<String, Boolean> res = new HashMap<String, Boolean>();
        boolean updateSuccessful = this.userService.setUserProfilePicture(img, username);
        if(updateSuccessful) logger.info("User profile picture change was successful");

        res.put("updateSuccessful", updateSuccessful);
        return res;
    }
}
