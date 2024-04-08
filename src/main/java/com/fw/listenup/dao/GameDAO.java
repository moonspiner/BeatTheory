package com.fw.listenup.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.fw.listenup.models.game.LeaderboardRecord;

//DAO class that handles db transactions related to game functionality
@Repository
public class GameDAO extends DAOBase{
    private static final Logger logger = (Logger) LoggerFactory.getLogger(GameDAO.class);

    //Returns all of the records in the scores table
    public ArrayList<LeaderboardRecord> getLeaderboardRecords(){
        ArrayList<LeaderboardRecord> scores = new ArrayList<LeaderboardRecord>();
        try(Connection con = getConnection()){
            logger.info("Calling db for score details");
            String query = "select game_id, username, total_correct, total_attempted, " +
            "accuracy, time_submitted from ( select s.game_id, u.username, s.total_correct, " + 
            "s.total_attempted, s.accuracy, s.time_submitted, ROW_NUMBER() over (PARTITION BY " +
            "s.game_id, u.username order by s.total_correct desc) as rn from scores s " + 
            "inner join user u on s.user_id = u.id) as ranked where rn = 1";
            PreparedStatement stmt = con.prepareStatement(query);
            
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                int gameId = rs.getInt("game_id");
                String username = rs.getString("username");
                int totalCorrect = rs.getInt("total_correct");
                int totalAttempted = rs.getInt("total_attempted");
                String accuracy = rs.getString("accuracy");
                Date timestamp = rs.getDate("time_submitted");

                LeaderboardRecord record = new LeaderboardRecord(gameId, username, totalCorrect, totalAttempted, accuracy, timestamp);
                scores.add(record);
            }
        } catch(SQLException e){
            logConnectionError(e);
        }

        return scores;
    }
}
