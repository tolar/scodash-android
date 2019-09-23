package com.scodash.android.activities;

import java.util.Set;
import java.util.TreeSet;

public class NewDashboard {

    private static NewDashboard instance;

    private Set<String> items = new TreeSet<>();

    private String name;

    private String description;

    private String authorName;

    private String authorEmail;


    public static NewDashboard getInstance() {
        if (instance == null) {
            instance = new NewDashboard();
        }
        return instance;
    }

    void reset() {
        this.items.clear();
    }


    void addItem(String item) {
        this.items.add(item);
    }

    void removeItem(String item) {
        this.items.remove(item);
    }

    public Set<String> getItems() {
        return items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }
}
