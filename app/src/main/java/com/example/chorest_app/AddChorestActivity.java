package com.example.chorest_app;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chorest_app.Fragments.HomeFragment;
import com.example.chorest_app.Fragments.MapFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class AddChorestActivity extends AppCompatActivity {

    private static final String TAG = "AddChorestActivity";
    private RadioGroup rgLocation;
    private RadioButton rbCurrentLocation;
    private RadioButton rbChooseLocation;
    private Button btCalculateMap;
    private EditText etChorestName;
    private RecyclerView rvCalculatedRoutes;
    private Button btTypeAddress;
    private Button btSubmitList;
    private EditText etAddLocations;
    private RecyclerView rvAddLocations;
    private EditText etTypeAddress;
    private static final int RC_TO_MAP = 23;
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private Button btAddLocations;

    private Chorest chorest;
    private static final String KEY_CHOREST_NAME = "name";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AddChorestAdapter routesAdapter;
    private AddChorestChoresListAdapter choresAdapter;

    // Retrieve current location of user
    private FusedLocationProviderClient fusedLocationClient;
    private int locationRequestCode = 1000;
    private double longitude;
    private double latitude;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chorest);


        mAuth = FirebaseAuth.getInstance();
        rgLocation = findViewById(R.id.rgLocation);
        rbCurrentLocation = findViewById(R.id.rbCurrentLocation);
        rbChooseLocation = findViewById(R.id.rbChooseLocation);
        btCalculateMap = findViewById(R.id.btCalculateMap);
        etChorestName = findViewById((R.id.etChorestName));
        rvCalculatedRoutes = findViewById(R.id.rvCalculatedRoutes);
        btTypeAddress = findViewById(R.id.btTypeAddress);
        btSubmitList = findViewById(R.id.btSubmitList);
        rvAddLocations = findViewById(R.id.rvAddLocations);
        etTypeAddress = findViewById(R.id.etTypeAddress);
        btAddLocations = findViewById(R.id.btAddLocations);
        etAddLocations = findViewById(R.id.etAddLocations);


        ArrayList<String> choreslist;
        ArrayList<String> routeslist;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up chores list recycler view (rvAddLocations)
        choreslist = new ArrayList<>();
        choreslist.add("Groceries");
        choreslist.add("Gym");
        choreslist.add("pizza");

        // Delete a chore
        AddChorestChoresListAdapter.OnLongClickListener onLongClickListener = new AddChorestChoresListAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int positon) {

                choreslist.remove(positon);
                choresAdapter.notifyItemRemoved(positon);

                Toast.makeText(AddChorestActivity.this, "Chore Removed", Toast.LENGTH_SHORT).show();

            }
        };

        choresAdapter = new AddChorestChoresListAdapter(choreslist, onLongClickListener);
        rvAddLocations.setAdapter(choresAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvAddLocations.setLayoutManager(linearLayoutManager);


        // Set up routes list recycler view (rvCalculatedRoutes)
        routeslist = new ArrayList<>();
        routeslist.add("Snacks");
        routeslist.add("Music");
        routeslist.add("Socks");

        AddChorestRouteListAdapter routesAdapter = new AddChorestRouteListAdapter(routeslist);
        LinearLayoutManager linLayManager = new LinearLayoutManager(this);
        rvCalculatedRoutes.setAdapter(routesAdapter);
        rvCalculatedRoutes.setLayoutManager(linLayManager);






        // Initialize the SDK for places api
        Places.initialize(getApplicationContext(), getResources().getString(R.string.places_key));


        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);



        // Request location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.i(TAG, "Launching permission requester");

            // Launch requester
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, locationRequestCode);

        } else {
            // already permission granted
            Log.i(TAG, "Permission ALREADY granted");
            getLastLocation();
        }


        // Save all changes and return user to home fragment
        // Save the chorest info to firebase
        btCalculateMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = mAuth.getCurrentUser();

                String name = etChorestName.getText().toString();

                Map<String, Object> chorestRoute = new HashMap<>();
                chorestRoute.put(KEY_CHOREST_NAME, name);

                db.collection("users").document(currentUser.getUid()).collection("chorests")
                        .add(chorestRoute)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "onSuccess: Added new chorests");
                                Toast.makeText(AddChorestActivity.this, "Chorests Route Saved", Toast.LENGTH_SHORT).show();
                                goToMain();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Failed to add chorests");
                                Toast.makeText(AddChorestActivity.this, "Chorests Route Failed to Save", Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });

        // Add address' latitude and longitude
        btTypeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            // Add latitude and longitude here?

            }
        });

        // Add each chore to an arraylist of strings
        btAddLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chore = etAddLocations.getText().toString();

                choreslist.add(chore);
                choresAdapter.notifyItemInserted(choreslist.size()-1);
                etAddLocations.setText("");
            }
        });

        // Force users to name their chorests by disabling the submit button
        etChorestName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String chorestNameInput = etChorestName.getText().toString().trim();

                btSubmitList.setEnabled(!chorestNameInput.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Save info to Chorest Object and show the calculated route
        btSubmitList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etChorestName.getText().toString();

                // Create new chorest object from user input
                Chorest.createChorest(name, latitude, longitude, choreslist, new Chorest.CreateChorestCallback() {
                    @Override
                    public void onCallback(Chorest newChorest) {

                        Log.i(TAG, "New Chorest created: " + newChorest.getName() + newChorest.getLocLat()+ newChorest.getLocLong() + newChorest.getId() + newChorest.getRoute());

                        newChorest.findNewRoute(new Chorest.FindRouteCallback() {
                            @Override
                            public void onCallback(Boolean hasUpdated) {
                                ArrayList<String> routes = newChorest.getRoute();

                                try{
                                    for( int i = 0; i < routes.size(); i++){
                                        // Put array in 2nd recyclerview  (rvCalculatedRoutes)
                                        routeslist.add(routes.get(i));
                                    }
                                }
                                catch ( java.lang.NullPointerException e){
                                    Log.w(TAG, "Failed to produce routes", e);
                                    Toast.makeText(AddChorestActivity.this, "Failed to get routes", Toast.LENGTH_SHORT).show();
                                    return;
                                }


                            }
                        });
                        /*// Get calculated route as an arraylist
                        ArrayList<String> routes = newChorest.getRoute();

                        try{
                            for( int i = 0; i < routes.size(); i++){
                                // Put array in 2nd recyclerview  (rvCalculatedRoutes)
                                routeslist.add(routes.get(i));
                            }
                        }
                        catch ( java.lang.NullPointerException e){
                            Log.w(TAG, "Failed to produce routes", e);
                            Toast.makeText(AddChorestActivity.this, "Failed to get routes", Toast.LENGTH_SHORT).show();
                            return;
                        }*/

                    }

                });

                //Chorest cho = new Chorest.createChorest();

                // Get calculated route as an array

                // Put array in 2nd recyclerview

                // Make sure array is cleared when going back to add activity

            }
        });


        // Autocomplete feature for your address
        // The edit text is non-focusable
        etTypeAddress.setFocusable(false);
        etTypeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Initialize place field list
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ID, Place.Field.NAME);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .build(getBaseContext()); // get context
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });



        // Set the recyclerview for rvCalculatedRoutes
        //setRouteRecyclerView();
        

    }



    /*private void setRouteRecyclerView() {
        Query query = db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("chorests"); // can order by putting '.orderBy("field", Query.Direction.ASCENDING/DESCENDING);'

        //RecyclerOptions
        FirestoreRecyclerOptions<Chorest> options = new FirestoreRecyclerOptions.Builder<Chorest>()
                .setQuery(query, Chorest.class)
                .build();

        adapter = new AddChorestAdapter(options);

        RecyclerView rvCalculatedRoutes = findViewById(R.id.rvCalculatedRoutes);
        rvCalculatedRoutes.setHasFixedSize(true);
        rvCalculatedRoutes.setLayoutManager(new LinearLayoutManager(this));
        rvCalculatedRoutes.setAdapter(adapter);
    }*/

    /*@Override
    public void onStop() {

        super.onStop();
        adapter.stopListening();

    }

    @Override
    public void onStart() {

        super.onStart();
        adapter.startListening();

    }*/

    // Requests permission for location from user
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission for location services granted");
                    getLastLocation();
                } else {
                    Log.i(TAG, "Permission for location services DENIED");
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.i(TAG, "LOCATION RECEIVED: " +  latitude + ", " + longitude);
            }
            else{
                Log.i(TAG, "Received location is NULL");
            }
        });
    }


    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.rbCurrentLocation:
                if (checked)
                    Toast.makeText(AddChorestActivity.this, "Current Location", Toast.LENGTH_SHORT).show();

                // Address text view and button is greyed out
                etTypeAddress.setEnabled(false);
                btTypeAddress.setEnabled(false);

                // Implement GPS location feature

                break;

            case R.id.rbChooseLocation:
                if(checked)
                    Toast.makeText(AddChorestActivity.this, "Choose A Location", Toast.LENGTH_SHORT).show();

                // Address text view and button can be used
                etTypeAddress.setEnabled(true);
                btTypeAddress.setEnabled(true);

                // Implement Google Maps api
                break;
        }


    }

    // Go back to the home page
    private void goToMain(){

        try {
            Log.d(TAG, "Successfully signed out");
            //Toast.makeText(this, "Successfully saved chorest", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
            finish();

        } catch (Exception e) {
            Log.w(TAG, "Issue with signout", e);
            Toast.makeText(this, "Failed to save chorest", Toast.LENGTH_SHORT).show();
            return;
        }

    }

/*    private class AddChorestViewHolder extends RecyclerView.ViewHolder {

        private TextView tvHomeName;

        public AddChorestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHomeName = itemView.findViewById(R.id.tvHomeName);

        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());


                etTypeAddress.setText(place.getAddress());
                LatLng latLng = place.getLatLng();

               latitude = latLng.latitude;
               longitude = latLng.longitude;
                Log.i(TAG, "LatLng: " + place.getLatLng() + ", Lat: " + latitude + ", Lng: "+ longitude);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.

                // Initialize the status
                Status status = Autocomplete.getStatusFromIntent(data);

                Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}