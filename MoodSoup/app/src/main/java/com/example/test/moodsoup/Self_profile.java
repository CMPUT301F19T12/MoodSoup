package com.example.test.moodsoup;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Self_profile {
    private String username;
    private ArrayList<Mood> allEvents = new ArrayList<Mood>();
    ListView event_list;
    ArrayAdapter<Mood> MoodAdapter;

    public Self_profile(String username, ArrayList<Mood> allEvents){
        this.username = username;
        this.allEvents = allEvents;
    }

    public void delete_event(){

    }
}
