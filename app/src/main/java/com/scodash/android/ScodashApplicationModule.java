package com.scodash.android;

import com.scodash.android.activities.DashboardActivity;
import com.scodash.android.activities.DashboardItemsFragment;
import com.scodash.android.activities.MainActivity;
import com.scodash.android.activities.NewDashboardActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ScodashApplicationModule {

    @ContributesAndroidInjector
    abstract NewDashboardActivity contributeNewDashboardActivityInjector();

    @ContributesAndroidInjector
    abstract DashboardActivity contributeDashboardActivityInjector();

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivityInjector();

    @ContributesAndroidInjector
    abstract DashboardItemsFragment contributeDashboardItemsFragmentInjector();





}
