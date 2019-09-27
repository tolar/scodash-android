package com.scodash.android.services;

import com.scodash.android.dto.Dashboard;
import com.scodash.android.services.dto.DashboardId;

public interface ScodashService {

    DashboardId createDashboard(Dashboard newDashboard);

    Dashboard getDashboard(DashboardId dashboardId);
}
