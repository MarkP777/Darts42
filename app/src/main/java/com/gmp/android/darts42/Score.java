package com.gmp.android.darts42;

import java.util.List;

public class Score {

    private String thrower;
    private String throwString;
    private Integer throwScore;
    private List<Integer> totalScore;
    private List<Boolean> hasStarted;
    private List<Integer> setScores;
    private List<Integer> legScores;
    private Boolean legFinished;


    public Score() {
    }

    public Score(String thrower, String throwString, Integer throwScore, List<Integer> totalScore, List<Boolean> hasStarted, List<Integer> setScores, List<Integer> legScores, Boolean legFinished) {
        this.thrower = thrower;
        this.throwString = throwString;
        this.throwScore = throwScore;
        this.totalScore = totalScore;
        this.hasStarted = hasStarted;
        this.setScores = setScores;
        this.legScores = legScores;
        this.legFinished = legFinished;
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

    public List<Integer> getSetScores() { return setScores; }

    public List<Integer> getLegScores() { return legScores; }

    public Boolean getLegFinished() { return legFinished; }

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

    public void setSetScores(List<Integer> setScores) { this.setScores = setScores;}

    public void setLegScores(List<Integer> legScores) {this.legScores = legScores; }

    public void setLegFinished(Boolean legFinished) {this.legFinished = legFinished; }
}
