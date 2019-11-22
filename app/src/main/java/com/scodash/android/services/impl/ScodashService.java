package com.scodash.android.services.impl;

import android.util.Log;

import com.scodash.android.dto.Dashboard;
import com.scodash.android.services.dto.DashboardId;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ScodashService {

    // TODO docasne jen jedna nastenka
    private Dashboard dashboard;

    @Inject
    public ScodashService() {
    }

    public DashboardId createDashboard(Dashboard newDashboard) {
        Log.d("ScodashService", "createDashboard");
        DashboardId dashboardId = new DashboardId("aaaaa", "bbbbb");
        dashboard = newDashboard;
        return dashboardId;
    }

    public Dashboard getDashboard(DashboardId dashboardId) {
        return dashboard;
    }
}
