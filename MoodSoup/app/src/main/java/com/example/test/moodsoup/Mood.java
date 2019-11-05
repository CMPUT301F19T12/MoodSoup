package com.example.test.moodsoup;

public class Mood{
    private String email;
    private String emotion;
    private String date;
    private String time;
    private String reason;
    private String social;
    private String location;
    //private User creator;

    public Mood(String email, String date, String time, String emotion, String reason, String social, String location) {
        //this.creator = creator;
        this.email = email;
        this.emotion = emotion;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.social = social;
        this.location = location;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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


    //public User getCreator() {
    //    return creator;
    // }

    //public void setCreator(User creator) {
    //    this.creator = creator;
    //}
}
