package com.scodash.android;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.LinearLayout;

public class NewDashboardActivity extends AppCompatActivity {

    public static final int NAME_TAB_POSITION = 0;
    public static final int ITEMS_TAB_POSITION = 1;
    public static final int AUTHOR_TAB_POSITION = 2;

    private ViewPager viewPager;
    private StepsPageAdapter stepsPageAdapter;
    private DashboardItemsFragment dashboardItemsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        EditText nameEditText = findViewById(R.id.name);
        NewDashboard.getInstance().setName(nameEditText.toString());
        EditText descriptionEditText = findViewById(R.id.description);
        NewDashboard.getInstance().setDescription(descriptionEditText.toString());
        viewPager.setCurrentItem(ITEMS_TAB_POSITION);
    }

    public void itemsStepPrevButtonClicked(View view) {
        viewPager.setCurrentItem(NAME_TAB_POSITION);
    }

    public void itemsStepNextButtonClicked(View view) {
        viewPager.setCurrentItem(AUTHOR_TAB_POSITION);
    }

    public void authorStepPrevButtonClicked(View view) {
        viewPager.setCurrentItem(ITEMS_TAB_POSITION);
    }

    public void authorStepNextButtonClicked(View view) {
        // TODO finish dashboard creation
    }

    public void addItem(View view) {
        EditText editText = findViewById(R.id.new_item_name);
        NewDashboard.getInstance().addItem(editText.getText().toString());
        editText.setText("");
        dashboardItemsFragment.notifyItemsChanged();
    }

    public void removeItem(View view) {
        ViewParent viewParent = view.getParent();
        EditText itemEdit = ((LinearLayout)viewParent).findViewById(R.id.item_name);
        NewDashboard.getInstance().removeItem(itemEdit.getText().toString());
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
