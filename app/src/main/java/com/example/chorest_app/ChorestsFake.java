package com.example.chorest_app;

public class ChorestsFake {

    private String name;
    //private timestamp timestamp; FieldValue.serverTimestamp()

    //Empty constructor for firebase
    public ChorestsFake() {
    }

    public ChorestsFake(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
