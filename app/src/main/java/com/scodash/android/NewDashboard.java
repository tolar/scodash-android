package com.scodash.android;

import java.util.LinkedList;
import java.util.List;

public class NewDashboard {

    private static NewDashboard instance;

    public static NewDashboard getInstance() {
        if (instance == null) {
            instance = new NewDashboard();
        }
        return instance;
    }

    private List<String> items = new LinkedList<>();

    void reset() {
        this.items.clear();
    }


    void addItem(String item) {
        this.items.add(item);
    }

    public List<String> getItems() {
        return items;
    }
}
