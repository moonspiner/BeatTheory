package com.fw.beattheory.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fw.beattheory.models.game.LeaderboardRecord;
import com.fw.beattheory.services.GameService;

import ch.qos.logback.classic.Logger;


@RestController
@RequestMapping("/api/v1/game/")
@CrossOrigin(origins = "https://beattheorymusic.com")
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

    //Returns the records of all the scores for a specific user
    @GetMapping("scores/{username}")
    public ResponseEntity<ArrayList<LeaderboardRecord>> getUserScores(@PathVariable String username){
        logger.info("Retrieving records for user " + username);
        ArrayList<LeaderboardRecord> scores = this.gameService.getUserLeaderboardRecords(username);
        if(scores.size() > 0) logger.info("Score successfully retrieved");
        if(scores != null) logger.info("User " + username + " does not have any score records");
        return ResponseEntity.ok(scores);
    }

    //Sets a new record into the score table
    @PostMapping("scores/setScore")
    public Map<String, Boolean> setScore(@RequestParam int gameId, @RequestParam int userId, @RequestParam int totalCorrect,
                                      @RequestParam int totalAttempted, @RequestParam String accuracy, @RequestParam String timestamp,
                                      @RequestParam int difficulty, @RequestParam int score){
        Map<String, Boolean> res = new HashMap<String, Boolean>();
        boolean val = this.gameService.setScore(gameId, userId, totalCorrect, totalAttempted, accuracy, timestamp, difficulty, score);
        res.put("scoreSet", val);
        return res;
    }


}
