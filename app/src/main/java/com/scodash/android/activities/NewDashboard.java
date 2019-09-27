package com.scodash.android.activities;

import com.scodash.android.dto.Dashboard;

import java.util.Set;
import java.util.TreeSet;

public class NewDashboard {

    private static Dashboard instance;

    public static Dashboard getInstance() {
        if (instance == null) {
            instance = new Dashboard();
        }
        return instance;
    }




}
