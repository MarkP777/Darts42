package com.gmp.android.darts42;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.Group;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Date;
import java.util.List;

public class IssueChallenge extends AppCompatActivity {

    private static final String TAG = "IssueChallenge";

    private Button checkOpponentButton;
    private EditText mMessageEditText;

    public FirebaseDatabase mScoresDatabase;
    private DatabaseReference mScoresDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public CommonData commonData;
    public String mUid;

    private String newMatchKey;

    private Spinner setsSpinner;
    private Spinner legsSpinner;
    private Spinner startSpinner;

    private String[] setsValues = new String[7];
    private String[] legsValues = new String [6];
    private String[] startValues = new String[5];

    private SwitchCompat doubleToStartSwitch;
    private SwitchCompat doubleToFinishSwitch;

    private Button sendChallengeButton;

    private Match matchToPlay;

    private PlayerProfile awayProfile;

    private CountDownTimer mcountDownTimer;
    private CountDownTimer messageCountdownTimer;

    private ChildEventListener mPlayerMessageListener;
/*
    final EditText emailValidate = (EditText)findViewById(R.id.tvOpponentEmail);
    final TextView opponentNameConfirm = (TextView)findViewById(R.id.tvOpponentNameConfirm);
    String awayEMail = emailValidate.getText().toString().trim();
*/

    private EditText emailValidate;

    private TextView opponentNameConfirm;
    private TextView challengeCountdown;

    private String awayEMail;

    private Boolean challengeAccepted;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    //String emailPattern = "[a-zA-Z0-9]";

    String founduserid = null;

    private Boolean demoMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_challenge);

        /*
        setSupportActionBar(findViewById(R.id.issue_challenge_toolbar));
        getSupportActionBar().setTitle("Darts 42 - Issue Challenge");

         */
        mScoresDatabase = FirebaseDatabase.getInstance();
        checkOpponentButton = (Button) findViewById(R.id.btCheckOpponent);

        emailValidate = (EditText)findViewById(R.id.tvOpponentEmail);
        opponentNameConfirm = (TextView)findViewById(R.id.tvOpponentNameConfirm);
        String awayEMailText = emailValidate.getText().toString().trim();


        /*
        emailValidate = (EditText)findViewById(R.id.tvOpponentEmail);
        opponentNameConfirm = (TextView)findViewById(R.id.tvOpponentNameConfirm);
        awayEMail = emailValidate.getText().toString().trim();
*/

        commonData = CommonData.getInstance();
        mUid = commonData.getHomeUserID();

        Log.d(TAG,"Starting Issue Challenge");

        //Temporary code to keep things moving on
        //Tell the parent activity that we've come from Issue Challenge
        Intent intent = new Intent();
        intent.putExtra("IssueChallenge", 10);

        //No checks on success
        setResult(RESULT_OK, intent);
        //Temporary code above

        //Set up values for the spinners
        int counter;
        int intValue = 501;
        for (counter = 0; counter < 5; counter++) {
            startValues[counter] = String.format("%1$3d", intValue);
            intValue = intValue - 100;
        }

        intValue = 1;
        for (counter = 0; counter < 7; counter++) {
            setsValues[counter] = String.format("%1$d", intValue);
            intValue = intValue +2;
        }

        intValue = 1;
        for (counter = 0; counter < 6; counter++) {
            legsValues[counter] = String.format("%1$d", intValue);
            intValue = intValue + 2;
        }

        //Bind the spinners to the layouts
        startSpinner = (Spinner) findViewById(R.id.spStart);
        setsSpinner = (Spinner) findViewById(R.id.spMaxSets);
        legsSpinner = (Spinner) findViewById(R.id.spMaxLegs);

        //Declare the adapters for the spinners
        ArrayAdapter<String> startAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, startValues);
        ArrayAdapter<String> setsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, setsValues);
        ArrayAdapter<String> legsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, legsValues);

        //Define the layouts for the spinner dropdowns
        startAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        legsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapters to the spinner
        startSpinner.setAdapter(startAdapter);
        setsSpinner.setAdapter(setsAdapter);
        legsSpinner.setAdapter(legsAdapter);

        // Set the default values for the spinners
        startSpinner.setSelection(4); //Start at 101
        setsSpinner.setSelection(0); //1 set per match
        legsSpinner.setSelection(1); //3 legs per set

        //Sort out the switches
        doubleToStartSwitch = (SwitchCompat) findViewById(R.id.swDoubleToStartPrompt);
        doubleToFinishSwitch = (SwitchCompat) findViewById(R.id.swDoubleToFinishPrompt);

        //And lastly the challenge button
        sendChallengeButton = (Button) findViewById(R.id.btSendChallenge);

        //Disable the game details until an opponent is found
        startSpinner.setEnabled(false);
        setsSpinner.setEnabled(false);
        legsSpinner.setEnabled(false);
        doubleToStartSwitch.setEnabled(false);
        doubleToFinishSwitch.setEnabled(false);
        sendChallengeButton.setEnabled(false);

        emailValidate.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (s.toString().matches(emailPattern) && s.length() > 0)
                {
                    checkOpponentButton.setEnabled(true);
                    awayEMail = s.toString().trim();
                }
                else
                {
                    checkOpponentButton.setEnabled(false);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Ensure the opponent's name is clear
                opponentNameConfirm.setText("");
            }
        });

    }

    private void writeNewMatchRecord(Match newMatchDetails) {

        //writes out a new match record
        mScoresDatabaseReference = mScoresDatabase.getReference("matches");
        newMatchKey = mScoresDatabaseReference.push().getKey();
        mScoresDatabaseReference.child(newMatchKey).setValue(newMatchDetails);

    }

    private void writeNewChallenge (String founduserid, String newMatchKey) {

        mScoresDatabaseReference = mScoresDatabase.getReference("player_messages/" + founduserid + "/");
        //Write out the player message
        /* A word about timestamps. In theory, we should use server time for timestamps and timeouts. However, although
        I worked out how to write a server timestamp, I struggled to get current server time at the client end. There
        is some documentation about looking at time drift between server and client but I decided, for simplicity at this stage,
        I'd just use client time all the way through. This could lead to problems if client clocks were wildly out of sync,
        but timeouts should be long enough for this not to be noticeable.
        For future reference:
        mScoresDatabaseReference.child(tempKey).child("timestamp").setValue(ServerValue.TIMESTAMP);
        */
        PlayerMessage playerMessage = new PlayerMessage(101, newMatchKey, mUid, Calendar.getInstance().getTimeInMillis());
        mScoresDatabaseReference.push().setValue(playerMessage);
    }

    public void checkOpponent(View view) {

        //Cannot challenge yourself
        if (awayEMail.toLowerCase().equals(commonData.getHomeUserEMail())) {
            opponentNameConfirm.setText("Cannot challenge yourself. Please select another player");
        }
        //Get opponent profile
        else {
            DatabaseReference playerProfilesReference = mScoresDatabase.getReference().child("player_profiles");
            Log.d(TAG,awayEMail.toLowerCase());
            Query data = playerProfilesReference.orderByChild("playerEMail").equalTo(awayEMail.toLowerCase()).limitToFirst(1);
            data.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        // Get the opponent's details
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            founduserid = childSnapshot.getKey();
                            Log.d(TAG, "Challenger user profile found " + founduserid);
                            awayProfile = childSnapshot.getValue(PlayerProfile.class);
                            opponentNameConfirm.setText(awayProfile.getPlayerName());
                            if (awayProfile.getPlayerEngaged())
                                //Opponent's busy flag set
                                opponentNameConfirm.setText(awayProfile.getPlayerName() + "is busy. Please select another opponent.");

                            else {
                                //Opponent is not busy
                                opponentNameConfirm.setText(awayProfile.getPlayerName());

                                //Set the demo mode flag if the opponent is the demo user
                                if (awayEMail.trim().toLowerCase().equals(Parameters.dummyEmail)) {
                                    demoMode = true;
                                }
                                else {
                                    demoMode = false;
                                }

                                //Disable any changes to the opponent ...
                                emailValidate.setEnabled(false);
                                checkOpponentButton.setEnabled(false);

                                //... and enable the match detail input
                                startSpinner.setEnabled(true);
                                setsSpinner.setEnabled(true);
                                legsSpinner.setEnabled(true);
                                doubleToStartSwitch.setEnabled(true);
                                doubleToFinishSwitch.setEnabled(true);
                                sendChallengeButton.setEnabled(true);
                            }
                        }
                    } else {
                        //Couldn't find a profile
                        Log.d(TAG, "No challenger user profile found");
                        opponentNameConfirm.setText("Cannot find player with this email address. Please try again");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
}

    public void sendChallenge(View view) {

        //Method called when the Send Challenge button is pressed

        //Disable the send challenge button to stop it being hit again
        sendChallengeButton.setEnabled(false);

        //Construct match details
        matchToPlay = new Match(
                mUid,
                founduserid,
                Integer.valueOf(setsValues[setsSpinner.getSelectedItemPosition()].trim()),
                Integer.valueOf(legsValues[legsSpinner.getSelectedItemPosition()].trim()),
                Integer.valueOf(startValues[startSpinner.getSelectedItemPosition()].trim()),
                doubleToStartSwitch.isChecked(),
                doubleToFinishSwitch.isChecked(),
                new ArrayList<Integer>(Arrays.asList(new Integer[]{0,0})),
                new ArrayList<Integer>(Arrays.asList(new Integer[]{0,0})),
                0
                );

        // Create the match record
        writeNewMatchRecord(matchToPlay);

        //If we're demo mode set up the game as if the challenge has been sent and accepted by the opponent
        // otherwise set the challenger's status to busy and write out the challenge to the opponent
        if (demoMode) {
            setUpDemoGame();
        }
        else {
            //Set challenger's status to busy
            mScoresDatabaseReference = mScoresDatabase.getReference("player_profiles");
            mScoresDatabaseReference.child(mUid).child("playerEngaged").setValue(true);

            //send challenge to opponent
            writeNewChallenge(founduserid,newMatchKey);
        }

        //Look for response in player messages. If nothing received in 3 minutes then assume that
        //opponent is not around

        mScoresDatabaseReference = mScoresDatabase.getReference().child("player_messages").child(mUid);

        attachPlayerMessageListener();

        setCountDownTimer();

    }

    private void attachPlayerMessageListener() {
        if (mPlayerMessageListener == null) {
            mPlayerMessageListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    //Response received, so kill the countdown timer and the listener, and hide the countdown
                    killCountdownTimer();
                    detachPlayerMessageListener();
                    challengeCountdown.setVisibility(View.GONE);

                    //Unwrap the message
                    PlayerMessage playerMessage = dataSnapshot.getValue(PlayerMessage.class);

                    Log.d(TAG, "Player message of type "
                            + playerMessage.getMessageType()
                            + " with payload "
                            + playerMessage.getPayload()
                            + " received from "
                            + playerMessage.getSender()
                    );

                   if (playerMessage.getMessageType() == 103) {
                       //Player declines
                       //Delete the message
                       mScoresDatabaseReference.child(dataSnapshot.getKey()).removeValue();
                       challengeAccepted = false;
                       displayNewGameMessage(awayProfile.getPlayerNickName()+ " does not want to play.");
                   }
                   else
                   {
                       //Assume that it's a Yes (message type 2), and go to play the match
                       challengeAccepted = true;
                       displayNewGameMessage(awayProfile.getPlayerNickName()+" has accepted your challenge. Game on!");
                   }
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mScoresDatabaseReference.addChildEventListener(mPlayerMessageListener);

        }
    }

    private void setCountDownTimer() {
        challengeCountdown = (TextView)findViewById(R.id.tvChallengeCountdown);
        //challengeCountdown.setVisibility(View.VISIBLE);
        mcountDownTimer = new CountDownTimer(Parameters.challengeTimeout+Parameters.challengeTimeoutMargin, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                double minutesToGo;
                double secondsToGo;
                String textToDisplay;
                minutesToGo = Math.floor((double) millisUntilFinished/1000/60);
                secondsToGo = Math.floor((double) (millisUntilFinished - (minutesToGo*60*1000))/1000);
                //Log.d("Countdown",String.format("Waiting for opponent's response: %1$02d"+":"+"%2$02d",(int)minutesToGo,(int)secondsToGo));
                textToDisplay=String.format("Waiting for "+
                        awayProfile.getPlayerNickName()+
                        "'s response: %1$02d"+
                        ":"+
                        "%2$02d",
                        (int)minutesToGo,(int)secondsToGo);
                challengeCountdown.setText(textToDisplay);
            }

            @Override
            public void onFinish() {
                //Countdown timer has fired so no response from opponent
                //Stop listening for messages and then tidy up
                challengeCountdown.setText("");
                detachPlayerMessageListener();
                challengeAccepted = false;
                displayNewGameMessage(awayProfile.getPlayerNickName()+" has not responded.");
                //challengeCountdown.setVisibility(View.GONE);

            }
        };
        mcountDownTimer.start();
    }

    private void killCountdownTimer() {
        if (mcountDownTimer != null) mcountDownTimer.cancel();
        mcountDownTimer = null;
    }

    private void handleNegativeResponse() {

        //The opponent has either declined the challenge or hasn't responded

            //Delete the match record
            mScoresDatabaseReference = mScoresDatabase.getReference("matches");
            mScoresDatabaseReference.child(newMatchKey).removeValue();

            //TODO: Need to work out what to do with the opponents challenge record

            //Set profile status to available
            mScoresDatabaseReference = mScoresDatabase.getReference("player_profiles");
            mScoresDatabaseReference.child(mUid).child("playerEngaged").setValue(false);

            finish();

    }



    private void detachPlayerMessageListener() {
        if (mPlayerMessageListener != null) {
            mScoresDatabaseReference.removeEventListener(mPlayerMessageListener);
            mPlayerMessageListener = null;
        }
    }


    private void goToMatch() {


        Intent intent1;
        intent1 = new Intent(IssueChallenge.this, PlayDarts.class);
         startActivityForResult(intent1, 2);
    }


    private void setMessageCountdownTimer() {
        messageCountdownTimer = new CountDownTimer(Parameters.gameMessageTimeout, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (challengeAccepted) {
                    goToMatch();
                }
                else {
                    handleNegativeResponse();
                }
            }
        };
        messageCountdownTimer.start();
    }

    private void displayNewGameMessage(String textToDisplay) {
        challengeCountdown.setVisibility(View.VISIBLE);
        challengeCountdown.setText(textToDisplay);
        setMessageCountdownTimer();
        challengeCountdown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                messageCountdownTimer.cancel();
                if (challengeAccepted) {
                    goToMatch();
                }
                else {
                    handleNegativeResponse();
                }
                return false;
            }
        });

    }

    private void setUpDemoGame() {

            String lastThrower;
            DatabaseReference playerMessageDatabaseReference;
            DatabaseReference playerProfileReference;
            PlayerMessage playerMessage;

            //toss to see who throws first

            if (Math.random() <= 0.5) lastThrower = founduserid;
            else lastThrower = mUid;

            //write a seed record to the scores table
            DatabaseReference scoreDatabaseReference = mScoresDatabase.getReference("scores").child(newMatchKey);
            Score seedScore = new Score(
                    lastThrower,
                    "",
                    -1,
                    new ArrayList<Integer>(Arrays.asList(new Integer[]{matchToPlay.getStartingPoints(), matchToPlay.getStartingPoints()})),
                    new ArrayList<Boolean>(Arrays.asList(new Boolean[]{!matchToPlay.getStartWithDouble(),!matchToPlay.getStartWithDouble()})),
                    new ArrayList<Integer>(Arrays.asList(new Integer[]{0,0})),
                    new ArrayList<Integer>(Arrays.asList(new Integer[]{0,0})),
                    false
            );
            scoreDatabaseReference.push().setValue(seedScore);

            matchToPlay.incrementNumberOfScoreRecords();
            scoreDatabaseReference = mScoresDatabase.getReference("matches");
            scoreDatabaseReference.child(newMatchKey).child("numberOfScoreRecords").setValue(matchToPlay.getNumberOfScoreRecords());


            //write game in progress messages to to oneself as if it has come from the demo user

            playerMessage = new PlayerMessage(102,newMatchKey,mUid,(long) 0);

            playerMessage.setSender(founduserid);
            playerMessageDatabaseReference = mScoresDatabase.getReference("player_messages").child(mUid);
            playerMessageDatabaseReference.push().setValue(playerMessage);

            //set own status to engaged
            playerProfileReference = mScoresDatabase.getReference("player_profiles");
            playerProfileReference.child(mUid).child("playerEngaged").setValue(true);
        }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //We should only get here from the PlayDarts activity.
        //At the moment, we just:
        finish();
    }



}