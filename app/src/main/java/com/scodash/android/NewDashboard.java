package com.scodash.android;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class NewDashboard {

    private static NewDashboard instance;

    private Set<String> items = new TreeSet<>();

    private String name;

    private String description;

    private String owner;

    private String email;


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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
