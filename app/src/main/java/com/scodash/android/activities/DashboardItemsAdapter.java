package com.scodash.android.activities;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scodash.android.R;
import com.scodash.android.dto.Dashboard;
import com.scodash.android.dto.Item;

public class DashboardItemsAdapter extends RecyclerView.Adapter<DashboardItemsAdapter.ViewHolder> {

    private Dashboard dashboard;

    public DashboardItemsAdapter(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        LinearLayout itemLineView = viewHolder.itemView.findViewById(R.id.item_line);
        // set item name
        TextView nameTextView = itemLineView.findViewById(R.id.item_name);
        final Item item = (dashboard.getItems().toArray(new Item[0]))[i];
        nameTextView.setText(item.getName());
        // set item score
        TextView scoreTextView = itemLineView.findViewById(R.id.score_text);
        scoreTextView.setText(String.valueOf(item.getScore()));


        itemLineView.findViewById(R.id.inc_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setScore(item.getScore()+1);
                notifyDataSetChanged();
            }
        });
        itemLineView.findViewById(R.id.dec_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getScore() > 0) {
                    item.setScore(item.getScore() - 1);
                    notifyDataSetChanged();
                }
            }
        });

        LinearLayout commasView = itemLineView.findViewById(R.id.commas);
        commasView.removeAllViews();

        int fives = item.getScore() / 5;
        int rest = item.getScore() % 5;

        for (int j = 0; j < fives; j++) {
            ImageView fiveCommasView = new ImageView(itemLineView.getContext());
            fiveCommasView.setImageResource(R.drawable.five_carek);
            commasView.addView(fiveCommasView);
        }
        ImageView restCommasView = new ImageView(itemLineView.getContext());
        if (rest > 0 && rest <= 5) {
            int resourceId = -1;
            switch (rest) {
                case 1:
                    resourceId = R.drawable.one_carka;
                    break;
                case 2:
                    resourceId = R.drawable.two_carky;
                    break;
                case 3:
                    resourceId = R.drawable.three_carky;
                    break;
                case 4:
                    resourceId = R.drawable.four_carky;
                    break;
                case 5:
                    resourceId = R.drawable.five_carek;
                    break;
            }
            restCommasView.setImageResource(resourceId);
        }
        commasView.addView(restCommasView);

    }

    @Override
    public int getItemCount() {
        return dashboard.getItems().size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView itemView;

        public ViewHolder(@NonNull CardView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
