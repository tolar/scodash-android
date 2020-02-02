package com.scodash.android.activities;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.scodash.android.R;
import com.scodash.android.dto.Dashboard;
import com.scodash.android.services.impl.RecentsLoadedListener;
import com.scodash.android.services.impl.ScodashService;

public class RecentDashboardsAdapter extends RecyclerView.Adapter<RecentDashboardsAdapter.ViewHolder> implements RecentsLoadedListener {

    private final Context context;
    private final ScodashService scodashService;
    private final ScodashActivity scodashActivity;
    private Snackbar snackbar;

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

        Dashboard dashboard = scodashService.getRecentDashboard(index);
        handleDashboardFromServer(viewHolder, dashboard);


//        final String hash = scodashService.getHashesFromLocalStorage(scodashActivity.getScodashSharedPreferences()).get(index);
//        if (!scodashService.getDashboardMap().containsKey(hash)) {
//            final Call<Dashboard> call = scodashService.getRemoteDashboardByHash(hash);
//            call.enqueue(new Callback<Dashboard>() {
//                @Override
//                public void onResponse(Call<Dashboard> call, Response<Dashboard> response) {
//                    if (response.isSuccessful()) {
//                        scodashService.getDashboardMap().put(hash, response.body());
//                        handleDashboardFromServer(viewHolder, hash, response.body());
//                    } else {
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Dashboard> call, Throwable t) {
//                    call.cancel();
//                }
//            });
//        } else {
//            handleDashboardFromServer(viewHolder, hash, scodashService.getDashboardMap().get(hash));
//        }


    }

    private void handleDashboardFromServer(@NonNull ViewHolder viewHolder, Dashboard dashboard) {
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

        ImageButton removeBtn = itemView.findViewById(R.id.remove_btn);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scodashService.removeHashFromLocaStorage(scodashActivity.getScodashSharedPreferences(), dashboard.getHash());

                snackbar = Snackbar.make(scodashActivity.findViewById(R.id.coordinator), R.string.recent_dashboard_removed, Snackbar.LENGTH_LONG);
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(scodashActivity, R.color.greenAddColor));
                snackbar.setActionTextColor(ContextCompat.getColor(scodashActivity, R.color.colorPureWhite));
                snackbar.setTextColor(ContextCompat.getColor(scodashActivity, R.color.colorPureWhite));

                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scodashService.putHashToLocalStorage(scodashActivity.getScodashSharedPreferences(), dashboard.getHash());
                        handleRecentsChanged();
                    }
                });
                snackbar.show();
                handleRecentsChanged();
            }
        });
    }

    private void handleRecentsChanged() {
        notifyDataSetChanged();
        if (scodashService.getHashesFromLocalStorage(scodashActivity.getScodashSharedPreferences()).size() == 0) {
            scodashActivity.recreate();
        }
    }

    private void startDashboardActivity(String hash) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.putExtra(DashboardActivity.HASH, hash);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return scodashService.getLoadedDashboardsSize();
    }

    @Override
    public void recentLoaded() {
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView itemView;

        public ViewHolder(@NonNull CardView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
