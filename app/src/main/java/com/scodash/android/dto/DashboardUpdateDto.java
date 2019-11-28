package com.scodash.android.dto;

public class DashboardUpdateDto {

    private String operation;
    private int itemId;
    private String hash;
    private int tzOffset;

    public DashboardUpdateDto(String operation, int itemId, String hash, int tzOffset) {
        this.operation = operation;
        this.itemId = itemId;
        this.hash = hash;
        this.tzOffset = tzOffset;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getTzOffset() {
        return tzOffset;
    }

    public void setTzOffset(int tzOffset) {
        this.tzOffset = tzOffset;
    }
}
