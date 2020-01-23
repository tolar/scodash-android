package com.scodash.android.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scodash.android.R;
import com.scodash.android.dto.Dashboard;
import com.scodash.android.services.impl.DashboardChangeListener;
import com.scodash.android.services.impl.ScodashService;
import com.scodash.android.services.impl.Sorting;
import com.scodash.android.utils.NetworkUtility;

import org.joda.time.format.ISODateTimeFormat;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.Typeface.BOLD;

public class DashboardActivity extends ScodashActivity implements DashboardChangeListener {

    // launching intent properties
    public static final String HASH = "hash";

    @Inject
    ScodashService scodashService;

    private DashboardItemsAdapter itemsAdapter;

    private String shareUrl;

    private boolean offline;
    private String hash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        offline = !NetworkUtility.isNetWorkAvailableNow(this);

        int layoutId;
        if (offline) {
            layoutId = R.layout.no_internet;
        } else {
            layoutId = R.layout.activity_dashboard;
        }

        setContentView(layoutId);
        setupToolbar();

        loadDataAndShow();
    }


    @Override
    protected void onStart() {
        super.onStart();
        loadDataAndShow();
    }

    private void loadDataAndShow() {
        offline = !NetworkUtility.isNetWorkAvailableNow(this);
        if (offline) {
            showNoInternetSnackbarWithRetryAction();
        } else {
            showDashboard();
        }
    }

    private void showDashboard() {

        itemsAdapter = new DashboardItemsAdapter(scodashService, this);
        RecyclerView itemsRecyler = findViewById(R.id.items);
        itemsRecyler.setAdapter(itemsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        itemsRecyler.setLayoutManager(layoutManager);

        hash = getHashFromIntent();
        Call<Dashboard> call = scodashService.getRemoteDashboardByHash(hash);
        call.enqueue(new Callback<Dashboard>() {
            @Override
            public void onResponse(Call<Dashboard> call, Response<Dashboard> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleDashboardFromServer(response.body());
                        }
                    });
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
        scodashService.addLoadedDashboard(dashboard.getHash(), dashboard);
        scodashService.connectToDashboardOnServer(dashboard.getHash());


        updateTextViews(dashboard);
        itemsAdapter.notifyDataSetChanged();

        RadioGroup radioGroupView = findViewById(R.id.sort);
        radioGroupView.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.sort_score) {
                itemsAdapter.setSorting(Sorting.SCORE);
            } else {
                itemsAdapter.setSorting(Sorting.AZ);
            }
            itemsAdapter.notifyDataSetChanged();
        });

        scodashService.addDashboardChangeListener(hash, this);
    }

    @Override
    protected void onDestroy() {
        Log.d("Dashboard activity", "onDestroy" );
        scodashService.removeCurrentDashboardChangeListener(hash,this);
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
            thirdPart = " on " + ISODateTimeFormat.date().print(dashboard.getCreated());
        }
        String textBeforeOwnerName = firstPartText + dashboardName + secondPartText;
        SpannableString str = new SpannableString(textBeforeOwnerName + dashboardAuthor + thirdPart);
        str.setSpan(new StyleSpan(BOLD), firstPartText.length(), firstPartText.length() + dashboardName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        str.setSpan(new StyleSpan(BOLD), textBeforeOwnerName.length(), textBeforeOwnerName.length() + dashboardAuthor.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }

    private String prepareFooterLine2Text(Dashboard dashboard) {
        if (dashboard.getUpdated() != null) {
            return "Last update on " + ISODateTimeFormat.date().print(dashboard.getUpdated()) + " " + ISODateTimeFormat.hourMinuteSecond().print(dashboard.getUpdated());
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
        actionBar.setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (offline) {
            getMenuInflater().inflate(R.menu.menu_dashboard_no_internet, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_dashboard:
                startNewDashboardActivity();
                return true;
            case R.id.action_go_home:
                startMainActivity();
                return true;
            case R.id.action_share_dashboard:
                shareUrl = scodashService.getReadonlyDashboardUrl(hash);
                if (TextUtils.isEmpty(scodashService.getLoadedDashboard(hash).getWriteHash())) {
                    startSharing();
                } else {
                    showShareDialog();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showShareDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Share the dashboard");
        shareUrl = scodashService.getReadonlyDashboardUrl(hash);
        dialog.setSingleChoiceItems(R.array.share_options,0, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shareUrl = which == 0 ? scodashService.getReadonlyDashboardUrl(hash) : scodashService.getWriteDashboardUrl(hash);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startSharing();
            }
        });
        dialog.show();
    }

    private void startSharing() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void startNewDashboardActivity() {
        Intent intent = new Intent(this, NewDashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public void dashboardChanged(final Dashboard dashboard) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemsAdapter.notifyDataSetChanged();
                updateTextViews(dashboard);
            }
        });
    }

    public String getHash() {
        return hash;
    }
}
