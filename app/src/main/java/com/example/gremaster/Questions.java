package com.example.gremaster;

//Model class for storing questions in forum
public class Questions {

    private String name, profileImage, question, time, date, status, key;
    public Questions(){

    }
    public Questions(String name, String profileImage, String question, String time, String date, String status) {
        this.name = name;
        this.profileImage = profileImage;
        this.question = question;
        this.time = time;
        this.date = date;
        this.status = status;
    }

    public String getUsername() {
        return name;
    }

    public void setUsername(String username) {
        this.name = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
