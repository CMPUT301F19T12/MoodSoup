package com.example.test.moodsoup;

import java.util.ArrayList;

public class Profile {
    private String username;
    private ArrayList<Following> followings = new ArrayList<Following>();
    private ArrayList<Mood> allEvents = new ArrayList<Mood>();

    public Profile(String username, ArrayList<Following> followings, ArrayList<Mood> allEvents){
        this.username = username;
        this.followings = followings;
        this.allEvents = allEvents;
    }

    public void request_follow(String username){
    }

}
