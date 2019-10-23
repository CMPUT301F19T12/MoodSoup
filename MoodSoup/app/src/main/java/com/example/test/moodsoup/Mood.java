package com.example.test.moodsoup;

public class Mood {
    private String emotion;
    private String date;
    private String time;
    private String reason;
    private User creator;

    public Mood(User creator, String emotion, String date, String time, String reason) {
        this.creator = creator;
        this.emotion = emotion;
        this.date = date;
        this.time = time;
        this.reason = reason;
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

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
}
