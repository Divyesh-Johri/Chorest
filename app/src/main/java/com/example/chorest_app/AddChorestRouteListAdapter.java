package com.example.chorest_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AddChorestRouteListAdapter extends RecyclerView.Adapter<AddChorestRouteListAdapter.AddChorestViewHolder> {

    private ArrayList<String> routeList;

    public AddChorestRouteListAdapter(ArrayList<String> choreList) {
        this.routeList = choreList;
    }

    @NonNull
    @Override
    public AddChorestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,parent,false);

        return new AddChorestRouteListAdapter.AddChorestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddChorestViewHolder holder, int position) {

        String route = routeList.get(position);
        if (route == null){
            return;
        }

        holder.bind(route);
    }

    @Override
    public int getItemCount() {
        if (routeList != null){

            return routeList.size();
        }
        return 0;
    }

    public class AddChorestViewHolder extends RecyclerView.ViewHolder {

        private TextView tvRoute;

        public AddChorestViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRoute = itemView.findViewById(android.R.id.text1);

        }

        public void bind(String route) {
            tvRoute.setText(route);
        }
    }
}
