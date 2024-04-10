package com.fw.listenup.services;

import java.sql.Date;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fw.listenup.dao.GameDAO;
import com.fw.listenup.models.game.LeaderboardRecord;


@Service
public class GameService {
    // private static final Logger logger = (Logger) LoggerFactory.getLogger(GameService.class);
    private final GameDAO dao;

    @Autowired
    public GameService(GameDAO gameDAO){
        this.dao = gameDAO;
    }
    //Service call for getting the records from the scores table
    public ArrayList<LeaderboardRecord> getLeaderboardRecords(){
        return dao.getLeaderboardRecords();
    }

    //Service call for setting a new record in the scores table
    public boolean setScore(int gameId, int userId, int totalCorrect, int totalAttempted, String accuracy, Date timestamp){
        return dao.setScore(gameId, userId, totalCorrect, totalAttempted, accuracy, timestamp);
    }
}
