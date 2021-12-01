package com.example.gremaster;

public class StoreData {

    private String name, expert;

    public StoreData(String name, String expert){
        this.name = name;
        this.expert = expert;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpert() {
        return expert;
    }

    public void setExpert(String expert) {
        this.expert = expert;
    }
}
