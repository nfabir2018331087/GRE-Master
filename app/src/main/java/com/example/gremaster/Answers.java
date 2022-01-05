package com.example.gremaster;


//Model class for storing answers in forum
public class Answers {

    private String name, profileImage, answer, time, date, key, status;
    public Answers(){

    }
    public Answers(String name, String profileImage, String answer, String time, String date, String status) {
        this.name = name;
        this.profileImage = profileImage;
        this.answer = answer;
        this.time = time;
        this.date = date;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String username) {
        this.name = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String question) {
        this.answer = question;
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
