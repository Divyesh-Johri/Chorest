package com.example.chorest_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.chorest_app.Fragments.MapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddChorestActivity extends AppCompatActivity {

    private static final String TAG = "AddChorestActivity";
    private RadioGroup rgLocation;
    private RadioButton rbCurrentLocation;
    private RadioButton rbChooseLocation;
    private Button btCalculateMap;
    private EditText etChorestName;
    private static final int  RC_TO_MAP  = 23;

    private static final String  KEY_CHOREST_NAME = "name";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


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

        // Go to the generated map of the user's chorest route
        btCalculateMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the user has added a start and at least one destination location, show button
                // Else, leave the button greyed out

                goToMain();
            }
        });

        etChorestName.setOnClickListener(new View.OnClickListener() {
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


    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()){
            case R.id.rbCurrentLocation:
                if(checked)
                    Toast.makeText(AddChorestActivity.this, "Current Location", Toast.LENGTH_SHORT).show();
                    // Implement GPS location feature
                    // Address text view is greyed out
                    break;

            case R.id.rbChooseLocation:
                if(checked)
                    Toast.makeText(AddChorestActivity.this, "Choose A Location", Toast.LENGTH_SHORT).show();
                    // Address text view can be used
                    // Implement Google Maps api
                    break;
        }
    }

    private void goToMain(){


        try {
            Log.d(TAG, "Successfully signed out");
            Toast.makeText(this, "Successfully saved chorest", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
            finish();

        } catch (Exception e) {
            Log.w(TAG, "Issue with signout", e);
            Toast.makeText(this, "Failed to save chorest", Toast.LENGTH_SHORT).show();
            return;
        }


        /*Intent i = new Intent(this, MainActivity.class);

        //i.putExtra("keyBoolean", true);
        startActivity(i);
        finish();*/

        /*Intent i = getIntent();
        startActivityForResult(i,RC_TO_MAP);*/
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_TO_MAP){

            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();

           *//* FragmentManager fm = getSupportFragmentManager();
            MapFragment chorestMap = new MapFragment();
            fm.beginTransaction().replace(R.id.addChorestContainer, chorestMap).commit();*//*


            Toast.makeText(AddChorestActivity.this, "Going to Map", Toast.LENGTH_SHORT).show();


        }
        else{
            Toast.makeText(AddChorestActivity.this, "Error going to Map", Toast.LENGTH_SHORT).show();
        }
    }*/
}