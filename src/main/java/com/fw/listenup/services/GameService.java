package com.fw.listenup.services;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fw.listenup.dao.GameDAO;
import com.fw.listenup.models.game.LeaderboardRecord;

import ch.qos.logback.classic.Logger;


@Service
public class GameService {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(GameService.class);
    private final GameDAO dao;

    @Autowired
    public GameService(GameDAO gameDAO){
        this.dao = gameDAO;
    }
    //Service call for getting the records from the scores table
    public ArrayList<LeaderboardRecord> getLeaderboardRecords(){
        return dao.getLeaderboardRecords();
    }

    //Service call for getting the score records for a specific user
    public ArrayList<LeaderboardRecord> getUserLeaderboardRecords(String username){
        logger.info("Inside service method");
        return dao.getUserLeaderboardRecords(username);
    }

    //Service call for setting a new record in the scores table
    public boolean setScore(int gameId, int userId, int totalCorrect, int totalAttempted, String accuracy, String timestamp, int difficulty, int score){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try{
            Date date = new Date(format.parse(timestamp).getTime());
            return dao.setScore(gameId, userId, totalCorrect, totalAttempted, accuracy, date, difficulty, score);
        } catch(ParseException e){
            logger.error("error with parsing the date: " + e.toString());
            return false;
        }
    }
}
