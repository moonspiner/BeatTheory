package com.fw.listenup.controllers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @GetMapping("{username}/joinedDate")
    public Map<String, String> getUserJoinedDate(@PathVariable String username){
        logger.info("Request for user joined date has been made");
        Map<String, String> res = new HashMap<String, String>();
        String userJoinedDate = this.userService.getUserJoinedDate(username);

        if (!CommonUtil.isEmpty(userJoinedDate)) logger.error("User joined date successfully retrieved");
        res.put("joinedDate", userJoinedDate);
        return res;
    }
}
