package com.example.chorest_app;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;


public class Chorest {

    public static final String TAG = "Chorest Object";

    // Properties for Chorest object
    public String id;
    public String name;
    public Timestamp timestamp;
    public String location;
    public String[] chores;
    public String[] route;

    // Firestore database initialization
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Current User
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    // User ID
    String uID = user.getUid();
    String currentPath = "users/" + uID + "/chorests/";



    // Constructor
    public Chorest(String id) {
        this.id = id;
        findChorest();
    }

    // Find indicated chorest in Firestore and fill out properties
    private void findChorest() {
        DocumentReference docRef = db.collection(currentPath).document(this.id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "findChorest - DocumentSnapshot data: " + document.getData());
                        // Retrieve and set properties
                        Map<String, Object> chorestDoc = document.getData();
                        name = (String) chorestDoc.get("name");
                        timestamp = (Timestamp) chorestDoc.get("timestamp");
                        location = (String) chorestDoc.get("location");
                        chores = (String[]) chorestDoc.get("chores");
                        route = (String[]) chorestDoc.get("route");
                    } else {
                        Log.d(TAG, "findChorest - No such document");
                    }
                } else {
                    Log.d(TAG, "findChorest - get failed with ", task.getException());
                }
            }
        });
    }

    // Updates chorest document in Firestore with newly set values
    private void updateChorest() {
        Map<String, Object> chorestDoc = new HashMap<>();
        chorestDoc.put("name", this.name);
        chorestDoc.put("timestamp", this.timestamp);
        chorestDoc.put("location", this.location);
        chorestDoc.put("chores", this.chores);
        chorestDoc.put("route", this.route);

        db.collection(currentPath).document(this.id)
                .set(chorestDoc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "updateChorest - DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "updateChorest - Error setting document", e);
                    }
                });
    }

    // Getters and Setters

    public void setName(String name) {
        this.name = name;
        updateChorest();
    }

    public void setLocation(String location) {
        this.location = location;
        updateChorest();
    }

    public void setChores(String[] chores) {
        this.chores = chores;
        updateChorest();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getLocation() {
        return location;
    }

    public String[] getChores() {
        return chores;
    }

    public String[] getRoute() {
        return route;
    }
}
