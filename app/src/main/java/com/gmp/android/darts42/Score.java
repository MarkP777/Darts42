package com.gmp.android.darts42;

public class Score {

    private String thrower;
    private String throwString;
    private Integer throwScore;
    private Integer totalScore;


    public Score() {
    }

    public Score(String thrower, String throwString, Integer throwScore, Integer totalScore) {
        this.thrower = thrower;
        this.throwString = throwString;
        this.throwScore = throwScore;
        this.totalScore = totalScore;
     }

    public String getThrower() {
        return thrower;
    }

    public String getThrowString() {return throwString;}

    public Integer getThrowScore() {
        return throwScore;
    }

    public Integer getTotalScore() {
        return totalScore;
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

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

}
