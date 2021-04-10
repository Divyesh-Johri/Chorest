// __________________________________________________________________________________
// Initialization 

// // The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
// const functions = require('firebase-functions');

// // The Firebase Admin SDK to access Firestore.
// const PROJECT_ID = "chorest-630f6";
// const admin = require('firebase-admin');
// admin.initializeApp({
//   projectId: PROJECT_ID,
//   credential: admin.credential.applicationDefault()
// });

// node-fetch for https calls
const fetch = require("axios");

// API key
const key = ""


// __________________________________________________________________________________
// Route Calculation Functions 

/**
 * Will return distance between two locations in meters based on their address using the Distance Matrix API
 * Inputs: loc1_id, loc2_id (Strings)
 * Ouput: distance (int)
 **/
function findDistance(loc1, loc2){
    const URL = `https://maps.googleapis.com/maps/api/distancematrix/json?origins=${loc1}&destinations=${loc2}&key=${key}`
    fetch(URL)
    .then(response => {
        response.json()
        })
    .then(data => {
        console.log(data)
    })
}

/**
 * Will return a list of addresses for each chore using a text-search request to the Places API
 * Inputs: chores (list of strings), loc_lat (Number), loc_long (Number) 
 * Ouput: addressList (list)
 **/
async function retrievePlaces(chores, loc_lat, loc_long){
    addressList = []
    for (const chore of chores){
        const URL = `https://maps.googleapis.com/maps/api/place/textsearch/json?query=${chore}&location=${loc_lat},${loc_long}&fields=formatted_address&radius=100&key=${key}`;
        let info = {}
        const response = await fetch.get(URL)
        addressList.push([ response.data["results"][0]["name"], response.data["results"][0]["formatted_address"] ])
    }
    return addressList
}

const choreList = ["laundry", "coffee", "supermarket", "mechanic"]
const lat = 40.798214
const long = -77.859909
retrievePlaces(choreList, lat, long)
.then( response => {
    console.log(response)
})



// ___________________________________________________________________________________
// Cloud function

// Listen for updates to any `user` document.
// exports.updateRoute = functions.firestore
//     .document('users/{userID}/chorests/{chorestID}')
//     .onUpdate((change, context) => {
//       // Retrieve the current and previous value
//       const data = change.after.data();
//       const previousData = change.before.data();

//       // We'll only update if the name has changed.
//       // This is crucial to prevent infinite loops.
//       if (data.chores == previousData.chores) {
//         return null;
//       }

      

//       // Then return a promise of a set operation to update the count
//       return change.after.ref.set({
//         name_change_count: count + 1
//       }, {merge: true});
//     });