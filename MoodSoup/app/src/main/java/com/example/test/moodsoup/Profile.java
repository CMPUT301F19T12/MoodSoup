package com.example.test.moodsoup;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Profile {
    private FirebaseAuth mAuth;
    private String username;
    private String following;
    private ArrayList<Mood> moodHistory = new ArrayList<Mood>(); // all events
    ListView event_list;
    ArrayAdapter<Mood> MoodAdapter;

    public Profile(String username,String following, ArrayList<Mood> allEvents){
        this.username = username;
        this.following = following;
        this.moodHistory = moodHistory;
    }


    //Send Auth to firebase to follow specific user

    public void request_follow(String username){
        mAuth = FirebaseAuth.getInstance();




    }

}
