package com.example.chorest_app.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chorest_app.AddChorestActivity;
//import com.example.chorest_app.ItemsAdapter;
//import com.example.chorest_app.HomeEditActivity;
import com.example.chorest_app.Chorest;
import com.example.chorest_app.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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
    private FirebaseAuth mAuth;

    private FloatingActionButton fabAddChorest;
    private OnItemLongClickListener longListener;
    private OnItemClickListener clickListener;

    //edit
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;

    RecyclerView rvSavedChorests;
    //HomeItemsAdapter homeItemsAdapter;
    private FirestoreRecyclerAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }


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
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        firebaseFirestore = FirebaseFirestore.getInstance();

        fabAddChorest =  view.findViewById(R.id.fabAddChorest);
        rvSavedChorests = view.findViewById(R.id.rvSavedChorests);

        Query query = firebaseFirestore.collection("users").document(currentUser.getUid()).collection("chorests").whereNotEqualTo("name", "dummy");

        //RecyclerOptions
        FirestoreRecyclerOptions<Chorest> options = new FirestoreRecyclerOptions.Builder<Chorest>()
                .setQuery(query, Chorest.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Chorest, HomeViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull HomeViewHolder holder, int position, @NonNull Chorest model) {
                holder.tvHomeName.setText(model.getName());
            }

            @NonNull
            @Override
            public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_home_list, parent, false);
                //view.setOnLongClickListener();
                return new HomeViewHolder(view);
            }


        };
        rvSavedChorests.setHasFixedSize(true);
        rvSavedChorests.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvSavedChorests.setAdapter(adapter);


        // Floating action button to add a chorest
        fabAddChorest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddChorest();
            }


        });

        //adapter.setOnItemClickListener(this);


    }

    /*@Override
    public void onItemLongClick(int position) {

    }

    @Override
    public void onItemClick(int position) {

    }*/


    private class HomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView tvHomeName;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHomeName = itemView.findViewById(R.id.tvHomeName);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

/*

            // OnLongClick Listener to delete a
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    int position = getAdapterPosition();

                    // The user double clicked on the item which will cause a crash
                    if (position != RecyclerView.NO_POSITION && longListener != null){
                        longListener.onItemLongClick(position);
                    }
                    return true;
                }
            });

            // OnClick Listener to edit a chorest
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    // The user double clicked on the item which will cause a crash
                    if (position != RecyclerView.NO_POSITION && clickListener != null){
                        clickListener.onItemClick(position);
                    }
                }
            });
*/
        }


        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), "On click listener", Toast.LENGTH_SHORT).show();
            int position = getAdapterPosition();

            // The user double clicked on the item which will cause a crash
            if (position != RecyclerView.NO_POSITION && clickListener != null){
                clickListener.onItemClick(position);
            }
        }

        @Override
        public boolean onLongClick(View v) {

            Toast.makeText(getActivity(), "On long click listener", Toast.LENGTH_SHORT).show();
            int position = getAdapterPosition();

            // The user double clicked on the item which will cause a crash
            if (position != RecyclerView.NO_POSITION && longListener != null){
                longListener.onItemLongClick(position);
            }
            return true;

        }
    }

    // To delete a route
    public interface OnItemLongClickListener {


        void onItemLongClick(int position);
    }

    // To edit a route
    public interface OnItemClickListener {

        void onItemClick(int position);
    }

    /*public void setOnItemLongClickListener(OnItemLongClickListener longListener){
        this.longListener = longListener;

    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.clickListener = clickListener;
    }*/

    @Override
    public void onStop() {

        super.onStop();
        adapter.stopListening();

    }

    @Override
    public void onStart() {

        super.onStart();
        adapter.startListening();

    }

    public void onItemLongClick(int position) {
    // firebase code to delete data
        FirebaseUser currentUser = mAuth.getCurrentUser();


        firebaseFirestore.collection("users").document(currentUser.getUid()).collection("chorests").document()
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    //@Override
    public void onItemClick(int position) {
    // code to go to add chorest edit activity page
    }





    //handle the result of the edit activity
    //@SuppressLint("MissingSuperCall")


/*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            //Retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            //extract the original position of the  edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            //update the model with the new item
            homeItems.set(position, itemText);
            //notify the adapter
            homeItemsAdapter.notifyItemChanged(position);
            //persist changes
            //saveItems(); Firebase save
            Toast.makeText(getActivity().getApplicationContext(), "Chorest updated successfully!", Toast.LENGTH_SHORT).show();

        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }*/



    private void goToAddChorest() {

        try {
            Log.d(TAG, "Successfully switched to Add Chorest page.");
            //Toast.makeText(getActivity(), "Add Chorest Page", Toast.LENGTH_SHORT).show();

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




