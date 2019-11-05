package com.scodash.android.services.impl;

import android.util.Log;

import com.scodash.android.dto.Dashboard;
import com.scodash.android.services.ScodashService;
import com.scodash.android.services.dto.DashboardId;

public class ScodashServiceImpl implements ScodashService {

    private static ScodashService scodashService;

    // TODO docasne jen jedna nastenka
    private Dashboard dashboard;

    public static ScodashService getInstance() {
        if (scodashService == null) {
            scodashService = new ScodashServiceImpl();
        }
        return scodashService;
    }

    @Override
    public DashboardId createDashboard(Dashboard newDashboard) {
        Log.d("ScodashService", "createDashboard");
        DashboardId dashboardId = new DashboardId("aaaaa", "bbbbb");
        dashboard = newDashboard;
        return dashboardId;
    }

    @Override
    public Dashboard getDashboard(DashboardId dashboardId) {
        return dashboard;
    }
}
