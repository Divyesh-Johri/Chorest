package com.example.chorest_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    // Initialize Firebase authenticator and database
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView tvLogo;
    EditText etEmail;
    EditText etPassword;
    Button btEmail;
    Button btSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        tvLogo = findViewById(R.id.tvLogo);
        btEmail = findViewById(R.id.btEmail);
        btSignUp = findViewById(R.id.btSignUp);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance();


        // Sign In with Email listener
        btEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(etEmail.getText().toString(), etPassword.getText().toString());
            }
        });

        // Sign up with email listener
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp(etEmail.getText().toString(), etPassword.getText().toString());
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null)
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            Log.i(TAG, "EMAIL: " + currentUser.getEmail());
            Log.i(TAG, "USER ID: " + currentUser.getUid());
            goToMain();
        }
    }

    // Navigate to MainActivity
    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    // Sign in with email/pass using Google Auth
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Update Firestore database with new user
                            if (user != null) {
                                Log.i(TAG, "EMAIL: " + user.getEmail());
                                Log.i(TAG, "USER ID: " + user.getUid());
                                goToMain();
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Signed up successfully! Now signed in... ", Toast.LENGTH_SHORT).show();

                        // Update Firestore database with new user
                        if (user != null) {
                            addUser(user);
                            Log.i(TAG, "EMAIL: " + user.getEmail());
                            Log.i(TAG, "USER ID: " + user.getUid());

                            goToMain();
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Sign up authentication failed.", Toast.LENGTH_SHORT).show();

                    }
                }
            });
    }

    private void addUser(FirebaseUser user) {

        String uID = user.getUid();

        Map<String, Object> userFields = new HashMap<>();
        userFields.put("email", user.getEmail());

        // Setting user document
        db.collection("users").document(uID)
                .set(userFields)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(LoginActivity.this, "Unable to add user to database. Try signing up again.", Toast.LENGTH_SHORT).show();
                    }
                });

        Map<String, Object> routeFields = new HashMap<>();
        routeFields.put("name", "dummy");
        routeFields.put("timestamp", FieldValue.serverTimestamp());


        // Setting user's chorest collection
        db.collection("users/" + uID + "/chorests" ).document(uID + "ROUTE")
                .set(routeFields)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(LoginActivity.this, "Unable to add user chorests to database. Try signing up again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


