// __________________________________________________________________________________
// Initialization 

// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Firestore.
const PROJECT_ID = "chorest-630f6";
const admin = require('firebase-admin');
admin.initializeApp({
  projectId: PROJECT_ID,
  credential: admin.credential.applicationDefault()
});

// node-fetch for https calls
const fetch = require("axios");

// API Key fetched from .env
require('dotenv').config();
const key = process.env.API_KEY


// __________________________________________________________________________________
// Route Calculation Functions 

/**
 * Will return distance between two locations in meters based on their address using the Distance Matrix API
 * Inputs: loc1_id, loc2_id (Strings)
 * Ouput: distance (int)
 **/
async function findDistance(loc1, loc2){

    // Helper function for findDistance
    // Takes a string an returns true if string is a number
    function isNumeric(str) {
        if (typeof str != "string") return false // we only process strings!  
        return !isNaN(str) && // use type coercion to parse the _entirety_ of the string (`parseFloat` alone does not do this)...
            !isNaN(parseFloat(str)) // ...and ensure strings of whitespace fail
    }

    // Call to Distance Matrix
    const URL = `https://maps.googleapis.com/maps/api/distancematrix/json?origins=${loc1}&destinations=${loc2}&key=${key}`
    response = await fetch.get(URL)
    distanceRaw = response.data["rows"][0]["elements"][0]["distance"]["text"]
    distance = ""
    // Parse raw string for numbers
    for( let i = 0; i < distanceRaw.length; i++ ) {
        if ( isNumeric(distanceRaw[i]) ){
            distance += distanceRaw[i]
        }        
    }
    // Check if in kilometers
    if( distanceRaw.includes('km') ){
        distance *= 1000
    }
    
    return Number(distance)
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

/**
 * Will restructure addressList to be a graph
 * Inputs: addressList (list) 
 * Ouputs: list of start (String of first key) and graph (Map)
 **/
async function createGraph(addressList){
    var start
    graph = new Map()
    
    for( const loc1 of addressList ){
        content = []
        for( const loc2 of addressList ){
            if( loc1 != loc2 ){
                content.push( [loc2[0], await findDistance(loc1[1], loc2[1])] )
            }
        }
        if( graph.size == 0 ){
            start = loc1[0]
        }
        graph.set(loc1[0], content)
    }

    return [start, graph]
}

/**
 * Takes a graph and returns its Min Spanning Tree using Prim's Algorithm
 * Inputs: start (String key for graph), graph (Map) 
 * Ouputs: list of tree (list) and totalDist (Number)
 **/
function prim(start, graph){
    tree = [start]
    totalDist = 0

    console.log('TOTAL DIST: ' + totalDist)

    while( graph.size != tree.length ){
        curr = []
        for ( const node of tree ){
            for ( const connector of graph.get(node) ){
                if ( !tree.includes(connector[0]) ){
                    curr.push(connector)
                }
            }
        }
        element = curr.sort(a => {a[1]})[0]
        tree.push(element[0])
        totalDist += element[1]

        console.log('TOTAL DIST: ' + totalDist)
    }

    return [tree, totalDist]
}

// Testing values
// const choreList = ["laundry", "coffee", "grocery", "pizza"]
// const lat = 40.798214
// const long = -77.859909


// ___________________________________________________________________________________
// Cloud function

// Will return calculated routes and update document with them
// data: {chorestID}
exports.updateRoute = functions.https.onCall(async (data, context) => {
    const uID = context.auth.uid;
    const chorestID = data.chorestID;
    var data = {}

    document = await admin.firestore().collection(`users/${uID}/chorests/`).doc(chorestID).get()
    if(document.exists){
        var data = document.data()
    }
    else{
        console.log("ERROR: DOCUMENT DOESN'T EXIST!")
        throw new functions.https.HttpsError("invalid-argument", "The indicated chorest doesn't exist")
    }

    // Error checking
    if(data.chores == null || data.location_latitude == null || data.location_longitude == null){
        console.log("ERROR: DOCUMENT MISSING PROPERTIES")
        throw new functions.https.HttpsError("failed-precondition", "The indicated chorest is missing vital properties")
    }

    // Run methods to calculate shortest route, then update document
    // Get Addresses of locations to perform chores
    addressList = await retrievePlaces(data.chores, data.location_latitude, data.location_longitude)    
    console.log('Address List: ' + addressList)
    
    // Receive graph to perform Primm's on
    graphResponse = await createGraph(addressList)
    
    start = graphResponse[0]
    graph = graphResponse[1]

    console.log('Graph Start: ' + start)
    console.log('Graph: ' + graph.toString())
    
    // Perform Primm's to find MST
    var route, totalDist
    [route, totalDist] = prim(start, graph)

    console.log('MST: ' + route)
    console.log('Total Distance: ' + totalDist)

    // Create list of addresses of route
    var routeAddr = []
    for( const loc of route ){
        routeAddr.push( addressList.find(place => place[0] == loc)[1] )
    }
    console.log('RouteAddr: ' + routeAddr)

    dataOutput = {
        route: route,       // Route with place names
        route_addresses: routeAddr,   // Route with addresses
        route_distance: totalDist    // Total route distance
    }

    // Update values in document
    updated = await admin.firestore().collection(`users/${uID}/chorests/`).doc(chorestID).update(dataOutput)

    // Then return a promise of a set operation to update the count
    return dataOutput
        
});

   