package com.example.chorest_app.Fragments;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.chorest_app.Chorest;
import com.example.chorest_app.R;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.Collections;


public class MapFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "MapFragment";
    ArrayList<Chorest> userChorests;
    Chorest focusChorest;
    Spinner spMapDropDown;
    Button btMaps;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spMapDropDown = view.findViewById(R.id.spMapDropDown);
        btMaps = view.findViewById(R.id.btMaps);
        userChorests = new ArrayList<>();

        // Get user chorests and add to Spinner
        Chorest.getAllUserChorests(new Chorest.UserChorestsCallback() {
            @Override
            public void onCallback(ArrayList<Chorest> chorests) {
                Log.i(TAG, "User Chorests found: Size " + chorests.size());

                ArrayList<String> chorestNames = new ArrayList<>();
                for(int i = 0; i < chorests.size(); i++){
                    chorestNames.add(chorests.get(i).getName());
                    userChorests.add(chorests.get(i));
                }
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, chorestNames);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                spMapDropDown.setAdapter(adapter);
            }
        });

        // Item selected listener
        spMapDropDown.setOnItemSelectedListener(this);

        // Button
        btMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Navigating to google maps... ");

                // Navigate to Google Maps
                ArrayList<String> routeAddr = focusChorest.getRoute_addresses();
                String currentLoc = focusChorest.getLocLat() + "," + focusChorest.getLocLong();
                String waypointString = "";
                for(int i = 0; i < routeAddr.size(); i++){
                    waypointString += Uri.encode(routeAddr.get(i)) + "|";
                }

//                Uri gmmIntentUri = Uri.parse("geo:"+loc_lat+","+loc_long+"?q=restaurants");
                Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + currentLoc
                        + "&destination=" + currentLoc + "&waypoints=" + waypointString);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(userChorests == null){
            return;
        }
        focusChorest = userChorests.get(i);
        Log.i(TAG, "FocusChorest set to: " + focusChorest.getName());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        focusChorest = null;
        Log.i(TAG, "FocusChorest currently null");
    }
}