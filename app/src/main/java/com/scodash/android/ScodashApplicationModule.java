package com.scodash.android;

import com.scodash.android.activities.DashboardActivity;
import com.scodash.android.activities.NewDashboardActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ScodashApplicationModule {

    @ContributesAndroidInjector
    abstract NewDashboardActivity contributeNewDashboardActivityInjector();

    @ContributesAndroidInjector
    abstract DashboardActivity contributeDashboardActivityInjector();



}
