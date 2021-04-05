package com.gmp.android.darts42;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class PlayDarts extends AppCompatActivity {

    Integer totalScore = 0;

    MyRecyclerViewAdapter adapter;

    ArrayList<String> scoreLinesIn4Columns = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_darts);
/*
        mSendButton = (Button) findViewById(R.id.sendButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        textTotalScore = (TextView) findViewById(R.id.textView2);
        bottomSectionPrompt = (LinearLayout) findViewById(R.id.bottomSectionPrompt);
        bottomSectionWaiting = (LinearLayout) findViewById(R.id.bottomSectionWaiting);

        // set up the RecyclerView
        recyclerView = findViewById(R.id.rvScores);

        // Create a grid layout with six columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 8);


        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setLayoutManager(new GridLayoutManager(this,4));

        // Create a custom SpanSizeLookup where the first item spans both columns
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (((position % 4) == 1) || ((position % 4) == 2))
                    return 1;
                else
                    return 3;
            }
        });

        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new MyRecyclerViewAdapter(this, scoreLinesIn4Columns);
        //adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);


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
    }
/*
        private void attachDatabaseReadListener() {
            if (mChildEventListener == null) {
                mChildEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        //Unwrap the message
                        PlayerMessage playerMessage = dataSnapshot.getValue(PlayerMessage.class);

                        Log.d(TAG, "Player message received from "
                                + playerMessage.getSender()
                                + " of type "
                                + playerMessage.getMessageType()
                                + " with payload "
                                + playerMessage.getPayload());

                        //Delete the message once it's been read
                        mScoresDatabaseReference.child(dataSnapshot.getKey()).removeValue();
                    /*

                    Score scoreRow = dataSnapshot.getValue(Score.class);

                    */
                    /*
                    Three cases
                    1) it's a seed record
                    2) it's a home score
                    3) it's an away score
                     */
                    /*
                    if (scoreRow.getFieldTotalScore()<0) {
                        scoreLinesIn4Columns.add("");
                        scoreLinesIn4Columns.add(Integer.toString(0));
                        scoreLinesIn4Columns.add(Integer.toString(0));
                        scoreLinesIn4Columns.add("");
                        //This needs some sorting out. It's not robust

                    }

                    else {

                    if (scoreRow.getFieldName().equals(mUsername)) {
                        scoreLinesIn4Columns.add(Integer.toString(scoreRow.getFieldScore()));
                        scoreLinesIn4Columns.add(Integer.toString(scoreRow.getFieldTotalScore()));
                        scoreLinesIn4Columns.add(scoreLinesIn4Columns.get(scoreLinesIn4Columns.size()-4));
                        scoreLinesIn4Columns.add("");
                    // In this case it's the other player to go next
                        bottomSectionPrompt.setVisibility(View.GONE);
                        bottomSectionWaiting.setVisibility(View.VISIBLE);
                    // Remind home player of his total score
                        totalScore = scoreRow.getFieldTotalScore();

                    }
                    else {
                        scoreLinesIn4Columns.add("");
                        scoreLinesIn4Columns.add(scoreLinesIn4Columns.get(scoreLinesIn4Columns.size()-4));
                        scoreLinesIn4Columns.add(Integer.toString(scoreRow.getFieldTotalScore()));
                        scoreLinesIn4Columns.add(Integer.toString(scoreRow.getFieldScore()));
                    // In this case it's the home player to go next
                        bottomSectionWaiting.setVisibility(View.GONE);
                        bottomSectionPrompt.setVisibility(View.VISIBLE);
                    }}

                    adapter.notifyItemRangeInserted(adapter.getItemCount(),4);
                    textTotalScore.setText(String.format("%1$d", totalScore));
                    recyclerView.scrollToPosition(adapter.getItemCount()-1);

                    */

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
                mScoresDatabaseReference.addChildEventListener(mChildEventListener);

            }
        }

        private void detachDatabaseReadListener() {
            if (mChildEventListener != null) {
                mScoresDatabaseReference.removeEventListener(mChildEventListener);
                mChildEventListener = null;
            }
        }


        // The dialog fragment receives a reference to this Activity through the
        // Fragment.onAttach() callback, which it uses to call the following methods
        // defined by the NoticeDialogFragment.NoticeDialogListener interface
        @Override
        public void onDialogPositiveClick(DialogFragment dialog) {

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
























    }


}