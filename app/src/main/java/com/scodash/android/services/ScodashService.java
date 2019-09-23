package com.scodash.android.services;

import com.scodash.android.activities.NewDashboard;
import com.scodash.android.services.dto.DashboardId;

public interface ScodashService {

    DashboardId createDashboard(NewDashboard newDashboard);
}
