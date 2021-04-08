/*
package com.example.chorest_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.chorest_app.Fragments.HomeFragment;

public class HomeEditActivity extends AppCompatActivity {

    EditText etChorest;
    Button btnSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_edit);

        etChorest = findViewById(R.id.etChorest);
        btnSave = findViewById(R.id.btnSave);

        getSupportActionBar().setTitle("Edit Chorest");

        etChorest.setText(getIntent().getStringExtra(HomeFragment.KEY_ITEM_TEXT));

        //When the user is done editing, they click the save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create an intent which will contain the results
                Intent intent = new Intent();

                //pass the data(results of editing)
                intent.putExtra(HomeFragment.KEY_ITEM_TEXT, etChorest.getText().toString());
                intent.putExtra(HomeFragment.KEY_ITEM_POSITION, getIntent().getExtras().getInt(HomeFragment.KEY_ITEM_POSITION));

                //set the result of the intent
                setResult(RESULT_OK, intent);

                //finish activity, close the screen and go back
                finish();

            }
        });
    }
}*/
