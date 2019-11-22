package com.scodash.android.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import com.scodash.android.R;
import com.scodash.android.dto.Dashboard;
import com.scodash.android.services.dto.DashboardId;
import com.scodash.android.services.impl.ScodashService;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class DashboardActivity extends AppCompatActivity {

    public static final String WRITE_HASH = "writeHash";
    public static final String READ_HASH = "readHash";

    @Inject
    ScodashService scodashService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        setupToolbar();
        Dashboard dashboard = getDashboardFromIntent();

        TextView nameView = findViewById(R.id.dashboard_name);
        nameView.setText(dashboard.getName());

        TextView descriptionView = findViewById(R.id.dashboard_description);
        descriptionView.setText(dashboard.getDescription());


}

    private Dashboard getDashboardFromIntent() {
        Intent intent = getIntent();
        String writeHash = intent.getStringExtra(WRITE_HASH);
        String readHash = intent.getStringExtra(READ_HASH);
        Dashboard dashboard = scodashService.getDashboard(new DashboardId(writeHash, readHash));
        return dashboard;
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
