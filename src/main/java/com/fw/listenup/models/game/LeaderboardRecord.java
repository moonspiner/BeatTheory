package com.fw.listenup.models.game;

import java.sql.Date;

//POJO class for encapsulating leaderboard details
public class LeaderboardRecord {
    private String gameId;
    private String username;
    private int score;
    private int totalCorrect;
    private int totalAttempted;
    private String accuracy;
    private Date timestamp;
    private int rank;

    public LeaderboardRecord(String gameId, String username, int score, int totalCorrect, int totalAttempted,
                            String accuracy, Date timestamp){
        this.gameId = gameId;
        this.username = username;
        this.score = score;
        this.totalCorrect = totalCorrect;
        this.totalAttempted = totalAttempted;
        this.accuracy = accuracy;
        this.timestamp = timestamp;
        this.rank = 0;
    }


    public String getGameId() {
        return this.gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore(){
        return this.score;
    }

    public void setScore(int score){
        this.score = score;
    }

    public int getTotalCorrect() {
        return this.totalCorrect;
    }

    public void setTotalCorrect(int totalCorrect) {
        this.totalCorrect = totalCorrect;
    }

    public int getTotalAttempted() {
        return this.totalAttempted;
    }

    public void setTotalAttempted(int totalAttempted) {
        this.totalAttempted = totalAttempted;
    }

    public String getAccuracy() {
        return this.accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public void setTimeElapsed(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }


    @Override
    public String toString() {
        return "{" +
            " gameId='" + getGameId() + "'" +
            ", username='" + getUsername() + "'" +
            ", score='" + getScore() + "'" +
            ", totalCorrect='" + getTotalCorrect() + "'" +
            ", totalAttempted='" + getTotalAttempted() + "'" +
            ", accuracy='" + getAccuracy() + "'" +
            ", timestamp='" + getTimestamp() + "'" +
            ", rank='" + getRank() + "'" +
            "}";
    }


    
}
