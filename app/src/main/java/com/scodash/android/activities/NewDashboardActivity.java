package com.scodash.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.scodash.android.R;
import com.scodash.android.services.dto.DashboardId;
import com.scodash.android.services.impl.ScodashService;

import java.util.Date;

import javax.inject.Inject;

import dagger.android.AndroidInjection;


public class NewDashboardActivity extends AppCompatActivity {

    public static final int NAME_TAB_POSITION = 0;
    public static final int ITEMS_TAB_POSITION = 1;
    public static final int AUTHOR_TAB_POSITION = 2;

    @Inject
    ScodashService scodashService;

    private ViewPager viewPager;
    private StepsPageAdapter stepsPageAdapter;
    private DashboardItemsFragment dashboardItemsFragment;
    private Snackbar noItemSnackbard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        setContentView(R.layout.activity_new_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        stepsPageAdapter = new StepsPageAdapter(getSupportFragmentManager(), this);
        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(stepsPageAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // to disable tab clicking
        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }

        dashboardItemsFragment = new DashboardItemsFragment();

    }

    public void nameStepNextButtonClicked(View view) {
        EditText nameEditText = findViewById(R.id.dashboard_name);
        String name = nameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            TextInputLayout nameInputLayout = findViewById(R.id.name_input_layout);
            nameInputLayout.setError(getString(R.string.dashboard_name_mandatory));
            return;
        }
        NewDashboard.getInstance().setName(name);
        EditText descriptionEditText = findViewById(R.id.dashboard_description);
        String description = descriptionEditText.getText().toString();
        NewDashboard.getInstance().setDescription(description);
        viewPager.setCurrentItem(ITEMS_TAB_POSITION);
    }

    public void itemsStepPrevButtonClicked(View view) {
        dismissNoItemSnackbar();
        viewPager.setCurrentItem(NAME_TAB_POSITION);
    }

    public void itemsStepNextButtonClicked(View view) {
        if (NewDashboard.getInstance().getItems().isEmpty()) {
            getNoItemSnackbar();
            noItemSnackbard.show();
            return;
        }
        viewPager.setCurrentItem(AUTHOR_TAB_POSITION);
    }

    private Snackbar getNoItemSnackbar() {
        if (noItemSnackbard != null) {
            return noItemSnackbard;
        }
        noItemSnackbard = Snackbar.make(findViewById(R.id.new_items), R.string.no_empty_items, Snackbar.LENGTH_INDEFINITE);
        int snackbarTextId = R.id.snackbar_text;
        View snackbarView = noItemSnackbard.getView();
        snackbarView.findViewById(snackbarTextId);
        TextView snackbarTextView = snackbarView.findViewById(snackbarTextId);
        snackbarTextView.setTextColor(getResources().getColor(R.color.colorErrorText));
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorErrorBackground));
        return noItemSnackbard;
    }

    public void authorStepPrevButtonClicked(View view) {
        viewPager.setCurrentItem(ITEMS_TAB_POSITION);
    }

    public void authorCreateDashboardlicked(View view) {

        boolean validationOk = true;
        EditText authorNameEditText = findViewById(R.id.author_name);
        String name = authorNameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            TextInputLayout authorNameInputLayout = findViewById(R.id.author_name_input_layout);
            authorNameInputLayout.setError(getString(R.string.author_name_mandatory));
            validationOk = false;
        }
        EditText authorEmailEditText = findViewById(R.id.author_email);
        String email = authorEmailEditText.getText().toString();
        TextInputLayout authorEmailInputLayout = findViewById(R.id.author_email_input_layout);
        if (TextUtils.isEmpty(email)) {
            authorEmailInputLayout.setError(getString(R.string.author_email_mandatory));
            validationOk = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            authorEmailInputLayout.setError(getString(R.string.not_valid_email));
            validationOk = false;
        }
        if (validationOk) {
            NewDashboard.getInstance().setAuthorName(name);
            NewDashboard.getInstance().setAuthorEmail(email);
            NewDashboard.getInstance().setCreated(new Date());
            NewDashboard.getInstance().setUpdated(new Date());

            DashboardId dashboardId = scodashService.createDashboard(NewDashboard.getInstance());
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra(DashboardActivity.WRITE_HASH, dashboardId.getWriteHash());
            intent.putExtra(DashboardActivity.READ_HASH, dashboardId.getReadHash());
            startActivity(intent);
        }



    }

    public void addItem(View view) {
        EditText editText = findViewById(R.id.new_item_name);
        String item = editText.getText().toString();
        if (!TextUtils.isEmpty(item)) {
            NewDashboard.getInstance().addItem(item);
            editText.setText("");
            dismissNoItemSnackbar();
            dashboardItemsFragment.notifyItemsChanged();
        }
    }

    private void dismissNoItemSnackbar() {
        getNoItemSnackbar().dismiss();
    }

    private void showNoItemSnackbar() {
        getNoItemSnackbar().show();
    }

    public void removeItem(View view) {
        ViewParent viewParent = view.getParent();
        TextView itemToRemove = ((LinearLayout)viewParent).findViewById(R.id.item_name);
        NewDashboard.getInstance().removeItem(itemToRemove.getText().toString());
        dashboardItemsFragment.notifyItemsChanged();
    }

    private class StepsPageAdapter extends FragmentPagerAdapter {

        private final Activity parentActivity;

        public StepsPageAdapter(FragmentManager fm, Activity parentActivity) {
            super(fm);
            this.parentActivity = parentActivity;
        }

        @Nullable
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case NAME_TAB_POSITION:
                    return new DashboardNameFragment();
                case ITEMS_TAB_POSITION:
                    return dashboardItemsFragment;
                case AUTHOR_TAB_POSITION:
                    return new DashboardAuthorFragment();
            }
            return null;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case NAME_TAB_POSITION:
                    return getResources().getText(R.string.dashboard_name_tab);
                case ITEMS_TAB_POSITION:
                    return getResources().getText(R.string.dashboard_items_tab);
                case AUTHOR_TAB_POSITION:
                    return getResources().getText(R.string.dashboard_author_tab);
            }
            return null;

        }

        @Override
        public int getCount() {
            return 3;
        }


    }
}
