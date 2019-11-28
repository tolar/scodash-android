package com.scodash.android.activities;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.scodash.android.R;
import com.scodash.android.dto.Dashboard;
import com.scodash.android.services.impl.ScodashService;

class RecentDashboardsAdapter extends RecyclerView.Adapter<RecentDashboardsAdapter.ViewHolder> {

    private Context context;
    private ScodashService scodashService;

    public RecentDashboardsAdapter(Context context, ScodashService scodashService) {
        this.context = context;
        this.scodashService = scodashService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_dashboard_item, parent, false);
        return new RecentDashboardsAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int index) {

        final Dashboard dashboard = scodashService.getLocalDashboards().get(index);

        CardView itemView = viewHolder.itemView;
        TextView recentName = itemView.findViewById(R.id.name);
        recentName.setText(dashboard.getName());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scodashService.setNewCurrentDashboard(dashboard);
                startDashboardActivity(dashboard.getHash());
            }
        });
        TextView recentDescription = itemView.findViewById(R.id.description);
        recentDescription.setText(dashboard.getDescription());
        TextView recentMode = itemView.findViewById(R.id.mode);
        recentMode.setText(dashboard.isWriteMode() ? "" :  context.getString(R.string.view_only) );
    }

    private void startDashboardActivity(String hash) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.putExtra(DashboardActivity.HASH, hash);
        context.startActivity(intent);
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
