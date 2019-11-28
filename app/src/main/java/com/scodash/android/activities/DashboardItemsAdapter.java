package com.scodash.android.activities;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayout;
import com.scodash.android.R;
import com.scodash.android.dto.Dashboard;
import com.scodash.android.dto.DashboardUpdateDto;
import com.scodash.android.dto.Item;
import com.scodash.android.services.impl.ScodashService;
import com.scodash.android.services.impl.Sorting;

import java.util.Date;

public class DashboardItemsAdapter extends RecyclerView.Adapter<DashboardItemsAdapter.ViewHolder> {

    private final ScodashService scodashService;

    private Sorting sorting = Sorting.AZ;

    public DashboardItemsAdapter(ScodashService scodashService) {
        this.scodashService = scodashService;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int index) {
        LinearLayout itemLineView = viewHolder.itemView.findViewById(R.id.item_line);
        // set item name
        TextView nameTextView = itemLineView.findViewById(R.id.item_name);
        final Item item = scodashService.getCurrentItem(index, sorting);
        nameTextView.setText(item.getName());
        // set item score
        TextView scoreTextView = itemLineView.findViewById(R.id.score_text);
        scoreTextView.setText(String.valueOf(item.getScore()));

        final Dashboard currentDashboard = scodashService.getCurrentDashboard();

        if (!TextUtils.isEmpty(currentDashboard.getWriteHash())) {
            View incBtnView = itemLineView.findViewById(R.id.inc_btn);
            ((FrameLayout)incBtnView.getParent()).setVisibility(View.VISIBLE);
            incBtnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.setScore(item.getScore() + 1);
                    notifyDataSetChanged();
                    scodashService.sendUpdateDataToServer(
                            new DashboardUpdateDto(
                                    "increment", item.getId(), currentDashboard.getWriteHash(), new Date().getTimezoneOffset()));
                }
            });
            View decBtnView = itemLineView.findViewById(R.id.dec_btn);
            ((FrameLayout)decBtnView.getParent()).setVisibility(View.VISIBLE);
            decBtnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getScore() > 0) {
                        item.setScore(item.getScore() - 1);
                        notifyDataSetChanged();
                        scodashService.sendUpdateDataToServer(
                                new DashboardUpdateDto(
                                        "decrement", item.getId(), currentDashboard.getWriteHash(), new Date().getTimezoneOffset()));
                    }
                }
            });
        }

        FlexboxLayout commasView = itemLineView.findViewById(R.id.commas);
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
        return scodashService.getCurrentDashboardItemCount();
    }

    public Sorting getSorting() {
        return sorting;
    }

    public void setSorting(Sorting sorting) {
        this.sorting = sorting;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView itemView;

        public ViewHolder(@NonNull CardView itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
