package com.fw.beattheory.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/logging")
@CrossOrigin(origins = {"https://beattheorymusic.com", "https://www.beattheorymusic.com"})

public class LoggingController{

    // private final LoggingDAO dao;

    // @Autowired
    // public LoggingController(DataSource ds){
    //     this.dao = new LoggingDAO(ds);
    // }

    // @GetMapping("/test")
    // public List<Test> test(){
        
    //     return dao.getAllTestEntities();
    // }
}
