package com.fw.listenup.controllers;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fw.listenup.dao.LoggingDAO;
import com.fw.listenup.models.Test;

@RestController
@RequestMapping("/api/v1/logging")
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