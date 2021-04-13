# Chorest

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Chorest is an app that allows users to find the shortest route between locations based on inputted tasks and the starting location of the user. Users can save created "chorests" for future reference and view chosen routes on Google maps. 

### App Evaluation
- **Category:** Productivity/Travel
- **Mobile:** Useful to access on the go due to saved chorest routes. The app uses Google Maps and user location.
- **Story:** Assits people who need to get many tasks done in a short period of time, especially in an unknown area (like a city).
- **Market:** Anyone old enough to do chores or activities outside can utilize the app. The ability to put in any number of chores or activities makes the app versatile for any use case.
- **Habit:** Chorests (and their routes) can be saved for future reference, as well as recalculated. This prompts the user to reuse the app depending on change of location or chores/activities.
- **Scope:** The app will allow users to create chorests, specify a starting location and populate chorests with chores/activities, as well as prioritize them. The calculated route will be the shortest, though the user can change the route with different locations if desired. The user will also be able to view chorest routes on a Google Maps window.  

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* [x] User can login to app and persist (Login page)
* [x] User can view their chorests (Home page)
* [x] User can view profile and logout (Profile page)
* [x] User can view maps (Maps page)
* [x] User can add a chorest (Home + Chorest Page)
* User can edit and delete a chorest (Home + Chorest Page)
* User can navigate to maps page with selected route (Chorest page)
* User can view new unoptimal routes and go back to old ones (Add chorest page)
* User can view a calculated shortest route of a chorest (Add chorest page)
* User can view map with chorests (Map page)
* User can see toasts for screen transitions/ items saved etc (General)

**Optional Nice-to-have Stories**

* User can favorite chorests for use offline
* User can obtain routes that account for traffic
* User can utilize speech-to-text for chores
* User can share chorests (through text or email) 

<img src = "https://github.com/Divyesh-Johri/Chorest/blob/main/Chorest3.gif" width=250><br>
Gif Link: https://github.com/Divyesh-Johri/Chorest/blob/main/Chorest3.gif


<img src = "https://github.com/Divyesh-Johri/Chorest/blob/main/Chorest4.gif" width=250><br>
Gif Link: https://github.com/Divyesh-Johri/Chorest/blob/main/Chorest4.gif

### 2. Screen Archetypes

* Login
   * User can login to app and persist
* Home
   * User can view their chorests
   * User can add and edit and delete a chorest
   * User can see toasts for screen transitions/ items saved etc
* Profile
    * User can view profile and logout
* Map
    * User can view map with chorests
* Chorest
    * User can add and edit a chorest
    * User can navigate to maps page with selected route
    * User can view a calculated shortest route of a chorest

### 3. Navigation

**Tab Navigation** (Bottom Navbar)

* Profile
* Home
* Maps

**Flow Navigation** (Screen to Screen)

* Login
   * Home
* Home
   * Profile
   * Maps
   * Chorest
* Profile
    * Home
    * Maps
    * Login
* Maps
    * Home
    * Profile
* Chorest
    * Home
    * Maps

## Wireframes
<img src="https://raw.githubusercontent.com/Divyesh-Johri/Chorest/main/wireframe.png" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
### Models

| Property      | Type    | Description                                                             |
| ------------- | ------- | ----------------------------------------------------------------------- |
| userID        | String  | id indicating current user                                              |
| chorest       | Chorest | Chorest class calculates routes depending on inputted chores/activities |
| chorest.start | String  | Chooses starting location                                               |
| chorest.title | String  | Displays the label of the chorest                                       |
| chorest.route | Array   | Returns calculated shortest route as locations from Google Maps         |
| chorest_list  | Array   | The array displays a list of chorest elements                           |



### Networking
- Login
    - "GET" request to call Firebase Authentication for user login or signup
    ```java
    // Choose authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());

    // Create and launch sign-in intent
    startActivityForResult(
            AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
            RC_SIGN_IN);
    ```

- Home
    - "GET" request to Firebase to retrieve user's saved chorests
    ```java
    db.collection("users")
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            }
        });
    ```
    - "DELETE" to delete existing chorest
    ```java
    db.collection("userID").document("chorest")
        .delete()
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully deleted!");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting document", e);
            }
        });
    ```

- Chorest
    - "POST" request to insert new chorest
    ```java
    db.collection("users").add({
    first: "Ada",
    last: "Lovelace",
    born: 1815
    })
    .then((docRef) => {
        console.log("Document written with ID: ", docRef.id);
    })
    .catch((error) => {
        console.error("Error adding document: ", error);
    });
    ```
    - "UPDATE" to edit existing chorest
    - "DELETE" to delete existing chorest
    - "GET" request to display saved chorest details    

- Map
    - "GET" request to Google SDK/API to show the maps
    - "GET" request to receive user's saved chorests

- Profile
    - "GET" request to signout by calling Firebase Authentication`        
    ```java
    AuthUI.getInstance()
           .signOut(this)
           .addOnCompleteListener(new OnCompleteListener<Void>(){
           public void onComplete(@NonNull Task<Void> task) {
                // ...
            }
        });
    ```
- [OPTIONAL: List endpoints if using existing API such as Yelp]
    - "GET" Firebase SDK
    - "GET" Google Maps API 
    - "GET" Maps SDK - displays the map in the app.
    - "GET" Places API - query for locations to access route for chorests. 
