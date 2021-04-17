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

    private ArrayList<String> choreList;

    public AddChorestChoresListAdapter(ArrayList<String> choreList) {
        this.choreList = choreList;
    }

   /* public void setData(ArrayList<String> list){
        this.choreList = list;
        //notifyDataSetChanged();
    }*/

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

        }
    }
}
