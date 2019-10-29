package com.example.test.moodsoup;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Self_profile {
    private FirebaseAuth mAuth;
    private String username;
    private ArrayList<Mood> allEvents = new ArrayList<Mood>();
    ListView event_list;
    ArrayAdapter<Mood> MoodAdapter;

    public Self_profile(String username, ArrayList<Mood> allEvents){
        this.username = username;
        this.allEvents = allEvents;
    }


    // Delete a mood - swipe left?
    public void delete_event(){
        mAuth = FirebaseAuth.getInstance();


    }
}
