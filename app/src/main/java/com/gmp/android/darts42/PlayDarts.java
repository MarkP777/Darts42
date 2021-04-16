package com.gmp.android.darts42;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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

public class PlayDarts extends AppCompatActivity {


    private static final String TAG = "PlayDarts";

    CommonData commonData;
    String myUId;
    String myNickname;
    String theirID;
    String theirNickname;

    Integer myTotalScore;
    Integer theirTotalScore;
    Boolean iHaveStarted;
    Boolean theyHaveStarted;



    FirebaseDatabase fbDatabase;
    DatabaseReference scoresDataBaseReference;
    ChildEventListener scoresChildEventListener;

    RecyclerView recyclerView;

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
    Match matchDetails;
    PlayerProfile theirProfile;

    private String matchID;
    private Integer setsToWin;
    private Integer legsToWin;
    private Integer startingPoints;
    private Boolean startWithDouble;
    private Boolean endWithDouble;
    private Integer[] setsScore = new Integer[2];
    private Integer[] legsScore = new Integer[2];
    private Integer myScoreIndex;
    private Integer theirScoreIndex;
    private Boolean iAmPlayer1;

    private Boolean myGoNext;

    Dart dart = new Dart();
    Score score = new Score();
    String throwString;



    Integer totalScore = 0;

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

        //Get who we are
        commonData = CommonData.getInstance();
        myUId = commonData.getHomeUserID();
        myNickname = commonData.getHomeUserNickname();

        //Read the message. We'll assume that there is only one with type 102

        fbMessageDatabaseReference = fbDatabase.getReference().child("player_messages");
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
                            iAmPlayer1 = true;
                            myScoreIndex = 0;
                            theirScoreIndex = 1;
                        } else {
                            iAmPlayer1 = false;
                            myScoreIndex = 1;
                            theirScoreIndex = 0;
                        }
                        setsToWin = (int) Math.floor(matchDetails.getBestOfSets() / 2) + 1;
                        legsToWin = (int) Math.floor(matchDetails.getBestOfLegs() / 2) + 1;
                        startingPoints = matchDetails.getStartingPoints();
                        startWithDouble = matchDetails.getStartWithDouble();
                        endWithDouble = matchDetails.getEndWithDouble();
                        setsScore[0] = matchDetails.getPlayer1Sets();
                        setsScore[1] = matchDetails.getPlayer2Sets();
                        legsScore[0] = matchDetails.getPlayer1Legs();
                        legsScore[1] = matchDetails.getPlayer2Legs();

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

            //Find all the elements of the screen
            TextView lSets = (TextView) findViewById(R.id.tvlSets);
            TextView rSets = (TextView) findViewById(R.id.tvrSets);
            TextView lLegs = (TextView) findViewById(R.id.tvlLegs);
            TextView rLegs = (TextView) findViewById(R.id.tvrLegs);
            TextView lName = (TextView) findViewById(R.id.tvlName);
            TextView rName = (TextView) findViewById(R.id.tvrName);
            waitingMessage = (TextView) findViewById(R.id.tvWaitingMessage);
            gameMessage = (TextView) findViewById(R.id.tvGameMessage);

            scoreSection = (Group) findViewById(R.id.gScoreSection);
            d1Section = (Group) findViewById(R.id.gd1Section);
            d2Section = (Group) findViewById(R.id.gd2Section);
            d3Section = (Group) findViewById(R.id.gd3Section);

            d1Input = (EditText) findViewById(R.id.etd1Input);
            d2Input = (EditText) findViewById(R.id.etd2Input);
            d3Input = (EditText) findViewById(R.id.etd3Input);



            //Set up the top section. "I" am always on the left
            lName.setText(myNickname);
            rName.setText(theirNickname);
            lSets.setText(String.format("%1$d",setsScore[myScoreIndex]));
            rSets.setText(String.format("%1$d",setsScore[theirScoreIndex]));
            lLegs.setText(String.format("%1$d",legsScore[myScoreIndex]));
            rLegs.setText(String.format("%1$d",legsScore[theirScoreIndex]));
            
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
                    if ((position == 1) || (position == 2))
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

                        //Unwrap the message
                        Score scoreRecord = dataSnapshot.getValue(Score.class);

                        Log.d(TAG, "Score record from "
                                + scoreRecord.getThrower()
                                + " with string "
                                + scoreRecord.getThrowString()
                                + " and scores "
                                + String.format("%1$d and %2$d", scoreRecord.getThrowScore(), scoreRecord.getTotalScore()));

                        //Delete the message once it's been read
                        scoresDataBaseReference.child(dataSnapshot.getKey()).removeValue();
                    /*

                    /*
                    Three cases
                    1) it's a seed record
                    2) it's "my" score
                    3) it's "their" score
                     */

                        if (iAmPlayer1) {
                            myScoreIndex = scoreRecord.getPlayer1TotalScore();
                            iHaveStarted = scoreRecord.getPlayer1HasStarted();
                        }

                        //Seed record because the total score is negative
                        if ( < 0) {
                            scoreLinesIn4Columns.add("");
                            scoreLinesIn4Columns.add(Integer.toString(startingPoints));
                            scoreLinesIn4Columns.add(Integer.toString(startingPoints));
                            scoreLinesIn4Columns.add("");
                            //Sort out who to go next
                            if (scoreRecord.getThrower().equals(myUId)) {
                                myGoNext = false;
                            } else {
                                myGoNext = true;
                                myTotalScore = startingPoints;
                                if (startWithDouble) {
                                    theyHaveStarted = false;
                                    iHaveStarted = false;
                                }
                                else {
                                    theyHaveStarted = true;
                                    iHaveStarted = true;
                                }
                            }
                        } else {

                            if (scoreRecord.getThrower().equals(myUId)) { //"my" score - because it's my ID
                                scoreLinesIn4Columns.add(scoreRecord.getThrowString());
                                scoreLinesIn4Columns.add(Integer.toString(scoreRecord.getTotalScore()));
                                scoreLinesIn4Columns.add(scoreLinesIn4Columns.get(scoreLinesIn4Columns.size() - 4));
                                scoreLinesIn4Columns.add("");
                                myGoNext = false; //Other player to go next
                                // In this case it's the other player to go next


                            } else { // "their" score
                                scoreLinesIn4Columns.add("");
                                scoreLinesIn4Columns.add(scoreLinesIn4Columns.get(scoreLinesIn4Columns.size() - 4));
                                scoreLinesIn4Columns.add(Integer.toString(scoreRecord.getTotalScore()));
                                scoreLinesIn4Columns.add(scoreRecord.getThrowString());
                                myGoNext = true; //Me to go
                                myTotalScore = scoreRecord.getTotalScore();
                                if (startWithDouble) {
                                    theyHaveStarted = false;
                                    iHaveStarted = false;
                                }
                                else {
                                    theyHaveStarted = true;
                                    iHaveStarted = true;
                            }
                        }

                        //TODO: sort out sets and legs

                        //Get all the above on the screen
                        adapter.notifyItemRangeInserted(adapter.getItemCount(), 4);
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);

                        //Get next input
                        if (myGoNext) {
                            threeDarts();
                        }

                        //Write out the score record - must do this last so that the listener
                        //does not trigger before everything is done
                        //TODO: score record

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


    private void threeDarts() {

        TextView d2ScoreThisDart = (TextView) findViewById(R.id.tvd2ScoreThisDart);
        TextView d2ScoreThisTurn = (TextView) findViewById(R.id.tvd2ScoreThisTurn);
        TextView d2ScoreTotal = (TextView)  findViewById(R.id.tvd2ScoreTotal);
        TextView d3ScoreThisDart = (TextView) findViewById(R.id.tvd3ScoreThisDart);
        TextView d3ScoreThisTurn = (TextView) findViewById(R.id.tvd3ScoreThisTurn);
        TextView d3ScoreTotal = (TextView)  findViewById(R.id.tvd3ScoreTotal);

        Button d1Send = (Button) findViewById(R.id.btd1);
        Button d2Send = (Button) findViewById(R.id.btd2);
        Button d3Send = (Button) findViewById(R.id.btd3);

         String scorePattern = "^([bim]{1})|^(^[dt]?[12]{1}[0]{1})|^(^[dt]?[1]?[1-9]{1})";


        d1Section.setEnabled(true);
        d1Send.setEnabled(false);
        d2Section.setEnabled(false);
        d3Section.setEnabled(false);
        scoreSection.setVisibility(View.VISIBLE);

        score.setThrower(myUId);

        d1Input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        d1Input.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (s.toString().matches(scorePattern) && s.length() > 0)
                {
                    d1Send.setEnabled(true);


                    //TODO: Process the dart
                }
                else
                {
                    d1Send.setEnabled(false);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });


        //Turn finished
        scoreSection.setVisibility(View.INVISIBLE);
        waitingMessage.setText(theirNickname + "'s go. Please wait ...");
        waitingMessage.setVisibility(View.VISIBLE);


    } //Ends threeThrows()


// The dialog fragment receives a reference to this Activity through the
// Fragment.onAttach() callback, which it uses to call the following methods
// defined by the NoticeDialogFragment.NoticeDialogListener interface
@Override
public void onDialogPositiveClick(DialogFragment dialog){

        /*
        //if (BuildConfig.DEBUG) Log.w(TAG,"Returned positive from alert dialog");

        // User touched the dialog's positive button, so initialise a new game

        mScoresDatabaseReference = mScoresDatabase.getReference("scores");

        //Remove the whole of the scores branch
        mScoresDatabaseReference.removeValue();

        //Recreate the score branch and seed it with a blank record, which forces user 1 to go next
        mScoresDatabaseReference = mScoresDatabase.getReference().child("scores");
        Score score = new Score(-1,-1,mUsername);
        mScoresDatabaseReference.push().setValue(score);

        // Attach the listener after everything has been set up
        attachDatabaseReadListener();
*/

        }

        public void getDart1(View view) {

            TextView d1ScoreThisDart = (TextView) findViewById(R.id.tvd1ScoreThisDart);
            TextView d1ScoreThisTurn = (TextView) findViewById(R.id.tvd1ScoreThisTurn);
            TextView d1ScoreTotal = (TextView)  findViewById(R.id.tvd1ScoreTotal);

            //Player has entered the score. Prevent them from going back to edit it
            d1Input.setFocusable(false);

            dart.analyse(d1Input.toString().trim());

            throwString = dart.string;





        }




        } //Ends PlayDarts.java


