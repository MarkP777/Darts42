package com.gmp.android.darts42;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
// import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayDarts extends AppCompatActivity {


    private static final String TAG = "PlayDarts";

    private static final String scorePattern = "^([bom]{1})|^(^[dt]?[12]{1}[0]{1})|^(^[dt]?[1]?[1-9]{1})";

    CommonData commonData;
    String myUId;
    String myNickname;
    String theirID;
    String theirNickname;

    Integer myTotalScore;
    Integer dummyTotalScore;
    Integer theirTotalScore;
    Boolean iHaveStarted;
    Boolean theyHaveStarted;

    Integer scoreThisTurn;
    Boolean scoresNewLineAdded;
    Boolean scoresLeftColumnFilled;
    Integer scoreLastPosition;

    Integer tempAdapterLength;

    Integer scoreThisDart;
    Boolean bustThisThrow;
    Boolean wonLegThisThrow;
    Boolean legFinished;



    FirebaseDatabase fbDatabase;
    DatabaseReference scoresDataBaseReference;
    DatabaseReference matchDatabaseReference;
    ChildEventListener scoresChildEventListener;

    RecyclerView recyclerView;


    TextView setsText;
    TextView lSets;
    TextView rSets;
    TextView d2SText;
    TextView legsText;
    TextView lLegs;
    TextView rLegs;
    TextView d2FText;
    TextView lName;
    TextView rName;

    TextView scoreTotal;
    TextView d1Score;
    TextView d2Score;
    TextView d1Prompt;

    ThrowKeyboard throwKeyboard;
    Group scoreSection;
    Group d1Section;
    Group d2Section;
    Group d3Section;
    TextView waitingMessage;

    EditText d1Input;
    EditText d2Input;
    EditText d3Input;

    Boolean demoMode;
    Boolean dummyDelayHandlerRunning = false;

    Integer numberOfScoreRecordsRead = 0;

    protected TextWatcher dart1Watcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {

            int stringLength = s.length();
            int lastCharacter;

            if (stringLength > 0) {

                lastCharacter = (int) s.charAt(s.length() - 1);

                if (lastCharacter == 10) {
                    // Log.d(TAG, s.toString() + " entered ");
                    dart1Thrown();

                } else {
                    // Log.d(TAG, s.toString() + " not entered yet ");
                }
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    };

    protected TextWatcher dart2Watcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {

            int stringLength = s.length();
            int lastCharacter;

            if (stringLength > 0) {

                lastCharacter = (int) s.charAt(s.length() - 1);

                if (lastCharacter == 10) {
                    // Log.d(TAG, s.toString() + " entered ");
                    dart2Thrown();

                } else {
                    // Log.d(TAG, s.toString() + " not entered yet ");
                }
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    };

    protected TextWatcher dart3Watcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {

            int stringLength = s.length();
            int lastCharacter;

            if (stringLength > 0) {

                lastCharacter = (int) s.charAt(s.length() - 1);

                if (lastCharacter == 10) {
                    // Log.d(TAG, s.toString() + " entered ");
                    dart3Thrown();

                } else {
                    // Log.d(TAG, s.toString() + " not entered yet ");
                }
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
    };

    PlayerMessage type102Message;
    Match matchDetails = new Match();
    PlayerProfile theirProfile;

    private String matchID;
    private Integer setsToWin;
    private Integer legsToWin;
    private Integer startingPoints;
    private Boolean startWithDouble;
    private Boolean endWithDouble;
    private List<Integer> totalScore = new ArrayList<>();
    private List<Integer> setScores = new ArrayList<>();
    private List<Integer> legScores = new ArrayList<>();
    private List<Boolean> hasStarted = new ArrayList<>();
    private Integer myScoreIndex;
    private Integer theirScoreIndex;
    private Boolean iAmPlayer0;
    private Boolean matchOver;
    private Boolean setOver;

    private Boolean myGoNext;
    private String whoStartedLastLegId;

    private String messageText;

    private CountDownTimer messageCountDownTimer;

    private Boolean textWatcherRunning = false;

    Dart dart = new Dart();
    Score score = new Score();
    String throwString;


    MyRecyclerViewAdapter adapter;

    ArrayList<String> scoreLinesIn4Columns = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        DatabaseReference fbMessageDatabaseReference;
          /*
        We should enter this activity with a message type 102 in the player_messages queue. This will tell us
        (payload) the id of the match
        (sender) who we're playing
         */


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_darts);
        fbDatabase = FirebaseDatabase.getInstance();

/*
        setSupportActionBar(findViewById(R.id.play_darts_toolbar));
        getSupportActionBar().setTitle("Darts 42 - Game On");
*/
        //Find all the elements of the screen

        setsText = (TextView) findViewById(R.id.tvSetsPrompt);
        lSets = (TextView) findViewById(R.id.tvlSets);
        rSets = (TextView) findViewById(R.id.tvrSets);
        d2SText = (TextView) findViewById(R.id.tvD2SPrompt);
        legsText = (TextView) findViewById(R.id.tvLegsPrompt);
        lLegs = (TextView) findViewById(R.id.tvlLegs);
        rLegs = (TextView) findViewById(R.id.tvrLegs);
        d2FText = (TextView) findViewById(R.id.tvD2FPrompt);
        lName = (TextView) findViewById(R.id.tvlName);
        rName = (TextView) findViewById(R.id.tvrName);
        waitingMessage = (TextView) findViewById(R.id.tvWaitingMessage);

        scoreTotal = (TextView) findViewById(R.id.tvScoreTotal);
        d1Score = (TextView) findViewById(R.id.tvd1Score);
        d2Score = (TextView) findViewById(R.id.tvd2Score);

        scoreSection = (Group) findViewById(R.id.gScoreSection);
        d1Section = (Group) findViewById(R.id.gd1Section);
        d2Section = (Group) findViewById(R.id.gd2Section);
        d3Section = (Group) findViewById(R.id.gd3Section);

        d1Input = (EditText) findViewById(R.id.etd1Input);
        d2Input = (EditText) findViewById(R.id.etd2Input);
        d3Input = (EditText) findViewById(R.id.etd3Input);

        d1Prompt = (TextView) findViewById(R.id.tvd1Prompt);

        throwKeyboard = (ThrowKeyboard) findViewById(R.id.throwKeyboard);

       //Get who we are
        commonData = CommonData.getInstance();
        myUId = commonData.getHomeUserID();
        myNickname = commonData.getHomeUserNickname();

        //Read the message. We'll assume that there is only one with type 102

        fbMessageDatabaseReference = fbDatabase.getReference("player_messages").child(myUId);
        Query data = fbMessageDatabaseReference.orderByChild("messageType").equalTo(102).limitToFirst(1);
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    // Get the opponent's details
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        type102Message = childSnapshot.getValue(PlayerMessage.class);
                        // Log.d(TAG, "Match message found " + childSnapshot.getKey());
                        matchID = type102Message.getPayload();
                        theirID = type102Message.getSender();

                        //Now get the match details
                        getMatchDetails();
                    }
                } else {
                    // Couldn't find the match message - do nothing
                    // Log.d(TAG, "102 message not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Goes with the match look up
            }
        });

    } //Ends onCreate

    private void getMatchDetails() {

        //Read the match record
        matchDatabaseReference = fbDatabase.getReference("matches");
        Query matchData = matchDatabaseReference.orderByKey().equalTo(matchID).limitToFirst(1);
        matchData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    for (DataSnapshot matchChildSnapshot : snapshot.getChildren()) {

                        matchDetails = matchChildSnapshot.getValue(Match.class);
                        // Log.d(TAG, "Match data found");

                        //TODO: unwrap the match record
                        if (matchDetails.getPlayer1Id().equals(myUId)) {
                            iAmPlayer0 = false;
                            myScoreIndex = 1;
                            theirScoreIndex = 0;
                        } else {
                            iAmPlayer0 = true;
                            myScoreIndex = 0;
                            theirScoreIndex = 1;
                        }
                        setsToWin = (int) Math.floor(matchDetails.getBestOfSets() / 2) + 1;
                        legsToWin = (int) Math.floor(matchDetails.getBestOfLegs() / 2) + 1;
                        startingPoints = matchDetails.getStartingPoints();
                        startWithDouble = matchDetails.getStartWithDouble();
                        endWithDouble = matchDetails.getEndWithDouble();
                        setScores = matchDetails.getSetScores();
                        legScores = matchDetails.getLegScores();

                        //Set up the headings
                        setsText.setText(String.format("Sets (of %1$d)",matchDetails.getBestOfSets()));
                        legsText.setText(String.format("Legs (of %1$d)",matchDetails.getBestOfLegs()));
                        if (startWithDouble) {d2SText.setText("D2S");}
                        else {d2SText.setText("");}
                        if (endWithDouble) {d2FText.setText("D2F");}
                        else {d2FText.setText("");}

                        //Now get the opponent details
                        getOpponentDetails();
                    }
                } else {
                    // Couldn't find the match record - do nothing
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Goes with the match look up
            }
        });

    } //ends getMatchDetails

    private void getOpponentDetails() {

        DatabaseReference fbProfileDatabaseReference;

        //Read the opponent's profile record
        fbProfileDatabaseReference = fbDatabase.getReference("player_profiles");
        Query opponentData = fbProfileDatabaseReference.orderByKey().equalTo(theirID).limitToFirst(1);
        opponentData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot profileChildSnapshot : snapshot.getChildren()) {

                        theirProfile = profileChildSnapshot.getValue(PlayerProfile.class);
                        // Log.d(TAG, "Opponent profile found");

                        theirNickname = theirProfile.getPlayerNickName();

                        //Look at the opponent's email. If it matches the preset demo email, go into demo mode
                        if (theirProfile.getPlayerEMail().trim().toLowerCase().equals(Parameters.dummyEmail)) {
                            demoMode = true;
                        }
                        else {
                            demoMode = false;
                        }

                        //play the game
                        playGame();
                    }
                } else {
                    // Couldn't find the profile record - do nothing
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Goes with the match look up
            }
        });

    } //ends getOpponentDetails


    class Dart {
        private String string;
        private boolean isDouble;
        private Integer score;


        public Dart() {
        }

        public void analyse(String string) {

            char tempChar = '$';

            this.string = string;
            this.isDouble = false;

            tempChar = string.charAt(0);

            switch (tempChar) {
                case 'b': //b bull
                    this.isDouble = true;
                    this.score = 50;
                    break;
                case 'o': //o outer
                    this.score = 25;
                    break;
                case 'm': //m miss
                    this.score = 0;
                    break;
                default: //not b,o,m
                    switch (tempChar) {
                        case 't': //t treble
                            this.score = Integer.valueOf(string.substring(1)) * 3;
                            break;
                        case 'd': //d double
                            this.isDouble = true;
                            this.score = Integer.valueOf(string.substring(1)) * 2;
                            break;
                        default: //No leading character - just a straight score
                            this.score = Integer.valueOf(string);
                    }

            }

        }
    }//ends Dart



        private void playGame () {



            //Set up the top section. "I" am always on the left
            lName.setText(myNickname);
            rName.setText(theirNickname);
            lSets.setText("");
            rSets.setText("");
            lLegs.setText("");
            rLegs.setText("");


            //Clear the bottom section
            scoreSection.setVisibility(View.GONE);
            throwKeyboard.setVisibility(View.GONE);

            //Show waiting message regardless of turn
            waitingMessage.setVisibility(View.VISIBLE);
            waitingMessage.setText("Setting up match. Please wait ...");


            // set up the RecyclerView
            recyclerView = findViewById(R.id.rvScores);

            // Create a grid layout with eight columns
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 8);

            // Create a custom SpanSizeLookup where column widths are:
            // col1:1
            // col2:3
            // col3:3
            // col2:1
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (((position % 4) == 1) || ((position % 4) == 2))
                        return 1;
                    else
                        return 3;
                }
            });

            //Set up the recycler and its adapter
            recyclerView.setLayoutManager(gridLayoutManager);
            adapter = new MyRecyclerViewAdapter(this, scoreLinesIn4Columns);
            recyclerView.setAdapter(adapter);

            //Point the Firebase database in the right direction
            scoresDataBaseReference = fbDatabase.getReference().child("scores").child(matchID);

            //Attach the scores listener
            attachScoresListener();
    } // ends playGame

        private void attachScoresListener() {
            if (scoresChildEventListener == null) {
                scoresChildEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        //Cancel the message countdowntimer if it's running
                        if (messageCountDownTimer != null) messageCountDownTimer.cancel();

                        //Increment the number of records read and unwrap the message
                        numberOfScoreRecordsRead ++;
                        Score scoreRecord = dataSnapshot.getValue(Score.class);

                        /*
                        Log.d(TAG, "Score record from "
                                + scoreRecord.getThrower()
                                + " with string "
                                + scoreRecord.getThrowString()
                                + " and throw score "
                                + String.format("%1$d", scoreRecord.getThrowScore()));
                         */

                        //Not sure why I thought I needed to delete the message here
                        //scoresDataBaseReference.child(dataSnapshot.getKey()).removeValue();
                    /*

                    /*
                    Three cases
                    1) it's a seed record
                    2) it's "my" score
                    3) it's "their" score
                     */

                        totalScore = scoreRecord.getTotalScore();
                        myTotalScore = totalScore.get(myScoreIndex);
                        hasStarted = scoreRecord.getHasStarted();
                        iHaveStarted = hasStarted.get(myScoreIndex);
                        setScores = scoreRecord.getSetScores();
                        legScores = scoreRecord.getLegScores();
                        legFinished = scoreRecord.getLegFinished();

                        //Also pick up the opponent's total score. We use it if in demo mode
                        dummyTotalScore = totalScore.get(theirScoreIndex);

                        //write out sets and legs
                        lSets.setText(String.format("%1$d",setScores.get(myScoreIndex)));
                        rSets.setText(String.format("%1$d",setScores.get(theirScoreIndex)));
                        lLegs.setText(String.format("%1$d",legScores.get(myScoreIndex)));
                        rLegs.setText(String.format("%1$d",legScores.get(theirScoreIndex)));


                        if (scoreRecord.getThrowScore() < 0) { //Seed record because the total score is negative
                            //if we have already displayed scores then clear everything down
                            if (scoreLinesIn4Columns.size() > 0) {
                                tempAdapterLength = adapter.getItemCount();
                                scoreLinesIn4Columns.clear();
                                adapter.notifyItemRangeRemoved(0,tempAdapterLength);
                            }

                            //Start adding new entries
                            addNewScoreLine();
                            scoresNewLineAdded = true;
                            scoreLinesIn4Columns.set(1,Integer.toString(startingPoints));
                            scoreLinesIn4Columns.set(2,Integer.toString(startingPoints));
                            //Sort out who to go next
                            if (scoreRecord.getThrower().equals(myUId)) {
                                myGoNext = false;
                                whoStartedLastLegId = theirID;
                            } else {
                                myGoNext = true;
                                whoStartedLastLegId = myUId;
                            }
                        } else { //Not a seed record
                             if (scoreRecord.getThrower().equals(myUId)) { //"my" score - because it's my ID
                                 if (scoreLinesIn4Columns.get(scoreLastPosition-2).length() > 0) {
                                     addNewScoreLine();
                                     scoresNewLineAdded = true;
                                 }
                                 else {
                                     scoresNewLineAdded = false;
                                     scoresLeftColumnFilled = true;
                                 }

                                 scoreLinesIn4Columns.set(scoreLastPosition -3, scoreRecord.getThrowString()+String.format(" [%1d]",scoreRecord.getThrowScore()));
                                 if (scoreRecord.getLegFinished()) { //This go won the leg
                                     scoreLinesIn4Columns.set(scoreLastPosition - 2,"W");
                                 } else { ///Leg continues
                                     scoreLinesIn4Columns.set(scoreLastPosition - 2, Integer.toString(scoreRecord.getTotalScore().get(myScoreIndex)));
                                 }
                                myGoNext = false; //Other player to go next
                                // In this case it's the other player to go next


                            } else { // "their" score

                                 if (scoreLinesIn4Columns.get(scoreLastPosition-1).length() > 0) {
                                     addNewScoreLine();
                                     scoresNewLineAdded = true;
                                 }
                                 else {
                                     scoresNewLineAdded = false;
                                     scoresLeftColumnFilled = false;
                                 }

                                 if (scoreRecord.getLegFinished()) { //This go won the leg
                                     scoreLinesIn4Columns.set(scoreLastPosition - 1, "W");
                                 } else { //Leg continues
                                     scoreLinesIn4Columns.set(scoreLastPosition - 1, Integer.toString(scoreRecord.getTotalScore().get(theirScoreIndex)));
                                 }
                                 scoreLinesIn4Columns.set(scoreLastPosition,String.format("[%1d] ",scoreRecord.getThrowScore())+scoreRecord.getThrowString());
                                 myGoNext = true; //Me to go
                            }

                        }
                            //Get all the above on the screen;
                        if (scoresNewLineAdded) {
                            adapter.notifyItemRangeInserted(adapter.getItemCount(), 4);
                        } else {
                            if (scoresLeftColumnFilled) {
                                adapter.notifyItemRangeChanged(scoreLastPosition-3,2);
                            } else {
                                adapter.notifyItemRangeChanged(scoreLastPosition-1,2);
                            }
                            }

                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                        if (legFinished) { //Leg has been won
                            completeThisLeg(scoreRecord.getThrower());
                        }
                        else { //Leg still in progress

                            //Get next input provided we're not just catching up reading score records
                            //Note that the number of score records in the match object is not accurate
                            //once we've caught up - we're not re-reading the match record updated by the opponent
                            if (numberOfScoreRecordsRead >= matchDetails.getNumberOfScoreRecords()) {
                                if (myGoNext) { //My go, so get input
                                    getDart1();
                                } else { //Their go, so I'm told to wait
                                    messageText = "Waiting for " + theirNickname + " to throw";
                                    waitingMessage.setText(messageText);
                                    hideKeyboard();
                                    scoreSection.setVisibility(View.GONE);
                                    waitingMessage.setVisibility(View.VISIBLE);

                                    //And if we're in demo mode, then simulate the opponent's go
                                    if (demoMode) {
                                        dummyGo();
                                    }
                                }
                            }
                        }


                    }
                        public void onChildChanged (DataSnapshot dataSnapshot, String s){
                        }

                        public void onChildRemoved (DataSnapshot dataSnapshot){
                        }

                        public void onChildMoved (DataSnapshot dataSnapshot, String s){
                        }

                        public void onCancelled (DatabaseError databaseError){
                        }
                    };

                //And don't forget to enable the listener!!
                scoresDataBaseReference.addChildEventListener(scoresChildEventListener);

                        }
                        } //Ends attachScoresListener()

    private void addNewScoreLine() {

        for (int i = 1; i < 5; i++) {scoreLinesIn4Columns.add("");}
        scoreLastPosition = scoreLinesIn4Columns.size()-1;
    }

    private void detachScoresListener() {
        if (scoresChildEventListener != null) {
        scoresDataBaseReference.removeEventListener(scoresChildEventListener);
        scoresChildEventListener = null;
        }
        }// Ends detachScoresListener


    private void getDart1() {

        waitingMessage.setVisibility(View.GONE);
        scoreTotal.setText(Integer.toString(myTotalScore));
        //scoreSection.setVisibility(View.VISIBLE);

        //make sure that the input is clear
        d1Input.getText().clear();

        // prevent system keyboard from appearing when d1Input is tapped
        d1Input.setRawInputType(InputType.TYPE_CLASS_TEXT);
        d1Input.setTextIsSelectable(false);

        // pass the InputConnection from d1Input to the keyboard
        InputConnection ic = d1Input.onCreateInputConnection(new EditorInfo());
        throwKeyboard.setInputConnection(ic);

        d1Score.setText("");

        //setDartInputFilters(d1Input,d1Send);
        scoreSection.setVisibility(View.INVISIBLE);
        d1Section.setVisibility(View.VISIBLE);
        d1Input.setFocusableInTouchMode(true);
        d1Input.setFocusable(true);
        d1Input.requestFocus();

        /*
        d2Section.setVisibility(View.INVISIBLE);
        d3Section.setVisibility(View.INVISIBLE);
         */

        // Show the keyboard and move the rest of the screen up above it
        //Make sure that the recycler is still scrolled to the bottom
        showKeyboard();
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);

        // Add a listener that is looking for the Send key (char 10) - process throw when detected
        // Log.d(TAG,"Setting up textwatcher");
        if (!textWatcherRunning) {
            d1Input.addTextChangedListener(dart1Watcher);
            textWatcherRunning = true;
        }


    } //Ends getDart1()



    private void getDart2() {


        // prevent system keyboard from appearing when d2Input is tapped
        d2Input.setRawInputType(InputType.TYPE_CLASS_TEXT);
        d2Input.setTextIsSelectable(false);

        // pass the InputConnection from d2Input to the keyboard
        InputConnection ic = d2Input.onCreateInputConnection(new EditorInfo());
        throwKeyboard.setInputConnection(ic);

        d2Input.setText("");
        d2Score.setText("");

        //setDartInputFilters(d2Input,d2Send);
        d2Section.setVisibility(View.VISIBLE);
        d2Input.setFocusableInTouchMode(true);
        d2Input.setFocusable(true);
        d2Input.requestFocus();

        //d3Section.setVisibility(View.INVISIBLE);

        // Add a listener that is looking for the Send key (char 10) - process throw when detecyed
        if (!textWatcherRunning) {
            d2Input.addTextChangedListener(dart2Watcher);
            textWatcherRunning = true;
        }


    } //Ends getDart2()

    private void getDart3() {


        // prevent system keyboard from appearing when d1Input is tapped
        d3Input.setRawInputType(InputType.TYPE_CLASS_TEXT);
        d3Input.setTextIsSelectable(false);

        // pass the InputConnection from d3Input to the keyboard
        InputConnection ic = d3Input.onCreateInputConnection(new EditorInfo());
        throwKeyboard.setInputConnection(ic);

        d3Input.setText("");

        //setDartInputFilters(d3Input,d3Send);

        d3Section.setVisibility(View.VISIBLE);
        d3Input.setFocusableInTouchMode(true);
        d3Input.setFocusable(true);
        d3Input.requestFocus();

        // Add a listener that is looking for the Send key (char 10) - proess throw when detecyed
        if (!textWatcherRunning) {
            d3Input.addTextChangedListener(dart3Watcher);
            textWatcherRunning = true;
        }


    } //Ends getDart3()


    public void dart1Thrown() {

        //Player has entered the score. Prevent them from going back to edit it
        d1Input.setFocusable(false);

        //Remove the textwatcher
        d1Input.removeTextChangedListener(dart1Watcher);
        textWatcherRunning = false;

        dart.analyse(d1Input.getText().toString().trim());

        throwString = dart.string;
        bustThisThrow = false;
        wonLegThisThrow = false;
        scoreThisDart = dart.score;

        // score for the dart might be modified by start and finish conditions
        scoreThisTurn = scoreThisDart;
        checkStartAndFinishConditions();
        myTotalScore = myTotalScore - scoreThisDart;

        //Write them to the screen
        d1Score.setText("("+Integer.toString(scoreThisDart)+") ["+Integer.toString(scoreThisTurn)+"]");
        scoreTotal.setText(Integer.toString(myTotalScore));

        if (bustThisThrow || wonLegThisThrow) {
            finishThrow();
        }
        else {

            getDart2();
        }

    } //Ends dart1Thrown


    public void dart2Thrown() {

        //Player has entered the score. Prevent them from going back to edit it
        d2Input.setFocusable(false);

        //Remove the textwatcher
        d2Input.removeTextChangedListener(dart2Watcher);
        textWatcherRunning = false;

        dart.analyse(d2Input.getText().toString().trim());

        throwString = throwString + ":" + dart.string;
        bustThisThrow = false;
        wonLegThisThrow = false;
        scoreThisDart = dart.score;

        // score for the dart might be modified by start and finish conditions
        scoreThisTurn = scoreThisTurn + scoreThisDart;
        checkStartAndFinishConditions();
        myTotalScore = myTotalScore - scoreThisDart;

        //Write them to the screen
        d2Score.setText("("+Integer.toString(scoreThisDart)+") ["+Integer.toString(scoreThisTurn)+"]");
        scoreTotal.setText(Integer.toString(myTotalScore));

        if (bustThisThrow || wonLegThisThrow) {
            finishThrow();
        }
        else {
            getDart3();
        }

    } //Ends dart2Thrown


    public void dart3Thrown() {

        //Player has entered the score. Prevent them from going back to edit it
        d3Input.setFocusable(false);

        //Remove the textwatcher
        d3Input.removeTextChangedListener(dart3Watcher);
        textWatcherRunning = false;

        dart.analyse(d3Input.getText().toString().trim());

        throwString = throwString + ":" + dart.string;
        bustThisThrow = false;
        wonLegThisThrow = false;
        scoreThisDart = dart.score;

        // score for the dart might be modified by start and finish conditions
        scoreThisTurn = scoreThisTurn + scoreThisDart;
        checkStartAndFinishConditions();
        myTotalScore = myTotalScore - scoreThisDart;

        finishThrow();

    } //Ends dart3Thrown

    private void finishThrow() {

        //Turn finished

        //Hide the keyboard and the score section

        hideKeyboard();
        scoreSection.setVisibility(View.GONE);

        waitingMessage.setText(theirNickname + "'s go. Please wait ...");
        waitingMessage.setVisibility(View.VISIBLE);

        //Work out scores if a winning dart was thrown
        //We'll work out if the game is over when we read the record back
        if (wonLegThisThrow) {
            legScores.set(myScoreIndex, legScores.get(myScoreIndex) + 1);
            if (legScores.get(myScoreIndex) == legsToWin) {
                setScores.set(myScoreIndex,setScores.get(myScoreIndex)+1);
                legScores.set(myScoreIndex,0);
                legScores.set(theirScoreIndex,0);
            }
        }


        //write out the score record - which will then force the flip to the other player's turn
        score.setThrower(myUId);
        score.setThrowString(throwString);
        score.setThrowScore(scoreThisTurn);
        totalScore.set(myScoreIndex,myTotalScore);
        score.setTotalScore(totalScore);
        hasStarted.set(myScoreIndex,iHaveStarted);
        score.setHasStarted(hasStarted);
        score.setLegScores(legScores);
        score.setSetScores(setScores);
        score.setLegFinished(wonLegThisThrow);

        scoresDataBaseReference = fbDatabase.getReference("scores").child(matchID);
        scoresDataBaseReference.push().setValue(score);

        //Update the number of score records in the match record
        matchDetails.incrementNumberOfScoreRecords();
        matchDatabaseReference.child(matchID).child("numberOfScoreRecords").setValue(matchDetails.getNumberOfScoreRecords());

        //Write out the match record if the leg was won
        //TODO: Consider using updateChildren here and elsewhere when doing multiple connected updates
        if (wonLegThisThrow) {
            matchDatabaseReference.child(matchID).child("setScores").setValue(setScores);
            matchDatabaseReference.child(matchID).child("legScores").setValue(legScores);
        }

    }

    private void checkStartAndFinishConditions() {


            //Starting conditions
            if (!iHaveStarted) { //I haven't started, so I must need a double to start
                if (dart.isDouble) {
                    iHaveStarted = true;}
                else { //Not a double so scores 0
                    scoreThisDart = 0;
                    scoreThisTurn = 0;
                }
            }

            //Finishing conditions
            if (endWithDouble) { //Double needed to finish
                if (dart.isDouble && (scoreThisDart == myTotalScore)) { //Throw a double to get exactly to 0
                    wonLegThisThrow = true;
                } else { //Didn't get to 0
                    if (scoreThisDart >= (myTotalScore - 1)) { //Throw anything that takes me to 1 or less
                        bustThisThrow = true;
                        scoreThisTurn = scoreThisTurn - scoreThisDart;
                        scoreThisDart = 0;
                    }
                }
            } else { //Double not needed to finish
                    if (scoreThisDart >= myTotalScore) { //I get to the finishing point, so I win
                    wonLegThisThrow = true;
                }
            }

        } //Ends checkStartAndFinishConditions

  /*
        private void setDartInputFilters(EditText editText, Button button) {

            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

            editText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {

                    if (s.toString().matches(scorePattern) && s.length() > 0)
                    {
                        button.setEnabled(true);
                    }
                    else
                    {
                        button.setEnabled(false);
                    }
                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
            });

        } //Ends setDartInputFilters

   */

    private void completeThisLeg(String winner) {

        //Work out where we are in the game
        matchOver = false;
        setOver = false;
        messageText = "Leg to ";
        if ((legScores.get(myScoreIndex) == 0) && (legScores.get(theirScoreIndex) == 0)) { //Leg scores are 0 so must have finished the set
            setOver = true;
            messageText = "Leg and set to ";
            if ((setScores.get(myScoreIndex) == setsToWin) || (setScores.get(theirScoreIndex) == setsToWin)) { //Someone has m
                matchOver = true;
                messageText = "Leg, set and match to ";
            }
        }

        if (winner.equals(myUId)) {
            messageText = messageText + "you";
        }
        else {
            messageText = messageText + theirNickname;
        }

        //Tell the player what's happened
        //TODO: need to consider re-using waitingMessage here
        //TODO: do we still need the gameMessage section at the bottom?
        scoreSection.setVisibility(View.GONE);
        waitingMessage.setText(messageText);
        waitingMessage.setVisibility(View.VISIBLE);

        messageCountDownTimer = new CountDownTimer(Parameters.gameMessageTimeout,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                //Both players reset the number of score records they've read and then reset the match object
                numberOfScoreRecordsRead = 0;
                if (matchOver) { matchDetails.setNumberOfScoreRecords(0); }
                else { matchDetails.setNumberOfScoreRecords(1); }

                //The loser (or the only player if in demo mode) clears down the score records and if the match is still in progress writes a new seed record
                if (!winner.equals(myUId) || demoMode) {
                    scoresDataBaseReference = fbDatabase.getReference("scores").child(matchID);
                    scoresDataBaseReference.removeValue();
                    if (!matchOver) {
                        score.setThrower(whoStartedLastLegId); //id of person starting last leg so that the other player starts next leg
                        score.setThrowString("");
                        score.setThrowScore(-1);
                        score.setTotalScore(new ArrayList<Integer>(Arrays.asList(new Integer[]{startingPoints, startingPoints})));
                        score.setHasStarted(new ArrayList<Boolean>(Arrays.asList(new Boolean[]{!startWithDouble, !startWithDouble})));
                        score.setLegScores(legScores);
                        score.setSetScores(setScores);
                        score.setLegFinished(false);
                        scoresDataBaseReference.push().setValue(score);
                    }
                    //Update the number of score records in the match record
                    matchDatabaseReference.child(matchID).child("numberOfScoreRecords").setValue(matchDetails.getNumberOfScoreRecords());
                }
                 /*
                If the match is over both players delete their Match in Progress message records
                //and set their profiles to Not Engaged
                 */
                if (matchOver) {
                    //Profile:
                    scoresDataBaseReference = fbDatabase.getReference("player_profiles");
                    scoresDataBaseReference.child(myUId).child("playerEngaged").setValue(false);
                    //Messages (clear all)
                    scoresDataBaseReference = fbDatabase.getReference().child("player_messages").child(myUId);
                    scoresDataBaseReference.removeValue();

                    //And get out of here
                    finish();
                }

            }

        };
        messageCountDownTimer.start();

    } //Ends completeThisLeg()

    private void dummyGo() {

        //Wait a few seconds so that it appears that the user is waiting for the opponent to throw
        //Make sure that we don't get multiple handlers running
        if (!dummyDelayHandlerRunning) {
            dummyDelayHandlerRunning = true;
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    dummy3Throws();
                }
            }, Parameters.dummyGoWaitTime);
        }

 /*
        try {
            Thread.sleep(Parameters.dummyGoWaitTime);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
*/

    } //ends dummyGo()

        private void dummy3Throws () {

        /*
        Simulates the other player having a go:
        - "Throws" three darts
        - Doesn't respect needing a double to start
        - Never throws a winning dart
         */

            int dummyDartsThrown = 0;

            // Log.d(TAG,"Entering dummy3Throws");

            dummyDelayHandlerRunning = false;
            throwString = "";
            scoreThisTurn = 0;

            //Get 3 darts
            while (dummyDartsThrown < 3) {

                dart.analyse(dummyDart());

                //We don't want dummy to ever win, so modify the score if it looks a possibility
                if ((dummyTotalScore-dart.score)<2){
                    dart.analyse("m");
                }

                if (dummyDartsThrown == 0) {
                    throwString = dart.string;
                }
                else {
                    throwString = throwString + ":" + dart.string;
                }
                scoreThisTurn = scoreThisTurn + dart.score;
                dummyTotalScore = dummyTotalScore - dart.score;
                dummyDartsThrown ++;
            }

            //write out the score record - which will then force the flip to the player's turn
            score.setThrower(theirID);
            score.setThrowString(throwString);
            score.setThrowScore(scoreThisTurn);
            totalScore.set(theirScoreIndex,dummyTotalScore);
            score.setTotalScore(totalScore);
            hasStarted.set(theirScoreIndex,true);
            score.setHasStarted(hasStarted);
            score.setLegScores(legScores);
            score.setSetScores(setScores);
            score.setLegFinished(false);

            scoresDataBaseReference = fbDatabase.getReference("scores").child(matchID);
            scoresDataBaseReference.push().setValue(score);

            //Update the number of score records in the match record
            matchDetails.incrementNumberOfScoreRecords();
            matchDatabaseReference.child(matchID).child("numberOfScoreRecords").setValue(matchDetails.getNumberOfScoreRecords());

            // Log.d(TAG,"Exiting dummy3Throws");

        } //ends dummy3Throws


        private String dummyDart() {

        /*
        Constructs a string as if a user has entered a dart result from the keyboard
         */

        String dartString;
        Double randomNumber;

        //Work out if the dart is straight, miss, double or treble
        randomNumber = Math.random();
        if (randomNumber < 0.5) { //0.0 to 0.5: straight
            dartString = "";
        }
        else if (randomNumber < 0.7 ){ //0.5 to 0.7 miss
            dartString = "m";
        }
        else if (randomNumber < 0.85) { //0.7 to 0.85 double
            dartString = "d";
        }
        else {  //0.85 to 1.0 treble
            dartString = "t";
        }

        //If it wasn't a miss, add a number between 1 and 5
        if (!dartString.equals("m")) {
            dartString = dartString + String.format("%.0f", Math.floor((Math.random() * 5) + 1));
        }

        return dartString;

        } //ends dummyDart

    private void hideKeyboard() {

        //Make sure that the keyboard is invisible
        throwKeyboard.setVisibility(View.GONE);
        waitingMessage.setVisibility(View.VISIBLE);
        // Log.d(TAG,"Hid the keyboard");

        /*
        //Move the prompts right down to the bottom fo the screen to give more room for the recycler view
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) d1Prompt.getLayoutParams();
        params.bottomToTop = waitingMessage.getId();
        d1Prompt.setLayoutParams(params);
        */

    } //Ends hideKeyboard

    private void showKeyboard() {

        throwKeyboard.setVisibility(View.VISIBLE);
        waitingMessage.setVisibility(View.GONE);
        // Log.d(TAG,"Displayed the keyboard");

        /*
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) d1Prompt.getLayoutParams();
        params.bottomToTop = throwKeyboard.getId();
        d1Prompt.setLayoutParams(params);
         */

    } //Ends hideKeyboard

} //Ends PlayDarts.java


