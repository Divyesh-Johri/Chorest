package com.example.chorest_app;

import android.util.Log;

import androidx.annotation.NonNull;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
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

import okhttp3.Headers;


public class Chorest {

    public static final String TAG = "Chorest Object";

    // Properties for Chorest object
    public String id;
    public String name;
    public Timestamp timestamp;
    public double location_latitude;
    public double location_longitude;
    public String[] chores;
    public String[] route;

    // Firestore database initialization
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Current User
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    // Async HTTP Client
    AsyncHttpClient httpClient = new AsyncHttpClient();
    // User ID
    String uID = user.getUid();
    // chorests collection path
    String currentPath = "users/" + uID + "/chorests/";



    // Constructor
    public Chorest(String id) {
        this.id = id;
        findChorest();
    }

    // Constructor for creating new Chorest

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

    // Search for places near specified location
    private ArrayList<String[]> retrievePlaces() {
        // List where each element is a list of addresses for each chore
        ArrayList<String[]> places = new ArrayList<>();

        // Find addresses of locations for each chore
        String httpAddress = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
        for (String chore : this.chores){
            // Set parameters for chore
            RequestParams params = new RequestParams();
            params.put("query", chore);
            params.put("location", "" + this.location_latitude + "," + this.location_longitude);
            params.put("fields", "formatted_address");
            params.put("radius", "100");
            params.put("key", R.string.places_api_key);

            httpClient.get(httpAddress, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Headers headers, JSON json) {
                    Log.d(TAG, "retrievePlaces - Addresses received: " + json.jsonObject.toString());
                    // Add json address in places as a String[] of name, address, place_id
                    try {
                        JSONObject results = json.jsonObject.getJSONArray("results").getJSONObject(0);
                        String[] address = {results.getString("name"), results.getString("formatted_address"), results.getString("place_id")};
                        places.add(address);
                    } catch (JSONException e) {
                        Log.e(TAG, "retrievePlaces - JSON Parsing error: ", e);
                    }
                }

                @Override
                public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                    Log.e(TAG, "retrievePlaces - Failed to retrieve addresses: ", throwable);
                }
            });
        }

        if(places.isEmpty()){
            return null;
        }
        return places;
    }

    // Find shortest distance between 2 locations in meters
    private Integer findDistance(String place_id_origin, String place_id_dest){
        // Return value
        ArrayList<Integer> returnDistance = new ArrayList<>();
        // Set up HTTP call
        String httpAddress = "https://maps.googleapis.com/maps/api/distancematrix/json?";
        RequestParams params = new RequestParams();
        params.put("origins", "place_id:" + place_id_origin);
        params.put("destinations", "place_id:" + place_id_dest);
        params.put("key", R.string.places_api_key);

        httpClient.get(httpAddress, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "findDistance - Distance received: " + json.jsonObject.toString());
                try {
                    String distanceString = json.jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getString("text");

                    // Format distance string and return an integer in integer meters
                    char[] chars = distanceString.toCharArray();
                    char[] numberChars = new char[chars.length];
                    for(int n = 0; n < chars.length; n++){
                        if(Character.isDigit(chars[n])){
                            numberChars[n] = chars[n];
                        }
                    }
                    int distance = Integer.parseInt(String.valueOf(numberChars));
                    // Check if it's in kilometers
                    if(distanceString.contains("km")){
                        distance *= 1000;
                    }

                    returnDistance.add(distance);

                } catch (JSONException e) {
                    Log.e(TAG, "findDistance - JSON Parsing error: ", e);
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.e(TAG, "findDistance - Failed to retrieve distances: ", throwable);
            }
        });

        if(returnDistance.isEmpty()){
            return null;
        }
        return returnDistance.get(0);
    }

    // Create graph with addresses
    private void createGraph(ArrayList<String[]> places){
        // Dictionary java
        for(String[] location1 : places){
            Map<String[], Integer> content = new HashMap<>();
            for(String[] location2 : places){
                if(location1 != location2){
                    content.put(location2, findDistance(location1[2], location2[2]));
                }
            }
        }
    }

    // Primm's algorithm


    // Calculate shortest route action method


    // Getters and Setters

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

    public double getLocation_latitude() {
        return location_latitude;
    }

    public double getLocation_longitude() {
        return location_longitude;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String[] getChores() {
        return chores;
    }

    public String[] getRoute() {
        return route;
    }
}
