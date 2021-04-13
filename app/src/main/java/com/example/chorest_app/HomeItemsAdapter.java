package com.example.chorest_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

// Responsible for displaying data from the model into a row in the recycler view
//public class HomeItemsAdapter extends RecyclerView.Adapter<HomeItemsAdapter.ViewHolder> {

public class HomeItemsAdapter extends FirestoreRecyclerAdapter<HomeModel, HomeItemsAdapter.HomeItemsHolder> {


    public HomeItemsAdapter(@NonNull FirestoreRecyclerOptions<HomeModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull HomeItemsHolder holder, int position, @NonNull HomeModel model) {
        holder.tvHomeName.setText(model.getName());

    }

    @NonNull
    @Override
    public HomeItemsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_home_list, parent, false);

        return new HomeItemsHolder(v);
    }

    class HomeItemsHolder extends RecyclerView.ViewHolder {
        TextView tvHomeName;

        public HomeItemsHolder(View itemView) {
            super(itemView);

            tvHomeName = itemView.findViewById(R.id.tvHomeName);
        }
    }

    // To delete a route
    public interface OnLongClickListener {

        void onItemLongClicked(int position);
    }

    // To edit a route
    public interface OnClickListener {

        void onItemClicked(int position);
    }




    /*public interface OnClickListener {
        void onItemClicked(int position);
    }
    //end of edit

    public interface OnLongClickListener {
        void onItemLongClicked(int position);
    }

    List<String> items;
    OnLongClickListener longClickListener;
    OnClickListener clickListener; //edit
    public HomeItemsAdapter(List<String> items, OnLongClickListener longClickListener){//Listener) { //added comma and OnclickListener clicklistener for edit
        this.items = items;
        this.longClickListener = longClickListener;
        this.clickListener = clickListener; //edit
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use layout inflater to inflate a view
        View todoView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        // wrap it inside a view Holder and return it
        return new ViewHolder(todoView);
    }
    // responsible for binding data to a particular view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Grab the item at the position
        String item = items.get(position);
        // Bind the item into the specified view holder
        holder.bind(item);

    }

    // Tells the RV how many items are in the list
    @Override
    public int getItemCount() {
        return items.size();
    }

    // Container to provide easy access to views that represent each row of the list
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //tvItem = itemView.findViewById(android.R.id.text1);
        }

        // Update the view inside of the view holder with this data
        public void bind(String item) {
            tvItem.setText(item);
            // edit
            tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClicked(getAdapterPosition()); // go to main activity

                }
            });
            // end of edit
            tvItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Notify the listener which position was long pressed
                    longClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
        }
    }*/
}

/*
public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore  firebaseFirestore;
    private RecyclerView mFirestoreList;

    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestoreList = findViewById(R.id.firestore_list);

        //Query -[6:02]
        Query query = firebaseFirestore.collection("Products"); //collectionPath

        //RecyclerOptions
        FirestoreRecyclerOptions<ProductsModel> options = new FirestoreRecyclerOptions.Builder<ProductsModel>()
                .setQuery(query, ProductsModel.class) //ProductsModel - [6:50] - sub our model class
                .build();

        adapter = new FirestoreRecyclerAdapter<ProductsModel, ProdcustViewHolder>() { //(name of our view holder in place of ProductsViewHolder - create innerclass(click red thing)) then implement methods after u come to that ...then pass in options[8:01]
            //after implement methods
            //onCreateView
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home, parent, false) //[8:28] attachToRoot
            //return new ProductsViewHolder(view); //(instead of null)

            //onbindviewholder
            //holder.list_name.setText(model.getName()); //getName depends on what is stored in ProductModels.java [10:13]
            //holder.list_price.setText(model.getPrice() + ""); //[10:38]


        }

        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);

    }



    //private class ProductsViewHolder extends RecyclerView.ViewHolder { //create new constructor - [7:49]
    //private TextView list_name; //[8:51]
    //private TextView list_price;

    // public ProductsViewHolder(@NonNull View itemView) {
    //super(itemView)
    //}
    //after this implement methods [7:46]

    list_name = itemview.findViewById(R.id.list_name);
    list_price = itemview.findViewById(R.id.list_price);

    //onbindview holder under implement methods [9:56]
}

// protectedvoid onstop // [11:10]
//super.onstop
    adapter.stopListening();

            // protected void onstart

            //adapter.startListening();




            }*/
