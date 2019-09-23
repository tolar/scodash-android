package com.scodash.android.services.impl;

import android.util.Log;

import com.scodash.android.activities.NewDashboard;
import com.scodash.android.services.ScodashService;
import com.scodash.android.services.dto.DashboardId;

public class ScodashServiceImpl implements ScodashService {

    private static ScodashService scodashService;

    public static ScodashService getInstance() {
        if (scodashService == null) {
            scodashService = new ScodashServiceImpl();
        }
        return scodashService;
    }

    @Override
    public DashboardId createDashboard(NewDashboard newDashboard) {
        Log.d("ScodashService", "createDashboard");
        DashboardId result = new DashboardId();
        result.setReadHash("aabbccdd");
        result.setWriteHash("eeffgghh");
        return result;
    }
}
