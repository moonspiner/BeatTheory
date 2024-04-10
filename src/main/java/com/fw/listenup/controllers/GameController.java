package com.fw.listenup.controllers;

import java.util.ArrayList;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fw.listenup.models.game.LeaderboardRecord;
import com.fw.listenup.services.GameService;

import ch.qos.logback.classic.Logger;


@RestController
@RequestMapping("/api/v1/game/")
@CrossOrigin(origins = "http://localhost:4200")
public class GameController {
    private final GameService gameService;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(GameController.class);

    @Autowired
    public GameController(GameService gameService){
        this.gameService = gameService;
    }

    //Returns the records of the scores table in JSON format
    @GetMapping("scores")
    public ResponseEntity<ArrayList<LeaderboardRecord>> getScores(){
        logger.info("Request for leaderboard details has been made");
        ArrayList<LeaderboardRecord> scores = this.gameService.getLeaderboardRecords();
        //Check to see if the array list is populated
        if(scores.size() > 0){
            logger.info("Score successfully retrieved");
            return ResponseEntity.ok(scores);
        }

        //The array list is empty, so return an error
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(scores);
    }
}
