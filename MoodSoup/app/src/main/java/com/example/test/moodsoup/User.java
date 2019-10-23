package com.example.test.moodsoup;

import java.util.ArrayList;

public class User {
    private String username;
    private String email;
    private ArrayList<Mood> moodHistory = new ArrayList<Mood>();
    private ArrayList<User> followers = new ArrayList<User>();
    private ArrayList<User> following = new ArrayList<User>();
    private ArrayList<User> follower_req = new ArrayList<User>();
    private ArrayList<User> following_req = new ArrayList<User>();

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

    public void removeMood(Mood mood){
        moodHistory.remove(mood);
    }

    public ArrayList<User> getFollowers() {
        return followers;
    }

    public void addFollower(User follower){
        followers.add(follower);
    }

    public void removeFollower(User follower){
        followers.remove(follower);
    }

    public ArrayList<User> getFollowing() {
        return following;
    }

    public void addFollowing(User user){
        following.add(user);
    }

    public void removeFollowing(User user){
        following.remove(user);
    }

    public ArrayList<User> getFollower_req() {
        return follower_req;
    }

    public void addFollower_req(User follower){
        follower_req.add(follower);
    }

    public void removeFollower_req(User follower){
        follower_req.remove(follower);
    }

    public void addFollowing_req(User user){
        following_req.add(user);
    }

    public void removeFollowing_req(User user){
        following_req.remove(user);
    }
    public ArrayList<User> getFollowing_req() {
        return following_req;
    }
}
