package com.scodash.android.activities;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
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

    private final Context context;
    private final ScodashService scodashService;
    private final ScodashActivity scodashActivity;

    public RecentDashboardsAdapter(Context context, ScodashService scodashService, ScodashActivity scodashActivity) {
        this.context = context;
        this.scodashService = scodashService;
        this.scodashActivity = scodashActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_dashboard_item, parent, false);
        return new RecentDashboardsAdapter.ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int index) {

        final String hash = scodashService.getHashesFromLocalStorage(scodashActivity.getScodashSharedPreferences()).get(index);
        final Dashboard dashboard = scodashService.getRemoteDashboardByHash(hash);

        CardView itemView = viewHolder.itemView;
        TextView recentName = itemView.findViewById(R.id.name);
        recentName.setText(dashboard.getName());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDashboardActivity(dashboard.getHash());
            }
        });
        TextView recentDescription = itemView.findViewById(R.id.description);
        recentDescription.setText(dashboard.getDescription());
        TextView recentMode = itemView.findViewById(R.id.mode);
        recentMode.setText(
                (!TextUtils.isEmpty(dashboard.getWriteHash())) ? "" :  context.getString(R.string.view_only));
    }

    private void startDashboardActivity(String hash) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.putExtra(DashboardActivity.HASH, hash);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return scodashService.getHashesFromLocalStorage(scodashActivity.getScodashSharedPreferences()).size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView itemView;

        public ViewHolder(@NonNull CardView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
