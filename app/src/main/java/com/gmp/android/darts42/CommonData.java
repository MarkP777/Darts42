package com.gmp.android.darts42;


public class CommonData {
// Singleton class to share commonly used data between activities See https://stackoverflow.com/questions/4878159/whats-the-best-way-to-share-data-between-activities
// And https://www.geeksforgeeks.org/singleton-class-java/

    private String homeUserID;
    private String homeUserNickname;
    public String getHomeUserID() {return homeUserID;}
    public void setHomeUserID(String data) {this.homeUserID = data;}
    public String getHomeUserNickname() {return homeUserNickname;}
    public void setHomeUserNickname(String data) {this.homeUserNickname = data;}

    private static final CommonData holder = new CommonData();
    public static CommonData getInstance() {return holder;}
}

