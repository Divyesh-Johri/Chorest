package com.example.chorest_app.Fragments;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chorest_app.AddChorestActivity;
import com.example.chorest_app.ChorestsModel;
//import com.example.chorest_app.ItemsAdapter;
//import com.example.chorest_app.HomeEditActivity;
import com.example.chorest_app.HomeItemsAdapter;
import com.example.chorest_app.HomeModel;
import com.example.chorest_app.LoginActivity;
import com.example.chorest_app.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

import static android.app.Activity.RESULT_OK;

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

    //edit
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;
    //edit


    //List<String> homeItems;

    //RecyclerView rvMap;
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

        Query query = firebaseFirestore.collection("users").document(currentUser.getUid()).collection("chorests");

        //RecyclerOptions
        FirestoreRecyclerOptions<HomeModel> options = new FirestoreRecyclerOptions.Builder<HomeModel>()
                .setQuery(query, HomeModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<HomeModel, HomeViewHolder>(options) {
            @NonNull
            @Override
            public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_home_list, parent, false);
                return new HomeViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull HomeViewHolder holder, int position, @NonNull HomeModel model) {
                holder.tvHomeName.setText(model.getName());
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


    }

        // Firebase code to get items saved there
        //loadItems();

        /*HomeItemsAdapter.OnLongClickListener OnLongClickListener = new HomeItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                // Delete the item from the model
                homeItems.remove(position);
                //Notify the adapter
                homeItemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getActivity().getApplicationContext(), "Item was removed ", Toast.LENGTH_SHORT).show(); //Chore was deleted
                //saveItems(); // Firebase is where it's saved

            }
        };
        //edit
        HomeItemsAdapter.OnClickListener onClickListener = new HomeItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                //create the new activity
                Intent i = new Intent(getActivity(), HomeEditActivity.class);
                //pass the data being edited
                i.putExtra(KEY_ITEM_TEXT, homeItems.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                //display the activity
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };
        //edit
        homeItemsAdapter = new HomeItemsAdapter(homeItems, OnLongClickListener); //, onClickListener); //edit onClickListener
        rvSavedChorests.setAdapter(homeItemsAdapter);
        rvSavedChorests.setLayoutManager(new LinearLayoutManager(getActivity()));*/



    private class HomeViewHolder extends RecyclerView.ViewHolder {

        private TextView tvHomeName;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHomeName = itemView.findViewById(R.id.tvHomeName);
        }
    }

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




/*
public class MainActivity extends AppCompatActivity {

    //edit
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;
    //edit

    List<String> items;

    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);

        loadItems();

        ItemsAdapter.OnLongClickListener OnLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                // Delete the item from the model
                items.remove(position);
                //Notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed ", Toast.LENGTH_SHORT).show(); //Chore was deleted
                saveItems();

            }
        };
        //edit
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                //create the new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                //pass the data being edited
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                //display the activity
                startActivityForResult(i, EDIT_TEXT_CODE);
            }
        };
        //edit
        itemsAdapter = new ItemsAdapter(items, OnLongClickListener, onClickListener); //edit onClickListener
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = etItem.getText().toString();
                // Add item to the model
                items.add(todoItem);
                // Notify adapter that an item is inserted
                itemsAdapter.notifyItemInserted(items.size() - 1);
                etItem.setText("");
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });

    }

    //handle the result of the edit activity
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            //Retrieve the updated text value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            //extract the original position of the  edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            //update the model with the new item
            items.set(position, itemText)
            //notify the adapter
            itemsAdapter.notifyItemChanged(position);
            //persist changes
            saveItems();
            Toast.makeText(getApplicationContext(), "Chorest updated successfully!", Toast.LENGTH_SHORT).show();

        } else {
            Log.w("MainActivity", "Unknown call to onActivityResult");
        }
    }

    //Persistence
    private File getDataFile() {

        return new File(getFilesDir(),"data.txt");
    }

    // This function will load items by reading every line of the data file
    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items",e);
            items = new ArrayList<>();
        }
    }
    // This function saves items by writing them into the data file
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items",e);
        }
    }
}


*/
