package com.gmp.android.darts42;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PlayerProfile {

    private String playerName;
    private String playerNickName;
    private String playerEMail;
    private Boolean playerEngaged;

    public PlayerProfile() {
    }

    public PlayerProfile(String playerName, String playerNickName, String playerEMail, Boolean playerEngaged) {
        this.playerName = playerName;
        this.playerNickName = playerNickName;
        this.playerEMail = playerEMail;
        this.playerEngaged = playerEngaged;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerNickName() {
        return playerNickName;
    }

    public String getPlayerEMail() {
        return playerEMail;
    }

    public Boolean getPlayerEngaged() {return playerEngaged;}

    public void setPlayerEMail(String playerEMail) {
        this.playerEMail = playerEMail;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setPlayerNickName(String playerNickName) {
        this.playerNickName = playerNickName;
    }

    public void setPlayerEngaged(Boolean playerEngaged) {
        this.playerEngaged = playerEngaged;
    }

    /*

    public Boolean playerExists(String playerUid) {
        FirebaseDatabase mDatabase;
        DatabaseReference mDatabaseReference;
        ChildEventListener mChildEventListener;
        Boolean foundProfile = false;


        mDatabase = FirebaseDatabase.getInstance();


        DatabaseReference playerProfileReference = mDatabase.getReference().child("player_profiles");
        Query data = playerProfileReference.orderByKey().equalTo(playerUid).limitToFirst(1);
        data.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Log.d("MainActivity", "User profile found");
                    //TODO: unwrap the profile, get player status, if game is being played then get game and start play activity. Otherwise set player status
                    foundProfileMethod();
                }
            else{
                    Log.d("MainActivity", "No user status found");

                    //TODO: set player status
                }
            }

            private void foundProfileMethod() {foundProfile = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return true;

    }



*/


   }