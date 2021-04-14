package com.example.chorest_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class AddChorestActivity<True> extends AppCompatActivity {

    private static final String TAG = "AddChorestActivity";
    private RadioGroup rgLocation;
    private RadioButton rbCurrentLocation;
    private RadioButton rbChooseLocation;
    private Button btCalculateMap;
    private EditText etChorestName;
    private RecyclerView rvCalculatedRoutes;
    private Button btTypeAddress;
    private Button btAddLocations;
    private static final int RC_TO_MAP = 23;

    private static final String KEY_CHOREST_NAME = "name";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;

    private double longitude;
    private double latitude;
    private boolean location;
    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chorest);
        startLocationUpdates();
        location = true;
        mAuth = FirebaseAuth.getInstance();
        rgLocation = findViewById(R.id.rgLocation);
        rbCurrentLocation = findViewById(R.id.rbCurrentLocation);
        rbChooseLocation = findViewById(R.id.rbChooseLocation);
        btCalculateMap = findViewById(R.id.btCalculateMap);
        etChorestName = findViewById((R.id.etChorestName));
        rvCalculatedRoutes = findViewById(R.id.rvCalculatedRoutes);
        btTypeAddress = findViewById(R.id.btTypeAddress);
        btAddLocations = findViewById(R.id.btAddLocations);

        //Location
//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //fine location - gps
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        double longitude = location.getLongitude();
//        double latitude = location.getLatitude();
//        //Location
//
//        if (Location == null) {
//
//        }


//        final LocationListener locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                longitude = location.getLongitude();
//                latitude = location.getLatitude();
//            }
//
//            @Override
//            public void onStatusChanged(String s, int i, Bundle bundle) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String s) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String s) {
//                longitude = -77.859909;
//                latitude = 40.8148;
//            }
//        };
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            ActivityCompat.(requestPermissions);
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);



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

//        btTypeAddress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        btAddLocations.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//
//        private final LocationListener locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                longitude = location.getLongitude();
//                latitude = location.getLatitude();
//            }
//        }
//
//        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);


        FirebaseUser currentUser = mAuth.getCurrentUser();

        Query query = db.collection("users").document(currentUser.getUid()).collection("chorests");

        //RecyclerOptions
        FirestoreRecyclerOptions<Chorest> options = new FirestoreRecyclerOptions.Builder<Chorest>()
                .setQuery(query, Chorest.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Chorest, AddChorestActivity.AddChorestViewHolder>(options) {



            @NonNull
            @Override
            public AddChorestActivity.AddChorestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_home_list, parent, false);
                return new AddChorestActivity.AddChorestViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AddChorestActivity.AddChorestViewHolder holder, int position, @NonNull Chorest model) {
                holder.tvHomeName.setText(model.getName());


                /*holder.ViewHolder.setOnClickListener(new HomeViewHolder.Clicklistener()){

                }*/
            }



        };
        rvCalculatedRoutes.setHasFixedSize(true);
        rvCalculatedRoutes.setLayoutManager(new LinearLayoutManager(this));
        rvCalculatedRoutes.setAdapter(adapter);


    }

    //Location
    protected void startLocationUpdates() {
        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if(checkPermissions()) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_FINE_LOCATION);
    }
    //Location

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()){
            case R.id.rbCurrentLocation:
                if(checked)
                    Toast.makeText(AddChorestActivity.this, "Current Location", Toast.LENGTH_SHORT).show();
                    location = true;
                    //get longitude and latitude from current gps
                    // Implement GPS location feature
                    // Address text view is greyed out
                    break;

            case R.id.rbChooseLocation:
                if(checked)
                    Toast.makeText(AddChorestActivity.this, "Choose A Location", Toast.LENGTH_SHORT).show();
                    location = false;
                    // Address text view can be used
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

    private class AddChorestViewHolder extends RecyclerView.ViewHolder {

        private TextView tvHomeName;

        public AddChorestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHomeName = itemView.findViewById(R.id.tvHomeName);

        }
    }
}