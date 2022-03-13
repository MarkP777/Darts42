package com.gmp.android.darts42;

import java.util.List;

public class Match {

    private String player0Id;
    private String player1Id;
    private Integer bestOfSets;
    private Integer bestOfLegs;
    private Integer startingPoints;
    private Boolean startWithDouble;
    private Boolean endWithDouble;
    private List<Integer> setScores;
    private List<Integer> legScores;
    private Integer numberOfScoreRecords;


    public Match() {
    }

    public Match(
            String player0Id,
            String player1Id,
            Integer bestOfSets,
            Integer bestOfLegs,
            Integer startingPoints,
            Boolean startWithDouble,
            Boolean endWithDouble,
            List<Integer> setScores,
            List<Integer> legScores,
            Integer numberOfScoreRecords) {

        this.player0Id = player0Id;
        this.player1Id = player1Id;
        this.bestOfSets = bestOfSets;
        this.bestOfLegs = bestOfLegs;
        this.startingPoints = startingPoints;
        this.startWithDouble = startWithDouble;
        this.endWithDouble = endWithDouble;
        this.legScores = legScores;
        this.setScores = setScores;
        this.numberOfScoreRecords = numberOfScoreRecords;
    }

    public String getPlayer0Id() {
        return player0Id;
    }

    public String getPlayer1Id() {
        return player1Id;
    }

    public Integer getBestOfSets() {
        return bestOfSets;
    }

    public Integer getBestOfLegs() {
        return bestOfLegs;
    }

    public Integer getStartingPoints() {
        return startingPoints;
    }

    public Boolean getStartWithDouble() {
        return startWithDouble;
    }

    public Boolean getEndWithDouble() {
        return endWithDouble;
    }

    public List<Integer> getSetScores() {
        return setScores;
    }

    public List<Integer> getLegScores() {
        return legScores;
    }

    public Integer getNumberOfScoreRecords() { return numberOfScoreRecords; }

    public void setPlayer0Id(String player0Id) {
        this.player1Id = player0Id;
    }

    public void setPlayer1Id(String player1Id) {
        this.player1Id = player1Id;
    }

    public void setBestOfSets(Integer bestOfSets) {
        this.bestOfSets = bestOfSets;
    }

    public void setBestOfLegs(Integer bestOfLegs) {
        this.bestOfLegs = bestOfLegs;
    }

    public void setStartingPoints(Integer startingPoints) {
        this.startingPoints = startingPoints;
    }

    public void setStartWithDouble(Boolean startWithDouble) {
        this.startWithDouble = startWithDouble;
    }

    public void setEndWithDouble(Boolean endWithDouble) {
        this.endWithDouble = endWithDouble;
    }

    public void setSetScores(List<Integer> setScores) {
        this.setScores = setScores;
    }

    public void setLegScores(List<Integer> legScores) {
        this.legScores = legScores;
    }

    public void setNumberOfScoreRecords(Integer numberOfScoreRecords) { this.numberOfScoreRecords = numberOfScoreRecords; }

    public void incrementNumberOfScoreRecords() { numberOfScoreRecords++; }
}


