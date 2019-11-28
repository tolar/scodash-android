package com.scodash.android.dto;

import java.util.Objects;

public class Item implements Comparable<Item> {

    private int id;
    private String name;
    private int score;

    public Item(String name) {
        this.name = name;
        this.score = 0;
    }

    /**
     * Default constructor due to JSON deserialization.
     */
    public Item() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return name.equals(item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(Item o) {
        return this.name.compareTo(o.getName());
    }
}
