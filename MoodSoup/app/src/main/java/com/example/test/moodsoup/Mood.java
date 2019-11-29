package com.example.test.moodsoup;

import com.google.firebase.firestore.GeoPoint;

/**
 * Mood
 * V1.2
 *
 * Holds details of a Mood Object
 *
 * @author rqin1
 */
public class Mood{
    private String email;
    private String username;
    private String emotion;
    private String date;
    private String time;
    private String reason;
    private String social;
    private String location;
    private GeoPoint coords;
    private boolean imgIncluded;

    public Mood(String email, String username, String date, String time, String emotion, String reason, String social, String location, GeoPoint coords, boolean imgIncluded) {
        this.email = email;
        this.username = username;
        this.emotion = emotion;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.social = social;
        this.location = location;
        this.coords = coords;
        this.imgIncluded = imgIncluded;
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

    public String getSocial() {
        return social;
    }

    public void setSocial(String social) {
        this.social = social;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public GeoPoint getCoords() {
        return coords;
    }

    public void setCoords(GeoPoint coords) {
        this.coords = coords;
    }

    public boolean isImgIncluded() {
        return imgIncluded;
    }

    public void setImgIncluded(boolean imgIncluded) {
        this.imgIncluded = imgIncluded;
    }
}
