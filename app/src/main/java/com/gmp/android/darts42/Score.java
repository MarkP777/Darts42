package com.gmp.android.darts42;

import java.util.List;

public class Score {

    private String thrower;
    private String throwString;
    private Integer throwScore;
    private List<Integer> totalScore;
    private List<Boolean> hasStarted;


    public Score() {
    }

    public Score(String thrower, String throwString, Integer throwScore, List<Integer> totalScore, List<Boolean> hasStarted) {
        this.thrower = thrower;
        this.throwString = throwString;
        this.throwScore = throwScore;
        this.totalScore = totalScore;
        this.hasStarted = hasStarted;
    }

    public String getThrower() {
        return thrower;
    }

    public String getThrowString() {return throwString;}

    public Integer getThrowScore() {
        return throwScore;
    }

    public List<Integer> getTotalScore() {
        return totalScore;
    }

    public List<Boolean> getHasStarted() {
        return hasStarted;
    }


    public void setThrower(String thrower) {
        this.thrower = thrower;
    }

    public void setThrowString(String throwString) {
        this.throwString = throwString;
    }

    public void setThrowScore(Integer throwScore) {
        this.throwScore = throwScore;
    }

    public void setTotalScore(List<Integer> totalScore) {
        this.totalScore = totalScore;
    }

    public void setHasStarted(List<Boolean> hasStarted) {
        this.hasStarted = hasStarted;
    }
}

/*
public class Score {

    private String thrower;
    private String throwString;
    private Integer throwScore;
    private Integer player1TotalScore;
    private Integer player2TotalScore;
    private Boolean player1HasStarted;
    private Boolean player2HasStarted;


    public Score() {
    }

    public Score(String thrower, String throwString, Integer throwScore, Integer player1TotalScore, Integer player2TotalScore, Boolean player1HasStarted, Boolean player2HasStarted) {
        this.thrower = thrower;
        this.throwString = throwString;
        this.throwScore = throwScore;
        this.player1TotalScore = player1TotalScore;
        this.player2TotalScore = player2TotalScore;
        this.player1HasStarted = player1HasStarted;
        this.player2HasStarted = player2HasStarted;
     }

    public String getThrower() {
        return thrower;
    }

    public String getThrowString() {return throwString;}

    public Integer getThrowScore() {
        return throwScore;
    }

    public Integer getPlayer1TotalScore() {return player1TotalScore;}

    public Integer getPlayer2TotalScore() {return player2TotalScore;}

    public Boolean getPlayer1HasStarted() {return player1HasStarted;}

    public Boolean getPlayer2HasStarted() {return player2HasStarted;}

    public void setThrower(String thrower) {
        this.thrower = thrower;
    }

    public void setThrowString(String throwString) {
        this.throwString = throwString;
    }

    public void setThrowScore(Integer throwScore) {
        this.throwScore = throwScore;
    }

    public void setPlayer1TotalScore(Integer player1TotalScore) {this.player1TotalScore = player1TotalScore;}

    public void setPlayer2TotalScore(Integer player2TotalScore) {this.player2TotalScore = player2TotalScore;}

    public void setPlayer1HasStarted(Boolean player1HasStarted) {this.player1HasStarted = player1HasStarted;}

    public void setPlayer2HasStarted(Boolean player2HasStarted) {this.player2HasStarted = player2HasStarted;}
}

 */