package com.fw.listenup.models.game;

import java.sql.Date;

//POJO class for encapsulating leaderboard details
public class LeaderboardRecord {
    private int gameId;
    private String username;
    private int totalCorrect;
    private int totalAttempted;
    private String accuracy;
    private Date timestamp;
    private int rank;

    public LeaderboardRecord(int gameId, String username, int totalCorrect, int totalAttempted,
                            String accuracy, Date timestamp){
        this.gameId = gameId;
        this.username = username;
        this.totalCorrect = totalCorrect;
        this.totalAttempted = totalAttempted;
        this.accuracy = accuracy;
        this.timestamp = timestamp;
        this.rank = 0;
    }


    public int getGameId() {
        return this.gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
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
            ", totalCorrect='" + getTotalCorrect() + "'" +
            ", totalAttempted='" + getTotalAttempted() + "'" +
            ", accuracy='" + getAccuracy() + "'" +
            ", timeElapsed='" + getTimestamp() + "'" +
            ", rank='" + getRank() + "'" +
            "}";
    }


    
}
