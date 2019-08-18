package com.scodash.android;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class DashboardItemsAdapter extends RecyclerView.Adapter<DashboardItemsAdapter.ViewHolder> {

    public DashboardItemsAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.new_item, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        CardView itemView = viewHolder.itemView;
        TextView textView = itemView.findViewById(R.id.item_name);
        textView.setText((String)NewDashboard.getInstance().getItems().toArray()[i]);
    }

    @Override
    public int getItemCount() {
        return NewDashboard.getInstance().getItems().size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView itemView;

        public ViewHolder(@NonNull CardView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
