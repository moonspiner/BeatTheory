package com.fw.beattheory.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.fw.beattheory.models.game.LeaderboardRecord;

//DAO class that handles db transactions related to game functionality
@Repository
public class GameDAO extends DAOBase{
    private static final Logger logger = (Logger) LoggerFactory.getLogger(GameDAO.class);

    //Returns all of the records in the scores table
    public ArrayList<LeaderboardRecord> getLeaderboardRecords(){
        ArrayList<LeaderboardRecord> scores = new ArrayList<LeaderboardRecord>();
        try(Connection con = getConnection()){
            logger.info("Calling db for score details");
            String query = "WITH RankedScores AS ( " +
                    "SELECT " +
                    "    scores.game_id, " +
                    "    u.username, " +
                    "    scores.score, " +
                    "    scores.difficulty, " +
                    "    scores.total_correct, " +
                    "    scores.total_attempted, " +
                    "    scores.accuracy, " +
                    "    scores.time_submitted, " +
                    "    ROW_NUMBER() OVER (PARTITION BY scores.game_id, u.username, scores.difficulty ORDER BY scores.score DESC, scores.time_submitted DESC) AS rn " +
                    "FROM scores " +
                    "INNER JOIN user AS u ON scores.user_id = u.id " +
                ") " +
                "SELECT " + 
                "    game_id, " +
                "    username, " + 
                "    score, " +
                "    difficulty, " +
                "    total_correct, " +
                "    total_attempted, " +
                "    accuracy, " +
                "    time_submitted " +
                "FROM RankedScores " +
                "WHERE rn = 1 " +
                "ORDER BY game_id asc, score desc";
                
            PreparedStatement stmt = con.prepareStatement(query);
            
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String gameId = rs.getString("game_id");
                String username = rs.getString("username");
                int score = rs.getInt("score");
                int difficulty = rs.getInt("difficulty");
                int totalCorrect = rs.getInt("total_correct");
                int totalAttempted = rs.getInt("total_attempted");
                String accuracy = rs.getString("accuracy");
                Date timestamp = rs.getDate("time_submitted");

                LeaderboardRecord record = new LeaderboardRecord(gameId, username, score, difficulty, totalCorrect, totalAttempted, accuracy, timestamp);
                scores.add(record);
            }
        } catch(SQLException e){
            logConnectionError(e);
        }

        return scores;
    }

    //Returns all of the best records for a specific user
    public ArrayList<LeaderboardRecord> getUserLeaderboardRecords(String username) {
        ArrayList<LeaderboardRecord> scores = new ArrayList<LeaderboardRecord>();
        try(Connection con = getConnection()){
            logger.info("Calling db for score details");
            // String query = "SELECT s.game_id, s.score, s.difficulty, s.total_correct, s.total_attempted, s.accuracy, s.time_submitted " +
            //     "FROM ( " +
            //         "SELECT scores.game_id, scores.user_id, scores.difficulty, MAX(scores.score) AS max_score " +
            //         "FROM scores " +
            //         "INNER JOIN user ON scores.user_id = user.id " +
            //         "WHERE username = ? " +
            //         "GROUP BY scores.game_id, scores.difficulty, scores.user_id " +
            //     ") AS max_scores " +
            //     "INNER JOIN scores AS s ON max_scores.game_id = s.game_id AND max_scores.max_score = s.score AND max_scores.user_id = s.user_id " +
            //     "ORDER BY s.game_id";
            String query = "SELECT     s.game_id,     s.score,     s.difficulty,     s.total_correct,     s.total_attempted,     s.accuracy,     s.time_submitted FROM " + 
            "(    SELECT         scores.game_id,         scores.difficulty,         MAX(scores.score) AS max_score, MAX(scores.accuracy) as max_accuracy  FROM scores    " + 
            "INNER JOIN user ON scores.user_id = user.id    WHERE user.username = ?    GROUP BY scores.game_id, scores.difficulty) AS max_scores INNER JOIN scores AS s     " + 
            "ON max_scores.game_id = s.game_id     AND max_scores.difficulty = s.difficulty    AND max_scores.max_score = s.score AND max_scores.max_accuracy = s.accuracy ORDER BY s.game_id, s.difficulty";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            int count = 0;
            while(rs.next()){
                count++;
                String gameId = rs.getString("game_id");
                int score = rs.getInt("score");
                int difficulty = rs.getInt("difficulty");
                int totalCorrect = rs.getInt("total_correct");
                int totalAttempted = rs.getInt("total_attempted");
                String accuracy = rs.getString("accuracy");
                Date timestamp = rs.getDate("time_submitted");

                LeaderboardRecord record = new LeaderboardRecord(gameId, username, score, difficulty, totalCorrect, totalAttempted, accuracy, timestamp);
                scores.add(record);
            }

            if(count < 1){
                logger.error("NO VALUES RETURNED");
            }

            
        } catch(SQLException e){
            logConnectionError(e);
        }

        return scores;
    }

    //Sets a new record in the scores table
    public boolean setScore(int gameId, int userId, int totalCorrect, int totalAttempted, String accuracy, Date timestamp, int difficulty, int score){
        boolean res = false;
        try(Connection con = getConnection()){
            logger.info("Setting new score record");
            String query = "insert into scores (game_id, user_id, total_correct, total_attempted, accuracy, time_submitted, difficulty, score) " + 
            "values (?,?,?,?,?,?,?,?)";

            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1, gameId);
            stmt.setInt(2, userId);
            stmt.setInt(3, totalCorrect);
            stmt.setInt(4, totalAttempted);
            stmt.setString(5, accuracy);
            stmt.setDate(6, timestamp);
            stmt.setInt(7, difficulty);
            stmt.setInt(8, score);
            stmt.execute();
            
            res = true;
        } catch(SQLException e){
            logConnectionError(e);
        }
        return res;
    }

    
}
