package com.gmp.android.darts42;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayDarts extends AppCompatActivity {


    private static final String TAG = "PlayDarts";

    private static final String scorePattern = "^([bim]{1})|^(^[dt]?[12]{1}[0]{1})|^(^[dt]?[1]?[1-9]{1})";

    CommonData commonData;
    String myUId;
    String myNickname;
    String theirID;
    String theirNickname;

    Integer myTotalScore;
    Integer theirTotalScore;
    Boolean iHaveStarted;
    Boolean theyHaveStarted;

    Integer scoreThisTurn;
    Integer scoreThisDart;
    Boolean bustThisThrow;
    Boolean wonLegThisThrow;
    Boolean legFinished;



    FirebaseDatabase fbDatabase;
    DatabaseReference scoresDataBaseReference;
    ChildEventListener scoresChildEventListener;

    RecyclerView recyclerView;



    TextView lSets;
    TextView rSets;
    TextView lLegs;
    TextView rLegs;
    TextView lName;
    TextView rName;

    TextView d1ScoreThisDart;
    TextView d1ScoreThisTurn;
    TextView d1ScoreTotal;
    TextView d2ScoreThisDart;
    TextView d2ScoreThisTurn;
    TextView d2ScoreTotal;
    TextView d3ScoreThisDart;
    TextView d3ScoreThisTurn;
    TextView d3ScoreTotal;

    Button d1Send;
    Button d2Send;
    Button d3Send;



    Group scoreSection;
    Group d1Section;
    Group d2Section;
    Group d3Section;
    TextView waitingMessage;
    TextView gameMessage;

    EditText d1Input;
    EditText d2Input;
    EditText d3Input;



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

    private String messageText;

    private CountDownTimer messageCountDownTimer;

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

        //Find all the elements of the screen
        lSets = (TextView) findViewById(R.id.tvlSets);
        rSets = (TextView) findViewById(R.id.tvrSets);
        lLegs = (TextView) findViewById(R.id.tvlLegs);
        rLegs = (TextView) findViewById(R.id.tvrLegs);
        lName = (TextView) findViewById(R.id.tvlName);
        rName = (TextView) findViewById(R.id.tvrName);
        waitingMessage = (TextView) findViewById(R.id.tvWaitingMessage);
        gameMessage = (TextView) findViewById(R.id.tvGameMessage);

        d1ScoreThisDart = (TextView) findViewById(R.id.tvd1ScoreThisDart);
        d1ScoreThisTurn = (TextView) findViewById(R.id.tvd1ScoreThisTurn);
        d1ScoreTotal = (TextView)  findViewById(R.id.tvd1ScoreTotal);
        d2ScoreThisDart = (TextView) findViewById(R.id.tvd2ScoreThisDart);
        d2ScoreThisTurn = (TextView) findViewById(R.id.tvd2ScoreThisTurn);
        d2ScoreTotal = (TextView)  findViewById(R.id.tvd2ScoreTotal);
        d3ScoreThisDart = (TextView) findViewById(R.id.tvd3ScoreThisDart);
        d3ScoreThisTurn = (TextView) findViewById(R.id.tvd3ScoreThisTurn);
        d3ScoreTotal = (TextView)  findViewById(R.id.tvd3ScoreTotal);

        d1Send = (Button) findViewById(R.id.btd1);
        d2Send = (Button) findViewById(R.id.btd2);
        d3Send = (Button) findViewById(R.id.btd3);


        scoreSection = (Group) findViewById(R.id.gScoreSection);
        d1Section = (Group) findViewById(R.id.gd1Section);
        d2Section = (Group) findViewById(R.id.gd2Section);
        d3Section = (Group) findViewById(R.id.gd3Section);

        d1Input = (EditText) findViewById(R.id.etd1Input);
        d2Input = (EditText) findViewById(R.id.etd2Input);
        d3Input = (EditText) findViewById(R.id.etd3Input);

        setDartInputFilters(d1Input,d1Send);
        setDartInputFilters(d2Input,d2Send);
        setDartInputFilters(d3Input,d3Send);

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
                        Log.d(TAG, "Match message found " + childSnapshot.getKey());
                        matchID = type102Message.getPayload();
                        theirID = type102Message.getSender();

                        //Now get the match details
                        getMatchDetails();
                    }
                } else {
                    // Couldn't find the match message - do nothing
                    Log.d(TAG, "102 message not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Goes with the match look up
            }
        });

    } //Ends onCreate

    private void getMatchDetails() {

        DatabaseReference fbMatchDatabaseReference;

        //Read the match record
        fbMatchDatabaseReference = fbDatabase.getReference("matches");
        Query matchData = fbMatchDatabaseReference.orderByKey().equalTo(matchID).limitToFirst(1);
        matchData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    for (DataSnapshot matchChildSnapshot : snapshot.getChildren()) {

                        matchDetails = matchChildSnapshot.getValue(Match.class);
                        Log.d(TAG, "Match data found");

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
                        Log.d(TAG, "Opponent profile found");

                        theirNickname = theirProfile.getPlayerNickName();

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
                case 'i': //i inner
                    this.score = 25;
                    break;
                case 'm': //m miss
                    this.score = 0;
                    break;
                default: //not b,i,m
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
    
    private void getThrowScores () {
/*

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(view -> {
            // Send messages on click

            thisScore = Integer.parseInt(mMessageEditText.getText().toString());
            totalScore = totalScore + thisScore;
            Score score = new Score(thisScore, totalScore, mUsername);
            mScoresDatabaseReference.push().setValue(score);


            // Clear input box
            mMessageEditText.setText("");

        });
*/   
        
        
        
    }//Ends getThrowScores
    

        private void playGame () {



            //Set up the top section. "I" am always on the left
            lName.setText(myNickname);
            rName.setText(theirNickname);
            lSets.setText("");
            rSets.setText("");
            lLegs.setText("");
            rLegs.setText("");


            //Clear the bottom section
            scoreSection.setVisibility(View.INVISIBLE);
            gameMessage.setText("");
            
            //Show waiting message regardless of turn
            waitingMessage.setVisibility(View.VISIBLE);
            waitingMessage.setText("setting up match. Please wait ...");


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

                        //Unwrap the message
                        Score scoreRecord = dataSnapshot.getValue(Score.class);

                        Log.d(TAG, "Score record from "
                                + scoreRecord.getThrower()
                                + " with string "
                                + scoreRecord.getThrowString()
                                + " and throw score "
                                + String.format("%1$d", scoreRecord.getThrowScore()));

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

                        //write out sets and legs
                        lSets.setText(String.format("%1$d",setScores.get(myScoreIndex)));
                        rSets.setText(String.format("%1$d",setScores.get(theirScoreIndex)));
                        lLegs.setText(String.format("%1$d",legScores.get(myScoreIndex)));
                        rLegs.setText(String.format("%1$d",legScores.get(theirScoreIndex)));


                        if (scoreRecord.getThrowScore() < 0) { //Seed record because the total score is negative
                            //Clear the list
                            scoreLinesIn4Columns.clear();

                            //Start adding new entries
                            scoreLinesIn4Columns.add("");
                            scoreLinesIn4Columns.add(Integer.toString(startingPoints));
                            scoreLinesIn4Columns.add(Integer.toString(startingPoints));
                            scoreLinesIn4Columns.add("");
                            //Sort out who to go next
                            if (scoreRecord.getThrower().equals(myUId)) {
                                myGoNext = false;
                            } else {
                                myGoNext = true;
                            }
                        } else { //Not a seed record
                             if (scoreRecord.getThrower().equals(myUId)) { //"my" score - because it's my ID
                                scoreLinesIn4Columns.add(scoreRecord.getThrowString()+String.format(" [%1d]",scoreRecord.getThrowScore()));
                                scoreLinesIn4Columns.add(Integer.toString(scoreRecord.getTotalScore().get(myScoreIndex)));
                                scoreLinesIn4Columns.add(Integer.toString(scoreRecord.getTotalScore().get(theirScoreIndex)));
                                scoreLinesIn4Columns.add("");
                                myGoNext = false; //Other player to go next
                                // In this case it's the other player to go next


                            } else { // "their" score
                                scoreLinesIn4Columns.add("");
                                scoreLinesIn4Columns.add(Integer.toString(scoreRecord.getTotalScore().get(myScoreIndex)));
                                scoreLinesIn4Columns.add(Integer.toString(scoreRecord.getTotalScore().get(theirScoreIndex)));
                                scoreLinesIn4Columns.add(String.format("[%1d] ",scoreRecord.getThrowScore())+scoreRecord.getThrowString());
                                myGoNext = true; //Me to go
                            }

                        }
                            //Get all the above on the screen;
                            adapter.notifyItemRangeInserted(adapter.getItemCount(), 4);
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                        if (legFinished) { //Leg has been won
                            completeThisLeg(scoreRecord.getThrower());
                        }
                        else { //Leg still in progress

                            //Get next input
                            if (myGoNext) { //My go, so get input
                                getDart1();
                            } else { //Their go, so I'm told to wait
                                messageText = "Waiting for " + theirNickname + " to throw";
                                waitingMessage.setText(messageText);
                                scoreSection.setVisibility(View.INVISIBLE);
                                waitingMessage.setVisibility(View.VISIBLE);
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

    private void detachScoresListener() {
        if (scoresChildEventListener != null) {
        scoresDataBaseReference.removeEventListener(scoresChildEventListener);
        scoresChildEventListener = null;
        }
        }// Ends detachScoresListener


    private void getDart1() {

        waitingMessage.setVisibility(View.INVISIBLE);
        scoreSection.setVisibility(View.VISIBLE);
        d1Input.getText().clear();
        d1ScoreThisDart.setText("");
        d1ScoreThisTurn.setText("");
        d1ScoreTotal.setText("");

        //setDartInputFilters(d1Input,d1Send);
        d1Section.setVisibility(View.VISIBLE);
        d1Send.setEnabled(false);
        d1Input.setFocusableInTouchMode(true);
        d1Input.setFocusable(true);
        d1Input.requestFocus();
        d2Section.setVisibility(View.INVISIBLE);
        d3Section.setVisibility(View.INVISIBLE);

    } //Ends getDart1()

    private void getDart2() {

        d2Input.setText("");
        d2ScoreThisDart.setText("");
        d2ScoreThisTurn.setText("");
        d2ScoreTotal.setText("");

        //setDartInputFilters(d2Input,d2Send);
        d2Section.setVisibility(View.VISIBLE);
        d2Send.setEnabled(false);
        d2Input.setFocusableInTouchMode(true);
        d2Input.setFocusable(true);
        d2Input.requestFocus();
        d3Section.setVisibility(View.INVISIBLE);

    } //Ends getDart2()

    private void getDart3() {

        d3Input.setText("");
        d3ScoreThisDart.setText("");
        d3ScoreThisTurn.setText("");
        d3ScoreTotal.setText("");

        //setDartInputFilters(d3Input,d3Send);
        d3Section.setVisibility(View.VISIBLE);
        d3Send.setEnabled(false);
        d3Input.setFocusableInTouchMode(true);
        d3Input.setFocusable(true);
        d3Input.requestFocus();


    } //Ends getDart3()

        public void dart1Thrown(View view) {

            //Player has entered the score. Prevent them from going back to edit it
            d1Input.setFocusable(false);
            d1Send.setEnabled(false);
            d1Send.setVisibility(View.INVISIBLE);

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
            d1ScoreThisDart.setText(Integer.toString(scoreThisDart));
            d1ScoreThisTurn.setText(Integer.toString(scoreThisTurn));
            d1ScoreTotal.setText(Integer.toString(myTotalScore));

            if (bustThisThrow || wonLegThisThrow) {
                hideKeyboard();
                finishThrow();
            }
            else {
                getDart2();
            }

        } //Ends dart1Thrown

    public void dart2Thrown(View view) {

        //Player has entered the score. Prevent them from going back to edit it
        d2Input.setFocusable(false);
        d2Send.setEnabled(false);
        d2Send.setVisibility(View.INVISIBLE);


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
        d2ScoreThisDart.setText(Integer.toString(scoreThisDart));
        d2ScoreThisTurn.setText(Integer.toString(scoreThisTurn));
        d2ScoreTotal.setText(Integer.toString(myTotalScore));

        if (bustThisThrow || wonLegThisThrow) {
            hideKeyboard();
            finishThrow();
        }
        else {
            getDart3();
        }

    } //Ends dart2Thrown

    public void dart3Thrown(View view) {

        //Player has entered the score. Prevent them from going back to edit it
        d3Input.setFocusable(false);
        d3Send.setEnabled(false);

        dart.analyse(d3Input.getText().toString().trim());

        throwString = throwString + ":" + dart.string;
        bustThisThrow = false;
        wonLegThisThrow = false;
        scoreThisDart = dart.score;

        // score for the dart might be modified by start and finish conditions
        scoreThisTurn = scoreThisTurn + scoreThisDart;
        checkStartAndFinishConditions();
        myTotalScore = myTotalScore - scoreThisDart;

        //Write them to the screen
        d3ScoreThisDart.setText(Integer.toString(scoreThisDart));
        d3ScoreThisTurn.setText(Integer.toString(scoreThisTurn));
        d3ScoreTotal.setText(Integer.toString(myTotalScore));

        hideKeyboard();
        finishThrow();

    } //Ends dart3Thrown

    private void finishThrow() {

        //Turn finished
        scoreSection.setVisibility(View.INVISIBLE);
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

        //Write out the match record if the leg was won
        //TODO: Consider using updateChildren here and elsewhere when doing multiple connected updates
        if (wonLegThisThrow) {
            scoresDataBaseReference = fbDatabase.getReference("matches").child(matchID).child("setScores");
            scoresDataBaseReference.setValue(setScores);
            scoresDataBaseReference = fbDatabase.getReference("matches").child(matchID).child("legScores");
            scoresDataBaseReference.setValue(legScores);
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
                if (scoreThisDart > myTotalScore) { //Double needed, but I threw too much
                    bustThisThrow = true;
                    scoreThisTurn = scoreThisTurn = scoreThisDart;
                    scoreThisDart = 0;
                }
                else if (scoreThisDart == myScoreIndex)  { //Double needed, and I threw exactly what was needed
                    if (dart.isDouble) { //it was a double, so I won
                        wonLegThisThrow = true;
                    }
                    else { //it wasn't a double, so I'm bust
                        bustThisThrow = true;
                        scoreThisTurn = scoreThisTurn = scoreThisDart;
                        scoreThisDart = 0;
                    }
                }
            } else { //Double not needed to finish
                if (scoreThisDart >= myTotalScore) { //I get to the finishing point, so I win
                    wonLegThisThrow = true;
                }
            }

        } //Ends checkStartAndFinishConditions

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

    private void completeThisLeg(String winner) {

        //Work out where we are in the game
        matchOver = false;
        setOver = false;
        messageText = "Leg to ";
        if ((legScores.get(myScoreIndex) == 0) && (legScores.get(theirScoreIndex) == 0)) { //Leg scores are 0 so must have finished the set
            setOver = true;
            messageText = "Leg and set to ";
            if ((setScores.get(myScoreIndex) == setsToWin) || (setScores.get(theirScoreIndex) == setsToWin)) { //Someone has won
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
        scoreSection.setVisibility(View.INVISIBLE);
        waitingMessage.setText(messageText);
        waitingMessage.setVisibility(View.VISIBLE);

        messageCountDownTimer = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                //The loser clears down the score records and if the match is still in progress writes a new seed record
                if (winner != myUId) {
                    scoresDataBaseReference = fbDatabase.getReference("scores").child(matchID);
                    scoresDataBaseReference.removeValue();
                    if (!matchOver) {
                        score.setThrower(winner); //Winner's id so that the loser starts next leg
                        score.setThrowString("");
                        score.setThrowScore(-1);
                        score.setTotalScore(new ArrayList<Integer>(Arrays.asList(new Integer[]{startingPoints, startingPoints})));
                        score.setHasStarted(new ArrayList<Boolean>(Arrays.asList(new Boolean[]{!startWithDouble, !startWithDouble})));
                        score.setLegScores(legScores);
                        score.setSetScores(setScores);
                        score.setLegFinished(false);
                        scoresDataBaseReference.push().setValue(score);
                    }

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

    private void hideKeyboard() {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    } //Ends hideKeyboard

        } //Ends PlayDarts.java


