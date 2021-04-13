package com.example.chorest_app;

public class HomeModel {

    private String name;

    // Empty constructor for Firebase
    private HomeModel(){}

    // Constructor to recieve data
    private HomeModel(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
