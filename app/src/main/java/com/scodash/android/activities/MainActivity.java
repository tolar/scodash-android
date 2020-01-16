package com.scodash.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scodash.android.R;
import com.scodash.android.services.impl.ScodashService;
import com.scodash.android.utils.NetworkUtility;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class MainActivity extends ScodashActivity {

    private RecentDashboardsAdapter recentDashboardsAdapter;

    @Inject
    ScodashService scodashService;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        boolean offline = !NetworkUtility.isNetWorkAvailableNow(this);

        int layoutId = R.layout.activity_main;
        if (offline) {
            layoutId =  R.layout.no_internet;
        }
        setContentView(layoutId);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle("");

        if (offline) {
            showNoInternetSnackbarWithRetryAction();
        } else {
            showRecents();
        }
    }

    private void showRecents() {
        if (scodashService.getHashesFromLocalStorage(getScodashSharedPreferences()).size() > 0) {

            RecyclerView itemsRecyler = findViewById(R.id.recents);
            recentDashboardsAdapter = new RecentDashboardsAdapter(getApplicationContext(), scodashService, this);
            itemsRecyler.setAdapter(recentDashboardsAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            itemsRecyler.setLayoutManager(layoutManager);

            ViewGroup intro_container = findViewById(R.id.intro_container);
            intro_container.removeAllViews();
            toolbar.setVisibility(View.VISIBLE);
        } else {
            ViewGroup recents_container = findViewById(R.id.recents_container);
            recents_container.removeAllViews();
            toolbar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_dashboard:
                startNewDashboardActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClickAdd(View view) {
        startNewDashboardActivity();
    }

    public void onClickStart(View view) {
        startNewDashboardActivity();
    }

    private void startNewDashboardActivity() {
        Intent intent = new Intent(this, NewDashboardActivity.class);
        startActivity(intent);
    }


}
