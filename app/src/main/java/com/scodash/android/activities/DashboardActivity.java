package com.scodash.android.activities;

import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
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

    private Dashboard dashboard;

    @Inject
    ScodashService scodashService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        setContentView(R.layout.activity_dashboard);

        setupToolbar();
        dashboard = getDashboardFromIntent();

        TextView nameView = findViewById(R.id.dashboard_name);
        nameView.setText(dashboard.getName());

        TextView descriptionView = findViewById(R.id.dashboard_description);
        descriptionView.setText(dashboard.getDescription());

        RecyclerView itemsRecyler = findViewById(R.id.items);
        itemsRecyler.setAdapter(new DashboardItemsAdapter(dashboard));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        itemsRecyler.setLayoutManager(layoutManager);


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

    public void incScore(View view) {
        Log.d("x", "incItem");
        //dashboard.
    }

    public void decScore(View view) {
        Log.d("x", "decItem");
    }
}
