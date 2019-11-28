package com.scodash.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scodash.android.R;
import com.scodash.android.dto.Dashboard;
import com.scodash.android.services.impl.LocalDashboardService;
import com.scodash.android.services.impl.RemoteDashboardService;
import com.scodash.android.services.impl.Sorting;

import java.text.DateFormat;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

import static android.graphics.Typeface.BOLD;

public class DashboardActivity extends AppCompatActivity {

    // launching intent properties
    public static final String HASH = "hash";

    @Inject
    RemoteDashboardService remoteDashboardService;

    @Inject
    LocalDashboardService localDashboardService;

    private DashboardItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        setContentView(R.layout.activity_dashboard);

        setupToolbar();
        Dashboard dashboard = getDashboardFromIntent();

        TextView nameView = findViewById(R.id.dashboard_name);
        nameView.setText(dashboard.getName());

        TextView descriptionView = findViewById(R.id.dashboard_description);
        descriptionView.setText(dashboard.getDescription());

        RadioGroup radioGroupView = findViewById(R.id.sort);
        radioGroupView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.sort_score) {
                    itemsAdapter.setSorting(Sorting.SCORE);
                } else {
                    itemsAdapter.setSorting(Sorting.AZ);
                }
                itemsAdapter.notifyDataSetChanged();
            }
        });


        RecyclerView itemsRecyler = findViewById(R.id.items);
        itemsAdapter = new DashboardItemsAdapter(localDashboardService);
        itemsRecyler.setAdapter(itemsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        itemsRecyler.setLayoutManager(layoutManager);

        TextView footerLine1View = findViewById(R.id.dashboard_footer_line1);
        SpannableString footerLine1Str = prepareFooterLine1Text(dashboard);
        footerLine1View.setText(footerLine1Str);

        TextView footerLine2View = findViewById(R.id.dashboard_footer_line2);
        String footerLine2Str = prepareFooterLine2Text(dashboard);
        footerLine2View.setText(footerLine2Str);

    }

    private SpannableString prepareFooterLine1Text(Dashboard dashboard) {
        String firstPartText = "Dashboard ";
        String dashboardName = dashboard.getName();
        String secondPartText = " created by ";
        String dashboardAuthor = dashboard.getAuthorName();
        String thirdPart = " on " + DateFormat.getDateInstance().format(dashboard.getCreated());
        SpannableString str = new SpannableString(firstPartText + dashboardName + secondPartText + dashboardAuthor + thirdPart);
        str.setSpan(new StyleSpan(BOLD), firstPartText.length(), firstPartText.length() + dashboardName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }

    private String prepareFooterLine2Text(Dashboard dashboard) {
        return "Last update on " + DateFormat.getDateTimeInstance().format(dashboard.getUpdated());
    }

    private Dashboard getDashboardFromIntent() {
        Intent intent = getIntent();
        String hash = intent.getStringExtra(DashboardActivity.HASH);
        return localDashboardService.getDashboardByHash(hash);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.logo);
        actionBar.setDisplayUseLogoEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
