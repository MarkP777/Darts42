package com.gmp.android.darts42;

public class Match {

    private String player1Id;
    private String player2Id;
    private Integer bestOfSets;
    private Integer bestOfLegs;
    private Integer startingPoints;
    private Boolean startWithDouble;
    private Boolean endWithDouble;
    private Integer player1Sets;
    private Integer player1Legs;
    private Integer player2Sets;
    private Integer player2Legs;

    public Match() {

    }

    public Match(
            String player1Id,
            String player2Id,
            Integer bestOfSets,
            Integer bestOfLegs,
            Integer startingPoints,
            Boolean startWithDouble,
            Boolean endWithDouble,
            Integer player1Sets,
            Integer player1Legs,
            Integer player2Sets,
            Integer player2Legs
    ) {

        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.bestOfSets = bestOfSets;
        this.bestOfLegs = bestOfLegs;
        this.startingPoints = startingPoints;
        this.startWithDouble = startWithDouble;
        this.endWithDouble = endWithDouble;
        this.player1Sets = player1Sets;
        this.player1Legs = player1Legs;
        this.player2Sets = player2Sets;
        this.player2Legs = player2Legs;
    }

    public String getPlayer1Id() {return player1Id;}
    public String getPlayer2Id() {return player2Id;}
    public Integer getBestOfSets() {return bestOfSets;}
    public Integer getBestOfLegs() {return bestOfLegs;}
    public Integer getStartingPoints() {return startingPoints;}
    public Boolean getStartWithDouble() {return startWithDouble;}
    public Boolean getEndWithDouble() {return endWithDouble;}
    public Integer getPlayer1Sets() {return player1Sets;}

    public Integer getPlayer1Legs() {
        return player1Legs;
    }

    public Integer getPlayer2Legs() {
        return player2Legs;
    }

    public Integer getPlayer2Sets() {
        return player2Sets;
    }

    public void setPlayer1Sets(Integer player1Sets) {
        this.player1Sets = player1Sets;
    }

    public void setPlayer1Legs(Integer player1Legs) {
        this.player1Legs = player1Legs;
    }

    public void setPlayer2Sets(Integer player2Sets) {
        this.player2Sets = player2Sets;
    }

    public void setPlayer2Legs(Integer player2Legs) {
        this.player2Legs = player2Legs;
    }
}


