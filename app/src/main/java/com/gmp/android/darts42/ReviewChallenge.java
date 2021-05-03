package com.gmp.android.darts42;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class ReviewChallenge extends AppCompatActivity {

    private static final String TAG = "ReviewChallenge";
    public static final String EXTRA_CHALLENGEID = "Challenge_ID";
    public static final String EXTRA_OPPONENTID = "Opponent_ID";
    public static final String EXTRA_MATCHID = "Match_ID";
    public static final String EXTRA_TIMESTAMP = "Timeout";

    private FirebaseDatabase mDatabase;
    private DatabaseReference playerProfileReference;
    private DatabaseReference matchReference;
    private ChildEventListener mPlayerMessageListener;
    private String challengeMessageID;
    private String mUid;
    private String awayUid;
    private String startDoubleString;
    private String challengerID;
    private String matchID;
    private long timestamp;

    private CommonData commonData;

    private TextView reviewCountdown;
    private Calendar timeNowCalendar;
    private final Long challengeTimeout = (long) 18000 * 1000; //Challenge timeout in milliseconds

    private Button acceptChallengeButton;
    private Button declineChallengeButton;

    private CountDownTimer messageCountdownTimer;
    private CountDownTimer reviewCountdownTimer;


    Match matchDetails;
    PlayerProfile challengerProfile;

    TextView challengerDetails;
    TextView setsLegs;
    TextView startDoubles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_challenge);

        //Assume that we are passed - ownID, and all the details from the challenge record

        mDatabase = FirebaseDatabase.getInstance();
        commonData = CommonData.getInstance();

        challengerDetails = (TextView)findViewById(R.id.tvChallengerDetails);
        setsLegs = (TextView)findViewById(R.id.tvSetsLegs);
        startDoubles = (TextView)findViewById(R.id.tvStartDoubles);

        acceptChallengeButton = (Button)findViewById(R.id.btAccept);
        acceptChallengeButton.setEnabled(false);
        declineChallengeButton = (Button)findViewById(R.id.btDecline);
        declineChallengeButton.setEnabled(false);


        mUid = commonData.getHomeUserID();




        challengeMessageID = (String) getIntent().getExtras().get(EXTRA_CHALLENGEID);
        challengerID = (String) getIntent().getExtras().get(EXTRA_OPPONENTID);
        matchID = (String) getIntent().getExtras().get(EXTRA_MATCHID);
        timestamp = (long) getIntent().getExtras().get(EXTRA_TIMESTAMP);


        //Get the challenger details
        playerProfileReference=mDatabase.getReference("player_profiles");
        Query challengerData = playerProfileReference.orderByKey().equalTo(challengerID).limitToFirst(1);
        challengerData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot profileChildSnapshot : snapshot.getChildren()) {

                    challengerProfile = profileChildSnapshot.getValue(PlayerProfile.class);
                    Log.d(TAG, "User profile found");

                    //Get the match details
                    matchReference = mDatabase.getReference("matches");
                    Query matchData = matchReference.orderByKey().equalTo(matchID).limitToFirst(1);
                    matchData.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //Not checking for non-null snapshot as the match record should be there
                            for (DataSnapshot matchChildSnapshot : snapshot.getChildren()) {

                                matchDetails = matchChildSnapshot.getValue(Match.class);
                                Log.d("MainActivity", "Match data found");

                                //Display all the details
                                startDoubleString = "Start from " + Integer.toString(matchDetails.getStartingPoints()) + ", ";
                                if (matchDetails.getStartWithDouble()) {
                                    startDoubleString = startDoubleString + "double to start, ";
                                } else {
                                    startDoubleString = startDoubleString + "straight start, ";
                                }
                                if (matchDetails.getEndWithDouble()) {
                                    startDoubleString = startDoubleString + "double to finish";
                                } else {
                                    startDoubleString = startDoubleString + "straight finish";
                                }

                                challengerDetails.setText(challengerProfile.getPlayerName() + " has challenged you to a match:");
                                setsLegs.setText("Best of "
                                        + Integer.toString(matchDetails.getBestOfSets())
                                        + " set(s), each best of "
                                        + Integer.toString(matchDetails.getBestOfLegs())
                                        + " legs");
                                startDoubles.setText(startDoubleString);
                                declineChallengeButton.setEnabled(true);
                                acceptChallengeButton.setEnabled(true);
                                setReviewCountDownTimer();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void challengeAccepted(View view) {

        //Kill the timer
        stopReviewCountDownTimer();

        //Stop the user from pressing anymore buttons
        acceptChallengeButton.setEnabled(false);
        declineChallengeButton.setEnabled(false);

        //
        deleteChallenge();

        displayNewGameMessage("Challenge accepted. Starting match ...");
        //TODO: if the challenge hasn't expired then start match
        //TODO: otherwise tell the user and go back to the home screen
    }

    public void challengeDeclined(View view) {

        //Kill the timer
        stopReviewCountDownTimer();

        //Stop the user from pressing anymore buttons
        acceptChallengeButton.setEnabled(false);
        declineChallengeButton.setEnabled(false);

        //Delete the challenge record from messages and then send a decline message to the challenger
        deleteChallenge();
        sendDecline(challengerID,matchID);

        //TODO: set status before returning to Home screen
        finish();

    }

    private void sendDecline (String challengerID,String matchID) {
        DatabaseReference playerMessageDatabaseReference = mDatabase.getReference("player_messages").child(challengerID);
        PlayerMessage playerMessage = new PlayerMessage(103,matchID,mUid, (long) 0);
        playerMessageDatabaseReference.push().setValue(playerMessage);
    }

    private void deleteChallenge() {
        DatabaseReference playerMessageReference;
        playerMessageReference = mDatabase.getReference("player_messages").child(mUid).child(challengeMessageID);
        playerMessageReference.removeValue();
    }

    private void startMatch() {

        String lastThrower;
        DatabaseReference playerMessageDatabaseReference;
        PlayerMessage playerMessage;

        //toss to see who throws first

        if (Math.random() <= 0.5) lastThrower = mUid;
        else lastThrower = challengerID;

        //write a seed record to the scores table
        DatabaseReference scoreDatabaseReference = mDatabase.getReference("scores").child(matchID);
        Score seedScore = new Score(
                lastThrower,
                "",
                -1,
                new ArrayList<Integer>(Arrays.asList(new Integer[]{matchDetails.getStartingPoints(), matchDetails.getStartingPoints()})),
                new ArrayList<Boolean>(Arrays.asList(new Boolean[]{!matchDetails.getStartWithDouble(),!matchDetails.getStartWithDouble()})),
                new ArrayList<Integer>(Arrays.asList(new Integer[]{0,0})),
                new ArrayList<Integer>(Arrays.asList(new Integer[]{0,0})),
                false
                );
        scoreDatabaseReference.push().setValue(seedScore);

        //write game in progress messages to the challenger and oneself

        playerMessage = new PlayerMessage(102,matchID,mUid,(long) 0);
        playerMessageDatabaseReference = mDatabase.getReference("player_messages").child(challengerID);
        playerMessageDatabaseReference.push().setValue(playerMessage);

        playerMessage.setSender(challengerID);
        playerMessageDatabaseReference = mDatabase.getReference("player_messages").child(mUid);
        playerMessageDatabaseReference.push().setValue(playerMessage);

        //set status to engaged
        playerProfileReference = mDatabase.getReference("player_profiles");
        playerProfileReference.child(mUid).child("playerEngaged").setValue(true);

        //TODO: goto the game activity

    }

    private void setReviewCountDownTimer() {
        reviewCountdown = (TextView)findViewById(R.id.tvReviewCountdown);
        //challengeCountdown.setVisibility(View.VISIBLE);
        timeNowCalendar = Calendar.getInstance();
        reviewCountdownTimer = new CountDownTimer(timestamp+challengeTimeout-timeNowCalendar.getTimeInMillis(), 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                double minutesToGo;
                double secondsToGo;
                String textToDisplay;
                minutesToGo = Math.floor((double) millisUntilFinished/1000/60);
                secondsToGo = Math.floor((double) (millisUntilFinished - (minutesToGo*60*1000))/1000);
                //Log.d("Countdown",String.format("Waiting for opponent's response: %1$02d"+":"+"%2$02d",(int)minutesToGo,(int)secondsToGo));
                textToDisplay=String.format("Please respond to "+
                                challengerProfile.getPlayerNickName()+
                                " in: %1$02d"+
                                ":"+
                                "%2$02d",
                        (int)minutesToGo,(int)secondsToGo);
                reviewCountdown.setText(textToDisplay);
            }

            @Override
            public void onFinish() {
                //Countdown timer has fired so the challenge has expired
                reviewCountdown.setText("");

                //Stop the user from pressing anymore buttons
                acceptChallengeButton.setEnabled(false);
                declineChallengeButton.setEnabled(false);

                //TODO: Tell the user

                //Delete the challenge record
                deleteChallenge();

            }
        };
        reviewCountdownTimer.start();
    }

    private void stopReviewCountDownTimer() {
        reviewCountdown.setText("");
        reviewCountdownTimer.cancel();
    }

    private void setMessageCountdownTimer() {
        messageCountdownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
            startMatch();

            }
        };
        messageCountdownTimer.start();
    }

    private void displayNewGameMessage(String textToDisplay) {
        reviewCountdown.setVisibility(View.VISIBLE);
        reviewCountdown.setText(textToDisplay);
        setMessageCountdownTimer();
        reviewCountdown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                messageCountdownTimer.cancel();
                startMatch();
                return false;
            }
        });

    }



    }


