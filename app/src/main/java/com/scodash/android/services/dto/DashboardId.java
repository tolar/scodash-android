package com.scodash.android.services.dto;

public class DashboardId {

    private String writeHash;
    private String readHash;

    public DashboardId() {
    }

    public String getWriteHash() {
        return writeHash;
    }

    public void setWriteHash(String writeHash) {
        this.writeHash = writeHash;
    }

    public String getReadHash() {
        return readHash;
    }

    public void setReadHash(String readHash) {
        this.readHash = readHash;
    }
}
