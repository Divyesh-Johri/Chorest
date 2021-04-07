package com.example.chorest_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.chorest_app.Fragments.MapFragment;

public class AddChorestActivity extends AppCompatActivity {

    private RadioGroup rgLocation;
    private RadioButton rbCurrentLocation;
    private RadioButton rbChooseLocation;
    private Button btCalculateMap;
    private static final int  RC_TO_MAP  = 23;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chorest);

        rgLocation = findViewById(R.id.rgLocation);
        rbCurrentLocation = findViewById(R.id.rbCurrentLocation);
        rbChooseLocation = findViewById(R.id.rbChooseLocation);
        btCalculateMap = findViewById(R.id.btCalculateMap);

        // Go to the generated map of the user's chorest route
        btCalculateMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If the user has added a start and at least one destination location, show button
                // Else, leave the button greyed out

                goToMap();
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

    private void goToMap(){
        /*Intent i = new Intent(this, MainActivity.class);

        i.putExtra("keyBoolean", true);
        startActivity(i);
        finish();*/

        Intent i = getIntent();
        startActivityForResult(i,RC_TO_MAP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_TO_MAP){

            FragmentManager fm = getSupportFragmentManager();
            MapFragment chorestMap = new MapFragment();
            fm.beginTransaction().replace(R.id.addChorestContainer, chorestMap).commit();


            Toast.makeText(AddChorestActivity.this, "Going to Map", Toast.LENGTH_SHORT).show();

            //finish();
        }
        else{
            Toast.makeText(AddChorestActivity.this, "Error going to Map", Toast.LENGTH_SHORT).show();
        }
    }
}