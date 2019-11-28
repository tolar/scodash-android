package com.scodash.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scodash.android.R;
import com.scodash.android.services.impl.LocalDashboardService;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class MainActivity extends AppCompatActivity {

    private RecentDashboardsAdapter recentDashboardsAdapter;

    @Inject
    LocalDashboardService localDashboardService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView itemsRecyler = findViewById(R.id.recents);
        recentDashboardsAdapter = new RecentDashboardsAdapter(getApplicationContext(), localDashboardService);
        itemsRecyler.setAdapter(recentDashboardsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        itemsRecyler.setLayoutManager(layoutManager);

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

    private void startNewDashboardActivity() {
        Intent intent = new Intent(this, NewDashboardActivity.class);
        startActivity(intent);
    }
}
