package com.gmp.android.darts42;

// Public class for building the recycler adapter which displays the individual throw scores
public class ScoreRow {

    private String col1;
    private String col2;
    private String col3;
    private String col4;

    public ScoreRow(String col1, String col2, String col3, String col4) {
        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
        this.col4 = col4;
    }

    // Set methods not used

    public String getcol1() {
        return this.col1;
    }

    public String getcol2() {
        return this.col2;
    }

    public String getcol3() {
        return this.col3;
    }

    public String getcol4() {
        return this.col4;
    }

}


