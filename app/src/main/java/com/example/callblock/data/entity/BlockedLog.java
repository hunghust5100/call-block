package com.example.callblock.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "blocked_logs")
public class BlockedLog {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "brand_id")
    private long brandId;

    @ColumnInfo(name = "brand_name")
    private String brandName;

    @ColumnInfo(name = "phone_number")
    private String phoneNumber;

    @ColumnInfo(name = "blocked_at")
    private long blockedAt;

    @ColumnInfo(name = "matched_pattern")
    private String matchedPattern;

    public BlockedLog(long brandId, String brandName, String phoneNumber, long blockedAt, String matchedPattern) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.phoneNumber = phoneNumber;
        this.blockedAt = blockedAt;
        this.matchedPattern = matchedPattern;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBrandId() {
        return brandId;
    }

    public void setBrandId(long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getBlockedAt() {
        return blockedAt;
    }

    public void setBlockedAt(long blockedAt) {
        this.blockedAt = blockedAt;
    }

    public String getMatchedPattern() {
        return matchedPattern;
    }

    public void setMatchedPattern(String matchedPattern) {
        this.matchedPattern = matchedPattern;
    }
}
