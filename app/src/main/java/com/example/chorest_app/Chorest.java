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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class Chorest {

    public static final String TAG = "Chorest Object";

    // Static property of all of user's chorests
    public static ArrayList<Chorest> allUserChorests;

    // Properties for Chorest object
    private String id;
    private String name;
    private Timestamp timestamp;
    private double location_latitude;
    private double location_longitude;
    private ArrayList<String> chores;
    private ArrayList<String> route;
    private ArrayList<String> route_addresses;
    private int route_distance;
    private boolean find_new_route;

    // Firestore database initialization
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Current User
    private static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    // User ID
    private static String uID = user.getUid();
    // Chorests collection path
    private static String currentPath = "users/" + uID + "/chorests/";



    // Constructor for retrieving existing Chorest
    public Chorest(String id) {
        this.id = id;
        Log.i(TAG, "RETRIEVING EXISTING CHOREST");
        findChorest();
    }

    // Constructor for initializing new Chorest
    // Requires name of chorest, latitude and longitude of user's specified location, and a list of chores
    public Chorest(String name, double loc_lat, double loc_long, ArrayList<String> chores){
        this.name = name;
        this.location_latitude = loc_lat;
        this.location_longitude = loc_long;
        this.chores = chores;
        this.find_new_route = false;
        Log.i(TAG, "CREATING CHOREST");
        createChorest();
    }

    // Define information for existing Chorest
    // Used by getAllUserChorests
    public Chorest(String id, String name, Timestamp timestamp,
                   double location_latitude, double location_longitude,
                   ArrayList<String> chores, ArrayList<String> route, ArrayList<String> route_addresses,
                   int route_distance, boolean find_new_route) {
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
        this.location_latitude = location_latitude;
        this.location_longitude = location_longitude;
        this.chores = chores;
        this.route = route;
        this.route_addresses = route_addresses;
        this.route_distance = route_distance;
        this.find_new_route = find_new_route;
    }

    // Define information for chorests with no routes
    // Used by getAllUserChorests
    public Chorest(String id, String name, Timestamp timestamp,
                   double location_latitude, double location_longitude,
                   ArrayList<String> chores, boolean find_new_route) {
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
        this.location_latitude = location_latitude;
        this.location_longitude = location_longitude;
        this.chores = chores;
        this.find_new_route = find_new_route;
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

        Log.i(TAG, "createChorest -- INFO NOW BEING ADDED");

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
                        chores = (ArrayList<String>) chorestDoc.get("chores");
                        if(chorestDoc.get("route") != null){
                            route = (ArrayList<String>) chorestDoc.get("route");
                            route_addresses = (ArrayList<String>) chorestDoc.get("route_addresses");
                            route_distance = (Integer) chorestDoc.get("route_distance");
                        }
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

    // Will set new routes once they have finished updating
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
                        // THIS IS BAD, SHOULDN"T EXIST WILL MAKE APP CRASH!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        if (find_new_route){
                            getCurrentRoutes();
                        }

                        route = (ArrayList<String>) chorestDoc.get("route");
                        route_addresses = (ArrayList<String>) chorestDoc.get("route_addresses");
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



    // Callable Public methods

    // Public static method to receive all Chorest objects for a user
    // How to use:
    // Chorest.getAllUserChorests();
    // ArrayList<Chorests> var = Chorest.allUserChorests;
    public static void getAllUserChorests(){
        db.collection(currentPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "getAllUserChorests - " + document.getId() + " => " + document.getData());
                                // Skip iteration of document if Chorest is invalid
                                if (document.getId() == uID + "ROUTE" ){
                                    continue;
                                }
                                else{
                                    Map<String, Object> chorestDoc = document.getData();
                                    if(chorestDoc.get("route") != null){
                                        allUserChorests.add(new Chorest(
                                                document.getId(),
                                                (String) chorestDoc.get("name"),
                                                (Timestamp) chorestDoc.get("timestamp"),
                                                (Double) chorestDoc.get("location_latitude"),
                                                (Double) chorestDoc.get("location_longitude"),
                                                (ArrayList<String>) chorestDoc.get("chores"),
                                                (ArrayList<String>) chorestDoc.get("route"),
                                                (ArrayList<String>) chorestDoc.get("route_addresses"),
                                                (Integer) chorestDoc.get("route_distance"),
                                                (Boolean) chorestDoc.get("find_new_route") ));
                                    }
                                    else{
                                        allUserChorests.add(new Chorest(
                                                document.getId(),
                                                (String) chorestDoc.get("name"),
                                                (Timestamp) chorestDoc.get("timestamp"),
                                                (Double) chorestDoc.get("location_latitude"),
                                                (Double) chorestDoc.get("location_longitude"),
                                                (ArrayList<String>) chorestDoc.get("chores"),
                                                (Boolean) chorestDoc.get("find_new_route") ));
                                    }

                                }
                            }
                        } else {
                            Log.d(TAG, "getAllUserChorests - Error getting documents: ", task.getException());
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
    // THIS IS BAD, SHOULDN"T EXIST WILL MAKE APP CRASH!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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

    public void setChores(ArrayList<String> chores) {
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

    public ArrayList<String> getChores() {
        return chores;
    }

    public ArrayList<String> getRoute() {
        return route;
    }

    public ArrayList<String> getRoute_addresses() {
        return route_addresses;
    }

    public int getRoute_distance() {
        return route_distance;
    }
}
