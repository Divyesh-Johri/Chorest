package com.example.chorest_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 20;
    // For userExists function
    public boolean bUserExists;
    public int userExistsError;         // 0 if no error, 1 if error

    // Initialize Firebase authenticator and database
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView tvLogo;
    EditText etEmail;
    EditText etPassword;
    Button btEmail;
    SignInButton sbtGoogle;
    Button btSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        tvLogo = findViewById(R.id.tvLogo);
        btEmail = findViewById(R.id.btEmail);
        sbtGoogle = findViewById(R.id.sbtGoogle);
        btSignUp = findViewById(R.id.btSignUp);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance();


        // Sign In with Email listener
        btEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etEmail.getText() != null && etPassword.getText() != null){
                    signIn(etEmail.getText().toString(), etPassword.getText().toString());
                }
                else{
                    Toast.makeText(LoginActivity.this, "Please input an email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sign in with Google Authentication
        sbtGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        // Sign up with email listener
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etEmail.getText() != null && etPassword.getText() != null){
                    signUp(etEmail.getText().toString(), etPassword.getText().toString());
                }
                else{
                    Toast.makeText(LoginActivity.this, "Please input an email and password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null)
        FirebaseUser currentUser = mAuth.getCurrentUser();

//        if(currentUser != null){
//            Log.i(TAG, "EMAIL: " + currentUser.getEmail());
//            Log.i(TAG, "USER ID: " + currentUser.getUid());
//            goToMain();
//        }
    }

    // Navigate to MainActivity
    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    // Sign in with email/pass using Firebase Auth
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

    // Use Google Authenticator to sign in
    private void signInWithGoogle() {
        Log.i(TAG, "Now launching Google sign in activity");

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Navigate to Google Sign In Activity
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // Result from Google Sign In Activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // Get parceable of data from GoogleSignIn
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(LoginActivity.this, "Couldn't sign in using Google", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // Authenticate Google account with firebase
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential: SUCCESS");

                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.i(TAG, "Current user ID: " + user.getUid());

                            // If user profile exists, sign in
                            signInIfUserExists(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Couldn't sign in using Google", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    // Decide to sign in or sign up authenticated user
    private void signInIfUserExists(FirebaseUser user) {
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.i(TAG, "Document exists = " + document.exists());

                    if(document.exists()){
                        // If user's profile exists, just go to home page
                        Log.i(TAG, "Signing in user: " + user.getUid());
                    }
                    else{
                        // If user profile doesn't exist, sign them up (add them to database)
                        Log.i(TAG, "Signing up user and adding to database: " + user.getUid());
                        addUser(user);
                    }

                    goToMain();

                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    Toast.makeText(LoginActivity.this, "Failed to validate user, try signing in again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Sign up using given email and password
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

    // Add specified user to the database of users
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