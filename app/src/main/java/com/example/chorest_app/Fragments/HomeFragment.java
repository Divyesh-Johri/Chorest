package com.example.chorest_app.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.chorest_app.AddChorestActivity;
import com.example.chorest_app.ChorestsModel;
import com.example.chorest_app.LoginActivity;
import com.example.chorest_app.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "HomeFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseFirestore firebaseFirestore;
    private RecyclerView rvSavedChorests;
    private FloatingActionButton fabAddChorest;



    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fabAddChorest =  view.findViewById(R.id.fabAddChorest);

        fabAddChorest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddChorest();
            }


        });


                //firebaseFirestore = FirebaseFirestore.getInstance();
        //rvSavedChorests = view.findViewById(R.id.rvSavedChorests);

        // Query to get data from Firestore
       // Query query = firebaseFirestore.collection("chorests");
        // can pull in by certain order by ".orderBy(...)

        //RecyclerOptions
        //FirestoreRecyclerOptions<ChorestsModel> options = new FirestoreRecyclerOptions<>().setQuery(query);
    }


    private void goToAddChorest() {

        try {
            Log.d(TAG, "Successfully switched to Add Chorest page.");
            Toast.makeText(getActivity(), "Add Chorest Page", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(getActivity(), AddChorestActivity.class);
            startActivity(i);
            getActivity().finish();

        } catch (Exception e) {
            Log.w(TAG, "Issue with switch to Add Chorest Page", e);
            Toast.makeText(getActivity(), "Failed to go to Add Chorest", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}