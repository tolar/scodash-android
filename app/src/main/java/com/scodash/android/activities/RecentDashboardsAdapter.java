package com.scodash.android.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.scodash.android.R;
import com.scodash.android.services.impl.DashboardService;

class RecentDashboardsAdapter extends RecyclerView.Adapter<RecentDashboardsAdapter.ViewHolder> {

    private DashboardService dashboardService;

    private String[] names = new String[] {"Test", "Today Scrabble Game"};
    private String[] hashes = new String[] {"qMjiPQFo", "qMjiPQFo"};

    public RecentDashboardsAdapter(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_dashboard_reference, parent, false);
        return new RecentDashboardsAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int index) {
        CardView itemView = viewHolder.itemView;
        TextView recentItem = itemView.findViewById(R.id.name);
        recentItem.setText(names[index]);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboardService.connectToServer(hashes[index]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView itemView;

        public ViewHolder(@NonNull CardView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
