package com.gmp.android.darts42;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.ServerValue;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/*
public class MainActivity extends AppCompatActivity
        implements MyRecyclerViewAdapter.ItemClickListener
*/

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    public static final int RC_SIGN_IN = 1;
    public static final String EXTRA_OPPONENTID = "Opponent_ID";
    public static final String EXTRA_MATCHID = "Match_ID";

    private final Long challengeTimeout = (long) 18000 * 1000; //Challenge timeout in milliseconds




    private EditText mMessageEditText;
    private Button actionButton;
    private TextView whatsHappeningText;
    private TextView textTotalScore;
    private Integer thisScore;
    private RecyclerView recyclerView;
    private LinearLayout bottomSectionPrompt;
    private LinearLayout bottomSectionWaiting;

    private String mUsername;
    private String mUserNickname;
    private String mUid;
    private String mEMailAddress;
    private String homeStatus;
    private Integer buttonState;
    private Long timeStampLong;
    private Calendar timeoutCalendar = Calendar.getInstance();
    private Calendar timeNowCalendar;

    private String matchID;
    private String opponentID;
    private String messageID;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mPlayerMessageListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public CommonData commonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //setSupportActionBar(findViewById(R.id.main_toolbar));
        //getSupportActionBar().setTitle("Darts 42");

        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        actionButton = (Button) findViewById(R.id.btAction);
        actionButton.setEnabled(false);

        whatsHappeningText = (TextView) findViewById(R.id.tvWhatsHappening);
        mUsername = ANONYMOUS;

        mDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        commonData = CommonData.getInstance();

        mDatabaseReference = mDatabase.getReference().child("player_messages");


        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("MainActivity", "User signed in");
                    onSignedInInitialize(user.getDisplayName(), user.getUid(), user.getEmail());


                    attachPlayerMessageListener();

                    //TODO: Set the button to default state of set up new game
                    //TODO: Set a new listener on player_messages
                    //TODO: When this fires look at the type - button changes to continue game or
                    //TODO: respond to challenge















                } else {
                    // User is signed out
                    onSignedOutCleanup();
                    //Choose authentication providers
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build());

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };


/*
        @Override
        public void onItemClick (View view,int position){
            Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        }
*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        else { //We've come from somewhere else (not sign-in). For the time being, just start again

            mDatabaseReference = mDatabaseReference.child(mUid);
            attachPlayerMessageListener();

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    /*
        if (adapter.getItemCount() != 0)
        {

            // When we resume the recycle view will be rebuilt with items from
            // the Firebase DB. So, clear the list of scores and reset the total
            scoreLinesIn4Columns.clear();
            totalScore = 0;

            // Workaround to get around Android crash
            // See https://stackoverflow.com/questions/30220771/recyclerview-inconsistency-detected-invalid-item-position
            recyclerView.getRecycledViewPool().clear();

            // Tell the adapter that everything's changed
            adapter.notifyDataSetChanged();
       }

     */
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachPlayerMessageListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent1;
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.Menu1:
                intent1 = new Intent(MainActivity.this, IssueChallenge.class);
                startActivityForResult(intent1, 1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onSignedInInitialize(String username, String userid, String userEMailAddress) {
        mUsername = username;

        mUserNickname = findNickname(mUsername);
        mUid = userid;
        mEMailAddress = userEMailAddress;

        //Put home user id in common data
        commonData.setHomeUserID(mUid);
        commonData.setHomeUserNickname(mUserNickname);
        commonData.setHomeUserEMail(userEMailAddress);

        //Check to make sure that the player has a profile
        DatabaseReference playerProfileReference = mDatabase.getReference().child("player_profiles");
        Query data = playerProfileReference.orderByKey().equalTo(mUid).limitToFirst(1);
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists())
                    Log.d("MainActivity", "User profile found");
                    //TODO: unwrap the profile


                else {
                    Log.d("MainActivity", "No user status found");

                    mDatabaseReference = mDatabase.getReference().child("player_profiles");
                    PlayerProfile playerProfile = new PlayerProfile(mUsername,mUserNickname,mEMailAddress,false);
                    mDatabaseReference.child(mUid).setValue(playerProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
  /*
        scoreLinesIn4Columns.clear();
  */
        detachPlayerMessageListener();

    }

    private void attachPlayerMessageListener() {

        //Set default state - listening for messages
        buttonState = 100;
        setButton(buttonState);

        if (mPlayerMessageListener == null) {
            mPlayerMessageListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                        //Unwrap the message
                        PlayerMessage playerMessage = dataSnapshot.getValue(PlayerMessage.class);

                        opponentID = playerMessage.getSender();
                        matchID = playerMessage.getPayload();
                        buttonState = playerMessage.getMessageType();
                        timeStampLong = playerMessage.getTimestamp();
                        messageID = dataSnapshot.getKey();


                        Log.d(TAG, "Player message "
                                + messageID
                                + " of type "
                                + buttonState
                                + " with payload "
                                + matchID
                                + " received from "
                                + opponentID
                        );



                    setButton(buttonState);
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
            //make sure that we're listening for messages at the right level
            mDatabaseReference = mDatabase.getReference().child("player_messages").child(mUid);
            //attach the listener
            mDatabaseReference.addChildEventListener(mPlayerMessageListener);

        }
    }



    private void detachPlayerMessageListener() {
        if (mPlayerMessageListener != null) {
            mDatabaseReference.removeEventListener(mPlayerMessageListener);
            mPlayerMessageListener = null;
        }
    }


    private void setButton(Integer buttonState) {

        //Default case - nothing happening
        whatsHappeningText.setText("You have no matches in progress nor any challenges");
        actionButton.setText("New match");

        switch (buttonState) {
            case 100: //Nothing happening
                actionButton.setEnabled(true);
                break;


            case 101: //Challenge
                //Check the timestamp
                timeNowCalendar = Calendar.getInstance();
                //timeoutCalendar.setTimeInMillis(timeStampLong+challengeTimeout);
                Log.d(TAG, "Now: " + timeNowCalendar.getTimeInMillis() + " Timeout: "+ timeStampLong+challengeTimeout);
                if (timeNowCalendar.getTimeInMillis() >= (timeStampLong+challengeTimeout)) { //Expired so ignore and delete the message
                    mDatabaseReference = mDatabase.getReference().child("player_messages").child(mUid).child(messageID);
                    mDatabaseReference.removeValue();
                }
                else { //Still current
                    whatsHappeningText.setText("You have received a challenge from another player");
                    actionButton.setText("Review challenge");
                    actionButton.setEnabled(true);
                }
                break;


            case 102: //Game in progress
                //TODO Elaborate
                whatsHappeningText.setText("You are playing a match");
                actionButton.setText("Continue match");
                actionButton.setEnabled(true);
                break;

            case 103: //Stray decline message
                //Delete the message
                mDatabaseReference = mDatabase.getReference().child("player_messages").child(mUid).child(messageID);
                mDatabaseReference.removeValue();
                break;

        }

    }

    public void onClick(View view) {

        switch (buttonState) {
            case 100:
                //Set up new game
                startNewMatch();
                break;


            case 101:
                //Respond to challenge
                reviewChallenge();
                break;


            case 102:
                //Continue game
                continueMatch();
                break;


        }

    }

        private void startNewMatch() {
            Log.d(TAG, "Into startNewMatch");
            detachPlayerMessageListener();
            Intent intent1;
            intent1 = new Intent(MainActivity.this, IssueChallenge.class);
            startActivityForResult(intent1, 10);
        }

        private void reviewChallenge() {
            Log.d(TAG, "Into review challenge");
            detachPlayerMessageListener();
            Intent intent1;
            intent1 = new Intent(MainActivity.this, ReviewChallenge.class);
            intent1.putExtra(ReviewChallenge.EXTRA_CHALLENGEID, (String) messageID );
            intent1.putExtra(ReviewChallenge.EXTRA_MATCHID, (String) matchID );
            intent1.putExtra(ReviewChallenge.EXTRA_OPPONENTID, (String) opponentID);
            intent1.putExtra(ReviewChallenge.EXTRA_TIMESTAMP,(long)timeStampLong);
            startActivityForResult(intent1, 11);
    }


        private void continueMatch() {
            Log.d(TAG, "Into play darts");
            detachPlayerMessageListener();

            Intent intent1;
            intent1 = new Intent(MainActivity.this, PlayDarts.class);
            startActivityForResult(intent1, 12);

        }


        private void setHomeStatus (String playerStatus){
            homeStatus = playerStatus;
        }

    private String findNickname(String fullName) {
        Integer tempIndex;
        String tempName;

        //Get rid of leading and trailing spaces
        tempName = fullName.trim();

        //If nothing left nickname becomes a space
        if (tempName.length() == 0) {
            return " ";
        } else {
            tempIndex = tempName.indexOf(" ");
            //if there are no spaces in the name, nickname is the whole name
            if (tempIndex < 0) {
                return tempName;
            } else
                //Otherwise take the substring up to, but not including, the first space
                return tempName.substring(0, tempIndex);
        }
    }
        }