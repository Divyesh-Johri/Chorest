    package com.example.chorest_app;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

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
    private ArrayList<String> chores;
    private ArrayList<String> route;
    private ArrayList<String> route_addresses;
    private long route_distance;
    private boolean find_new_route;

    // Firestore database initialization
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Firebase Functions reference
    private FirebaseFunctions functions = FirebaseFunctions.getInstance();
    // Current User
    private static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    // User ID
    private static String uID = user.getUid();
    // Chorests collection path
    private static String currentPath = "users/" + uID + "/chorests/";

    // Empty constructor for Firebase
    private Chorest(){}

    // Define information for existing Chorest
    // Used by getAllUserChorests
    public Chorest(String id, String name, Timestamp timestamp,
                   double location_latitude, double location_longitude,
                   ArrayList<String> chores, ArrayList<String> route, ArrayList<String> route_addresses,
                   long route_distance) {
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
        this.location_latitude = location_latitude;
        this.location_longitude = location_longitude;
        this.chores = chores;
        this.route = route;
        this.route_addresses = route_addresses;
        this.route_distance = route_distance;
    }

    // Define information for chorests with no routes
    // Used by getAllUserChorests
    public Chorest(String id, String name, Timestamp timestamp,
                   double location_latitude, double location_longitude,
                   ArrayList<String> chores) {
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
        this.location_latitude = location_latitude;
        this.location_longitude = location_longitude;
        this.chores = chores;
    }


    // ----------------------------------------------------------------------------------------------
    // STATIC METHODS
    // ----------------------------------------------------------------------------------------------


    // Callback for use in the createChorest function
    public interface CreateChorestCallback{
        void onCallback(Chorest newChorest);
    }

    // Static method that creates new chorest in Firestore
    /** Ex of use:
     * Chorest.createChorest("chorest2", 40.798214, -77.859909, chores,
     *                 new Chorest.CreateChorestCallback() {
     *                     @Override
     *                     public void onCallback(Chorest newChorest) {
     *                         Log.i(TAG, "New Chorest created: " + newChorest.getName() + " " + newChorest.getId());
     *                     }
     *                 });
     */
    public static void createChorest(String name, double loc_lat, double loc_long, ArrayList<String> chores,
                               CreateChorestCallback createChorestCallback) {

        // Map necessary data
        Map<String, Object> chorestDoc = new HashMap<>();
        chorestDoc.put("name", name);
        chorestDoc.put("timestamp",  FieldValue.serverTimestamp());
        chorestDoc.put("location_latitude", loc_lat);
        chorestDoc.put("location_longitude", loc_long);
        chorestDoc.put("chores", chores);

        Log.i(TAG, "createChorest -- INFO NOW BEING ADDED");

        db.collection(currentPath)
                .add(chorestDoc)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "createChorest - DocumentSnapshot written with ID: " + documentReference.getId());

                        // Get id of new document
                        String id = documentReference.getId();

                        // Get details of document to find Timestamp
                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d(TAG, "createChorest - DocumentSnapshot data: " + document.getData());

                                        // Get timestamp
                                        Map<String, Object> data = document.getData();
                                        Timestamp timestamp = (Timestamp) data.get("timestamp");

                                        // Set listener
                                        createChorestCallback.onCallback(new Chorest(id, name, timestamp, loc_lat, loc_long, chores));

                                    } else {
                                        Log.d(TAG, "createChorest - 'get' document doesn't exist");
                                    }
                                } else {
                                    Log.d(TAG, "createChorest - 'get' failed with ", task.getException());
                                }
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "createChorest - Error adding document", e);
                    }
                });
    }


    // Callback listener for use in getAllUserChorests
    public interface UserChorestsCallback{
        void onCallback(ArrayList<Chorest> chorests);
    }

    // Public static method to receive all Chorest objects for a user
    // Updates callback listener with chorest objects to allow asynchronized collection
    /** Ex of use:
     *  Chorest.getAllUserChorests(new Chorest.UserChorestsCallback() {
     *             @Override
     *             public void onCallback(ArrayList<Chorest> chorests) {
     *                 Log.i(TAG, "Collected chorests size: " + chorests.size());
     *                 // Do what you need with list of chorests
     *             }
     *         });
     */
    public static void getAllUserChorests(UserChorestsCallback userChorestsCallback){
        db.collection(currentPath)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "getAllUserChorests - Query successful");
                            ArrayList<Chorest> allUserChorests = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "getAllUserChorests - " + document.getId() + " => " + document.getData());
                                // Skip iteration of document if Chorest is invalid
                                if ( document.getId().equals(uID + "ROUTE") ){
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
                                                (Long) chorestDoc.get("route_distance") ));
                                    }
                                    else{
                                        allUserChorests.add(new Chorest(
                                                document.getId(),
                                                (String) chorestDoc.get("name"),
                                                (Timestamp) chorestDoc.get("timestamp"),
                                                (Double) chorestDoc.get("location_latitude"),
                                                (Double) chorestDoc.get("location_longitude"),
                                                (ArrayList<String>) chorestDoc.get("chores") ));
                                    }
                                }
                            }
                            // After for loop, send data to callback
                            Log.i(TAG, "getAllUserChorests - Size of returned array: " + allUserChorests.size());
                            userChorestsCallback.onCallback(allUserChorests);
                        } else {
                            Log.d(TAG, "getAllUserChorests - Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    // ----------------------------------------------------------------------------------------------
    // OBJECT METHODS
    // ----------------------------------------------------------------------------------------------


    // Callback for use in the updateChorest function
    public interface UpdateChorestCallback{
        void onCallback(Boolean hasUpdated);
    }

    // Updates chorest document in Firestore with newly set values

    /** Ex of use:
     * newChorest.updateChorest(new Chorest.UpdateChorestCallback() {
     *          @Override
     *          public void onCallback(Boolean hasUpdated) {
     *              Log.i(TAG, "Chorest properties updated");
     *              // Do what you need to with object
     *          }
     * });
     */
    public void updateChorest(UpdateChorestCallback updateChorestCallback) {
        // Timestamp updated in document BUT NOT IN OBJ (object would need to be recreated)
        FieldValue currentTimestamp = FieldValue.serverTimestamp();
        // Specify fields
        Map<String, Object> chorestDoc = new HashMap<>();
        chorestDoc.put("name", this.name);
        chorestDoc.put("timestamp", currentTimestamp);
        chorestDoc.put("location_latitude", this.location_latitude);
        chorestDoc.put("location_longitude", this.location_longitude);
        chorestDoc.put("chores", this.chores);

        db.collection(currentPath).document(this.id)
                .update(chorestDoc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "updateChorest - DocumentSnapshot successfully written!");
                        updateChorestCallback.onCallback(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "updateChorest - Error setting document", e);
                    }
                });
    }

    // Callback for use in the getCurrentTimestamp function
    public interface TimestampCallback{
        void onCallback(Timestamp timestamp);
    }

    // Fetches new timestamp created in Firestore when field is updated
    /** Ex of use:
     *  chorestObj.getCurrentTimestamp(new TimestampCallback() {
     *             @Override
     *             public void onCallback(Timestamp timestamp) {
     *                  Log.i(TAG, "Current timestamp: " + timestamp.toString());
     *                  // Do what you need with timestamp
     *             }
     *         });
     *
     */
    public void getCurrentTimestamp(TimestampCallback timestampCallback) {
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
                        // Set callback
                        timestampCallback.onCallback(timestamp);
                    } else {
                        Log.d(TAG, "getCurrentTimestamp - No such document");
                    }
                } else {
                    Log.d(TAG, "getCurrentTimestamp - get failed with ", task.getException());
                }
            }
        });
    }


    // Method to call the Cloud Function, to be used by findNewRoute()
    private Task<String> callUpdateRoute(){
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("chorestID", this.id);
        return functions.getHttpsCallable("updateRoute")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
                        Log.d(TAG, "callUpdateRoute - Obtained results: " + result.toString());

                        route = (ArrayList<String>) result.get("route");
                        route_addresses = (ArrayList<String>) result.get("route_addresses");

                        return "result";
                    }
                });
    }

    // Callback listener for use in getAllUserChorests
    public interface FindRouteCallback{
        void onCallback(Boolean hasUpdated);
    }

    // Will trigger new routes to be calculated and set them one they have finished updating
    /** Ex of use:
     *   chorestObj.findNewRoute(new FindRouteCallback() {
     *             @Override
     *             public void onCallback(Boolean hasUpdated) {
     *                  Log.i(TAG, "Route: " + chorestObj.getRoute().toString());
     *                  Log.i(TAG, "Route: " + chorestObj.getRoute_addresses().toString());
     *                  Log.i(TAG, "Route: " + chorestObj.getRoute_distance().toString());
     *                  // Do what you need to with routes
     *             }
     *         });
     */
    public void findNewRoute(FindRouteCallback findRouteCallback) {
        callUpdateRoute()
            .addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        // Error with function results
                        Exception e = task.getException();
                        Log.e(TAG, "findNewRoute - Unable to retrieve new route: ", e);
                    }
                    // Set callback
                    Log.i(TAG, "findNewRoute - New route info set");
                    findRouteCallback.onCallback(true);
                }
            });
    }



    // ----------------------------------------------------------------------------------------------
    // SETTER METHODS
    // ----------------------------------------------------------------------------------------------
    /** REMEMBER TO CALL updateChorest() to reflect changes of setters in database */


    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(double latitude, double longitude) {
        this.location_latitude = latitude;
        this.location_longitude = longitude;
    }

    public void setChores(ArrayList<String> chores) {
        this.chores = chores;
    }


    // ----------------------------------------------------------------------------------------------
    // GETTER METHODS
    // ----------------------------------------------------------------------------------------------


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

    public ArrayList<String> getChores() {
        return chores;
    }

    public ArrayList<String> getRoute() {
        return route;
    }

    public ArrayList<String> getRoute_addresses() {
        return route_addresses;
    }
}
