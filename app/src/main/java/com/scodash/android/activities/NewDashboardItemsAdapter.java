package com.scodash.android.activities;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.scodash.android.R;
import com.scodash.android.dto.Item;
import com.scodash.android.services.impl.ScodashService;

public class NewDashboardItemsAdapter extends RecyclerView.Adapter<NewDashboardItemsAdapter.ViewHolder> {

    private final ScodashService scodashService;

    public NewDashboardItemsAdapter(ScodashService scodashService) {
        this.scodashService = scodashService;
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
        textView.setText((scodashService.getNewDashboard().getItems().toArray(new Item[0]))[i].getName());
    }

    @Override
    public int getItemCount() {
        return scodashService.getNewDashboard().getItems().size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView itemView;

        public ViewHolder(@NonNull CardView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
