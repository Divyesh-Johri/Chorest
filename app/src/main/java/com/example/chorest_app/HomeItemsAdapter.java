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

public class HomeItemsAdapter extends FirestoreRecyclerAdapter{

    public HomeItemsAdapter(@NonNull FirestoreRecyclerOptions options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Object model) {

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }


    class Chorest extends RecyclerView.ViewHolder{

        public Chorest(View itemView){
            super(itemView);
        }
    }

    // To delete a route
    public interface  OnLongClickListener{

        void onItemLongClicked(int position);
    }

    // To edit a route
    public interface  OnClickListener{

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