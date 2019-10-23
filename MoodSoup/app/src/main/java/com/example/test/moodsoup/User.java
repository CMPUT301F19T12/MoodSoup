package com.example.test.moodsoup;

import java.util.ArrayList;

public class User {
    private String username;
    private String email;
    private ArrayList<Mood> moodHistory = new ArrayList<Mood>();

    public User(String username, String email){
        this.username=username;
        this.email=email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Mood> getMoodHistory() {
        return moodHistory;
    }

    public void addMood(Mood mood){
        moodHistory.add(mood);
    }
}
