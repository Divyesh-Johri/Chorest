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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class Chorest {

    public static final String TAG = "Chorest Object";

    // Properties for Chorest object
    private String id;
    private String name;
    private Timestamp timestamp;
    private double location_latitude;
    private double location_longitude;
    private String[] chores;
    private String[] route;
    private String[] route_addresses;
    private int route_distance;
    private boolean find_new_route;

    // Firestore database initialization
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Current User
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    // User ID
    String uID = user.getUid();
    // chorests collection path
    String currentPath = "users/" + uID + "/chorests/";



    // Constructor for retrieving existing Chorest
    public Chorest(String id) {
        this.id = id;
        findChorest();
    }

    // Constructor for initializing new Chorest
    // Requires name of chorest, latitude and longitude of user's specified location, and a list of chores
    public Chorest(String name, double loc_lat, double loc_long, String[] chores){
        this.name = name;
        this.location_latitude = loc_lat;
        this.location_longitude = loc_long;
        this.chores = chores;
        this.find_new_route = false;
        createChorest();
    }

    // Creates new chorest in Firestore
    private void createChorest() {
        // Map necessary data
        Map<String, Object> chorestDoc = new HashMap<>();
        chorestDoc.put("name", this.name);
        chorestDoc.put("timestamp",  FieldValue.serverTimestamp());
        chorestDoc.put("location_latitude", this.location_latitude);
        chorestDoc.put("location_longitude", this.location_longitude);
        chorestDoc.put("chores", this.chores);
        chorestDoc.put("find_new_route", this.find_new_route);

        db.collection(currentPath)
                .add(chorestDoc)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "createChorest - DocumentSnapshot written with ID: " + documentReference.getId());
                        // Set id of new document and set timestamp
                        id = documentReference.getId();
                        getCurrentTimestamp();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "createChorest - Error adding document", e);
                    }
                });
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
                        location_latitude = (Double) chorestDoc.get("location_latitude");
                        location_longitude = (Double) chorestDoc.get("location_longitude");
                        chores = (String[]) chorestDoc.get("chores");
                        route = (String[]) chorestDoc.get("route");
                        route_addresses = (String[]) chorestDoc.get("route_addresses");
                        route_distance = (Integer) chorestDoc.get("route_distance");
                        find_new_route = (Boolean) chorestDoc.get("find_new_route");
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
        // Timestamp updated in document BUT NOT IN OBJ (object would need to be recreated)
        FieldValue currentTimestamp = FieldValue.serverTimestamp();
        // Specify fields
        Map<String, Object> chorestDoc = new HashMap<>();
        chorestDoc.put("name", this.name);
        chorestDoc.put("timestamp", currentTimestamp);
        chorestDoc.put("location_latitude", this.location_latitude);
        chorestDoc.put("location_longitude", this.location_longitude);
        chorestDoc.put("chores", this.chores);
        chorestDoc.put("find_new_route", this.find_new_route);

        db.collection(currentPath).document(this.id)
                .update(chorestDoc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "updateChorest - DocumentSnapshot successfully written!");

                        // If new routes have been created
                        if(find_new_route){
                            getCurrentRoutes();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "updateChorest - Error setting document", e);
                    }
                });
    }

    private void getCurrentRoutes() {
        DocumentReference docRef = db.collection(currentPath).document(this.id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "getCurrentTimestamp - DocumentSnapshot data: " + document.getData());
                        // Retrieve and set route properties ONLY if properties are updated
                        Map<String, Object> chorestDoc = document.getData();
                        find_new_route = (Boolean) chorestDoc.get("find_new_route");

                        // If values haven't been updated, try again
                        if (find_new_route){
                            getCurrentRoutes();
                        }

                        route = (String[]) chorestDoc.get("route");
                        route_addresses = (String[]) chorestDoc.get("route_addresses");
                        route_distance = (Integer) chorestDoc.get("route_distance");

                    } else {
                        Log.d(TAG, "getCurrentTimestamp - No such document");
                    }
                } else {
                    Log.d(TAG, "getCurrentTimestamp - get failed with ", task.getException());
                }
            }
        });
    }

    // Fetches new timestamp created in Firestore when field is updated
    private void getCurrentTimestamp() {
        DocumentReference docRef = db.collection(currentPath).document(this.id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "getCurrentTimestamp - DocumentSnapshot data: " + document.getData());
                        // Retrieve and set timestamp property
                        Map<String, Object> chorestDoc = document.getData();
                        timestamp = (Timestamp) chorestDoc.get("timestamp");
                    } else {
                        Log.d(TAG, "getCurrentTimestamp - No such document");
                    }
                } else {
                    Log.d(TAG, "getCurrentTimestamp - get failed with ", task.getException());
                }
            }
        });
    }

    // Trigger to calculate new routes
    public void findNewRoute(){
        this.find_new_route = true;
        updateChorest();
    }

    // Returns true when route fields are updated
    // Meant for use after calling findNewRoute()
    public Boolean isRouteUpdated(){
        if (this.find_new_route){
            return false;
        }
        else{
            return true;
        }
    }


    // Getters and Setters
    // findNewRoute will always update all values.

    public void setName(String name) {
        this.name = name;
        updateChorest();
    }

    public void setLocation(double latitude, double longitude) {
        this.location_latitude = latitude;
        this.location_longitude = longitude;
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

    public double getLocLat() {
        return location_latitude;
    }

    public double getLocLong() {
        return location_longitude;
    }

    // Fetches up-to-date timestamp from Firestore itself
    public Timestamp getTimestamp() {
        getCurrentTimestamp();
        return timestamp;
    }

    public String[] getChores() {
        return chores;
    }

    public String[] getRoute() {
        return route;
    }

    public String[] getRoute_addresses() {
        return route_addresses;
    }

    public int getRoute_distance() {
        return route_distance;
    }
}
