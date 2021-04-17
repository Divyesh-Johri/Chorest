package com.example.chorest_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AddChorestChoresListAdapter extends RecyclerView.Adapter<AddChorestChoresListAdapter.AddChorestViewHolder> {


    // Chores list public interface to delete a chore
    public interface OnLongClickListener{
        void onItemLongClicked(int positon);
    }

    private ArrayList<String> choreList;
    private OnLongClickListener longClickListener;

    public AddChorestChoresListAdapter(ArrayList<String> choreList, OnLongClickListener longClickListener) {
        this.choreList = choreList;
        this.longClickListener = longClickListener;
    }



    @NonNull
    @Override
    public AddChorestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,parent,false);

        return new AddChorestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddChorestViewHolder holder, int position) {

        String chore = choreList.get(position);
        if (chore == null){
            return;
        }

        holder.bind(chore);
    }

    @Override
    public int getItemCount() {
        if (choreList != null){

            return choreList.size();
        }
        return 0;
    }

    public class AddChorestViewHolder extends RecyclerView.ViewHolder{

        private TextView tvChore;

        public AddChorestViewHolder(@NonNull View itemView) {
            super(itemView);

            tvChore = itemView.findViewById(android.R.id.text1);
            //tvRouteName = itemView.findViewById(R.id.);
        }


        public void bind(String chore) {
            tvChore.setText(chore);

            // Notify listener of position of chore to be deleted
            tvChore.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    longClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });

        }
    }
}
