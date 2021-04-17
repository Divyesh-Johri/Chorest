package com.example.chorest_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AddChorestAdapter extends FirestoreRecyclerAdapter<Chorest, AddChorestAdapter.AddChorestHolder> {


    public AddChorestAdapter(@NonNull FirestoreRecyclerOptions<Chorest> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AddChorestHolder holder, int position, @NonNull Chorest model) {
        holder.tvRouteName.setText(model.getName()); // change to getRoute for the 2nd recyclerview
    }

    @NonNull
    @Override
    public AddChorestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_add_chorest_routes,parent,false);
        return new AddChorestHolder(v);
    }

    class AddChorestHolder extends RecyclerView.ViewHolder{
        TextView tvRouteName;

        public AddChorestHolder(@NonNull View itemView) {
            super(itemView);

            tvRouteName = itemView.findViewById(R.id.tvRouteName);
        }
    }
}
