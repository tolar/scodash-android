package com.scodash.android.dto;

public class HashNameTuple {

    private String hash;
    private String name;

    public HashNameTuple(String hash, String name) {
        this.hash = hash;
        this.name = name;
    }

    public String getHash() {
        return hash;
    }

    public String getName() {
        return name;
    }

}
