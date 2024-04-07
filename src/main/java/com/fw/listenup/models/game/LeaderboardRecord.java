package com.fw.listenup.models.game;

import java.sql.Date;

//POJO class for encapsulating leaderboard details
public class LeaderboardRecord {
    private int gameId;
    private int userId;
    private int totalCorrect;
    private int totalAttempted;
    private String accuracy;
    private Date timestamp;
    private int rank;

    public LeaderboardRecord(int gameId, int userId, int totalCorrect, int totalAttempted,
                            String accuracy, Date timestamp){
        this.gameId = gameId;
        this.userId = userId;
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

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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
            ", userId='" + getUserId() + "'" +
            ", totalCorrect='" + getTotalCorrect() + "'" +
            ", totalAttempted='" + getTotalAttempted() + "'" +
            ", accuracy='" + getAccuracy() + "'" +
            ", timeElapsed='" + getTimestamp() + "'" +
            ", rank='" + getRank() + "'" +
            "}";
    }


    
}
