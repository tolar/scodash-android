package com.scodash.android.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.scodash.android.services.impl.CustomDateTimeSerializer;

import org.joda.time.DateTime;

import java.util.Set;
import java.util.TreeSet;

public class Dashboard {

    private String id;

    private Set<Item> items = new TreeSet<>();

    private String name;

    private String description;

    private String ownerName;

    private String ownerEmail;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    private DateTime created;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    private DateTime updated;

    private String hash;

    private String writeHash;

    private String readonlyHash;

    public boolean deleted;

    public DateTime getCreated() {
        return created;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public DateTime getUpdated() {
        return updated;
    }

    public void setUpdated(DateTime updated) {
        this.updated = updated;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public void reset() {
        items.clear();
    }

    public void addItem(String name) {
        items.add(new Item(name));
    }

    public void removeItem(String name) {
        items.remove(new Item(name));
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getWriteHash() {
        return writeHash;
    }

    public void setWriteHash(String writeHash) {
        this.writeHash = writeHash;
    }

    public String getReadonlyHash() {
        return readonlyHash;
    }

    public void setReadonlyHash(String readonlyHash) {
        this.readonlyHash = readonlyHash;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
