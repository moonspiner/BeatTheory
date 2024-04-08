package com.fw.listenup.services;

import java.util.ArrayList;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fw.listenup.dao.GameDAO;
import com.fw.listenup.models.game.LeaderboardRecord;

import ch.qos.logback.classic.Logger;

@Service
public class GameService {
    // private static final Logger logger = (Logger) LoggerFactory.getLogger(GameService.class);

    //Service call for getting the records from the scores table
    public ArrayList<LeaderboardRecord> getLeaderboardRecords(){
        GameDAO dao = new GameDAO();
        return dao.getLeaderboardRecords();
    }
}