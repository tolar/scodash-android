package com.scodash.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scodash.android.R;
import com.scodash.android.dto.Dashboard;
import com.scodash.android.services.impl.CurrentDashboardChangeListener;
import com.scodash.android.services.impl.ScodashService;
import com.scodash.android.services.impl.Sorting;

import org.joda.time.format.DateTimeFormat;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.Typeface.BOLD;

public class DashboardActivity extends ScodashActivity implements CurrentDashboardChangeListener {

    // launching intent properties
    public static final String HASH = "hash";

    @Inject
    ScodashService scodashService;

    private DashboardItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        setContentView(R.layout.activity_dashboard);
        setupToolbar();

        final String hash = getHashFromIntent();
        Call<Dashboard> call = scodashService.getRemoteDashboardByHash(hash);
        call.enqueue(new Callback<Dashboard>() {
            @Override
            public void onResponse(Call<Dashboard> call, Response<Dashboard> response) {
                if (response.isSuccessful()) {
                    handleDashboardFromServer(response.body());
                } else {
                    redirectToMainActivity();
                }
            }

            @Override
            public void onFailure(Call<Dashboard> call, Throwable t) {
                call.cancel();
                redirectToMainActivity();
            }
        });
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void handleDashboardFromServer(Dashboard dashboard) {

        scodashService.populateAccessHash(dashboard);
        scodashService.putHashToLocalStorage(getScodashSharedPreferences(), dashboard.getHash());
        scodashService.setCurrentDashboard(dashboard);
        scodashService.connectToDashboardOnServer(dashboard.getHash());


        updateTextViews(dashboard);

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
        itemsAdapter = new DashboardItemsAdapter(scodashService);
        itemsRecyler.setAdapter(itemsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        itemsRecyler.setLayoutManager(layoutManager);

        scodashService.addCurrentDashboardChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        scodashService.removeCurrentDashboardChangeListener(this);
        super.onDestroy();
    }

    private void updateTextViews(Dashboard dashboard) {
        TextView nameView = findViewById(R.id.dashboard_name);
        nameView.setText(dashboard.getName());

        TextView descriptionView = findViewById(R.id.dashboard_description);
        descriptionView.setText(dashboard.getDescription());

        TextView footerLine1View = findViewById(R.id.dashboard_footer_line1);
        SpannableString footerLine1Str = prepareFooterLine1Text(dashboard);
        footerLine1View.setText(footerLine1Str);

        TextView footerLine2View = findViewById(R.id.dashboard_footer_line2);
        String footerLine2Str = prepareFooterLine2Text(dashboard);
        footerLine2View.setText(footerLine2Str);
    }

    private SpannableString prepareFooterLine1Text(Dashboard dashboard) {
        String firstPartText = "Dashboard ";
        String dashboardName = dashboard.getName() == null ? "" : dashboard.getName();
        String secondPartText = " created by ";
        String dashboardAuthor = dashboard.getOwnerName() == null ? "" :  dashboard.getOwnerName();
        String thirdPart = "";
        if (dashboard.getCreated() != null) {
            thirdPart = " on " + dashboard.getCreated().toString(DateTimeFormat.shortDate());
        }
        String textBeforeOwnerName = firstPartText + dashboardName + secondPartText;
        SpannableString str = new SpannableString(textBeforeOwnerName + dashboardAuthor + thirdPart);
        str.setSpan(new StyleSpan(BOLD), firstPartText.length(), firstPartText.length() + dashboardName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new StyleSpan(BOLD), textBeforeOwnerName.length(), textBeforeOwnerName.length() + dashboardAuthor.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }

    private String prepareFooterLine2Text(Dashboard dashboard) {
        if (dashboard.getUpdated() != null) {
            return "Last update on " + dashboard.getUpdated().toString(DateTimeFormat.shortDateTime());
        }
        return "";
    }

    private String getHashFromIntent() {
        Intent intent = getIntent();

        Intent appLinkIntent = getIntent();
        Uri appLinkData = appLinkIntent.getData();

        String hash;
        if (appLinkData != null && appLinkData.getPathSegments() != null && appLinkData.getPathSegments().size() >= 2) {
            hash = appLinkData.getPathSegments().get(1);
        } else {
            hash = intent.getStringExtra(DashboardActivity.HASH);
        }
        return hash;
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

    @Override
    public void currentDashboardChanged(final Dashboard dashboard) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemsAdapter.notifyDataSetChanged();
                updateTextViews(dashboard);
            }
        });
    }
}
