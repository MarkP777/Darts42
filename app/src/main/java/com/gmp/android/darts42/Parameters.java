package com.gmp.android.darts42;

public class Parameters {

    public static final long gameMessageTimeout = 10 * 1000; //How long a message is displayed on the screen (millisecs)

    public static final long challengeTimeout = 3 * 60 * 1000; //How long user has to respond to a challenge (millisecs)

    public static final long challengeTimeoutMargin = 15 * 1000; //Safety margin so that challenger does not timeout early (millisecs)

     //Demo mode. User enters dummyEmail as opponent to switch into demo mode
    //Firebase password for dummy is "DemoPassword@1"
    public static final String dummyEmail = "darts42demo@demo.zzz";
    public static final String dummyNickname = "Demo Mode";
    public static final String dummyId = dummyEmail;
    public static final long dummyGoWaitTime = 7 * 1000; //How long dummy takes to throw in demo mode

}


