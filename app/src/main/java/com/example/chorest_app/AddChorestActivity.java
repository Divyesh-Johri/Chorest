package com.example.chorest_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class AddChorestActivity extends AppCompatActivity {

    private static final String TAG = "AddChorestActivity";
    private RadioGroup rgLocation;
    private RadioButton rbCurrentLocation;
    private RadioButton rbChooseLocation;
    private Button btCalculateMap;
    private EditText etChorestName;
    private RecyclerView rvCalculatedRoutes;
    private Button btTypeAddress;
    private static final int  RC_TO_MAP  = 23;

    private static final String  KEY_CHOREST_NAME = "name";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreRecyclerAdapter adapter;


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
        btTypeAddress =  findViewById(R.id.btTypeAddress);



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

        btTypeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_add_chorest_routes, parent, false);
                return new AddChorestActivity.AddChorestViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AddChorestViewHolder holder, int position, @NonNull Chorest model) {
                holder.tvRouteName.setText(model.getName());
            }
        };

        rvCalculatedRoutes.setHasFixedSize(true);
        rvCalculatedRoutes.setLayoutManager(new LinearLayoutManager(this));
        rvCalculatedRoutes.setAdapter(adapter);
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

        private TextView tvRouteName;

        public AddChorestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRouteName = itemView.findViewById(R.id.tvRouteName);
        }
    }
}