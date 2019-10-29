package com.example.test.moodsoup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Profile {
    private FirebaseAuth mAuth;
    private String username;
    private String following;
    private ArrayList<Mood> allEvents = new ArrayList<Mood>();

    public Profile(String username, String following, ArrayList<Mood> allEvents){

        this.username = username;
        this.following = following;
        this.allEvents = allEvents;
    }


    // Follow a specific user
    public void request_follow(String username){
        mAuth = FirebaseAuth.getInstance();
    }


}
