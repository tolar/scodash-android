package com.scodash.android.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.scodash.android.R;
import com.scodash.android.dto.Dashboard;
import com.scodash.android.services.impl.ScodashService;
import com.scodash.android.utils.NetworkUtility;

import org.joda.time.DateTime;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewDashboardActivity extends ScodashActivity {

    public static final int NAME_TAB_POSITION = 0;
    public static final int ITEMS_TAB_POSITION = 1;
    public static final int AUTHOR_TAB_POSITION = 2;

    @Inject
    ScodashService scodashService;

    private ViewPager viewPager;
    private StepsPageAdapter stepsPageAdapter;
    private DashboardItemsFragment dashboardItemsFragment;
    private Snackbar noItemSnackbard;
    private Snackbar noInternetSnackbar;

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
            tabStrip.getChildAt(i).setOnTouchListener((v, event) -> true);
        }

        dashboardItemsFragment = new DashboardItemsFragment();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_go_home:
                startMainActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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
            showNoItemSnackbar();
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


    private Snackbar getNoInternetConnectionSnackbar() {
        if (noInternetSnackbar != null) {
            return noInternetSnackbar;
        }
        noInternetSnackbar = Snackbar.make(findViewById(R.id.new_items), R.string.no_internet_later, Snackbar.LENGTH_INDEFINITE);
        int snackbarTextId = R.id.snackbar_text;
        View snackbarView = noInternetSnackbar.getView();
        snackbarView.findViewById(snackbarTextId);
        TextView snackbarTextView = snackbarView.findViewById(snackbarTextId);
        snackbarTextView.setTextColor(getResources().getColor(R.color.colorErrorText));
        noInternetSnackbar.setActionTextColor(getResources().getColor(R.color.colorPureBlack));
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorErrorBackground));
        return noInternetSnackbar;
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

            boolean offline = !NetworkUtility.isNetWorkAvailableNow(this);
            if (offline) {
                showNoInternetSnackbar();
                return;
            }

            NewDashboard.getInstance().setOwnerName(name);
            NewDashboard.getInstance().setOwnerEmail(email);
            NewDashboard.getInstance().setCreated(new DateTime());
            NewDashboard.getInstance().setUpdated(new DateTime());

            Activity thisActivity = this;
            Call<Dashboard> call = scodashService.createDashboard(NewDashboard.getInstance());
            call.enqueue(new Callback<Dashboard>() {
                @Override
                public void onResponse(Call<Dashboard> call, Response<Dashboard> response) {
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(thisActivity, DashboardActivity.class);
                        intent.putExtra(DashboardActivity.HASH, response.body().getWriteHash());
                        thisActivity.startActivity(intent);
                    }
                    // TODO doimplementovat
                }

                @Override
                public void onFailure(Call<Dashboard> call, Throwable t) {
                    // TODO doimplementovat
                }
            });
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
        ScrollView scrollView = findViewById(R.id.new_items_scroll_view);
        FrameLayout buttonsLayoutView = findViewById(R.id.bottom_buttons);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, buttonsLayoutView.getBottom());
            }
        });
    }

    private void dismissNoItemSnackbar() {
        getNoItemSnackbar().dismiss();
    }

    private void showNoItemSnackbar() {
        getNoItemSnackbar().show();
    }

    private void showNoInternetSnackbar() {
        getNoInternetConnectionSnackbar().show();
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
