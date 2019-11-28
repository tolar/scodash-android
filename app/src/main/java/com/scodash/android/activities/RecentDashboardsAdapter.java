package com.scodash.android.activities;

import android.content.Context;
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

    private Context context;
    private DashboardService dashboardService;

    // TODO ulozit do lokalniho uloziste
    private String[] names = new String[] {"Test", "Today Scrabble Game"};
    private String[] descriptions = new String[] {"popis", "dlouhy popis"};
    private boolean[] writeModes = new boolean[] {false, true};
    private String[] hashes = new String[] {"bhnchWpU", "RH5lbxGr"};

    public RecentDashboardsAdapter(Context context, DashboardService dashboardService) {
        this.context = context;
        this.dashboardService = dashboardService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_dashboard_item, parent, false);
        return new RecentDashboardsAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int index) {
        CardView itemView = viewHolder.itemView;
        TextView recentName = itemView.findViewById(R.id.name);
        recentName.setText(names[index]);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dashboardService.connectToServer(hashes[index]);
            }
        });
        TextView recentDescription = itemView.findViewById(R.id.description);
        recentDescription.setText(descriptions[index]);
        TextView recentMode = itemView.findViewById(R.id.mode);
        recentMode.setText(writeModes[index] ? "" :  context.getString(R.string.view_only) );
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
