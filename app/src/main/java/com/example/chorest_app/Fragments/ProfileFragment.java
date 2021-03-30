package com.example.chorest_app.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.chorest_app.MainActivity;
import com.example.chorest_app.R;

import static com.example.chorest_app.LoginActivity.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile2, container, false);
    }
    FirebaseAuth.getInstance().

    signOut();

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
    if(user !=null)

    {
        Log.w(TAG, "Issue with login", e);
        Toast.makeText(ProfileFragment.this, "Issue with signout", Toast.LENGTH_SHORT).show();
        return;
    }
    else;

    {
        Log.d(TAG, "Successfully signed out");
        Toast.makeText(ProfileFragment.this, "Successfully signed out", Toast.LENGTH_SHORT).show();
        return;

        // TODO: navigate to the main activity if the user has signed in properly
        goMainActivity();
        Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
    }
}



//change page has option to go to any other screen

//signout
