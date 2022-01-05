package com.example.gremaster;

//model class for storing quiz marks and other user info
public class QuizMarks {

    String name, image;
    int marks;
    public QuizMarks(){

    }

    public QuizMarks(String name, String image, int marks) {
        this.name = name;
        this.image = image;
        this.marks = marks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getMarks() {
        return marks;
    }

    public void setMarks(int marks) {
        this.marks = marks;
    }
}
