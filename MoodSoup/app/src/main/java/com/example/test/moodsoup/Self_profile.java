package com.example.test.moodsoup;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Self_profile {
    private String username;
    private ArrayList<Mood> moodHistory = new ArrayList<Mood>();
    ListView event_list_self;
    ArrayAdapter<Mood> MoodAdapter;
    private FirebaseAuth mAuth;

    // View your own profile

    public Self_profile(String username, ArrayList<Mood> allEvents){
        this.username = username;
        this.moodHistory = moodHistory;
    }

    // Delete a mood

    public void delete_event(ArrayList<Mood> moodHistory){
        mAuth = FirebaseAuth.getInstance();



        // On click listener for delete button, or swipe left on mood history mood event

    }
}
