package com.example.gremaster;

public class StoreData {

    private String name, expert, username, email;

    public StoreData(){

    }

    public StoreData(String name, String username, String email, String expert){
        this.name = name;
        this.expert = expert;
        this.username = username;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExpert() {
        return expert;
    }

    public void setExpert(String expert) {
        this.expert = expert;
    }
}
