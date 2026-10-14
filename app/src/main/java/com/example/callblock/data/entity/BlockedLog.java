package com.example.callblock.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "blocked_logs")
public class BlockedLog {

    public static final String TYPE_CALL = "CALL";
    public static final String TYPE_SMS = "SMS";

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

    @ColumnInfo(name = "log_type", defaultValue = "'CALL'")
    private String logType = TYPE_CALL;

    public BlockedLog(long brandId, String brandName, String phoneNumber, long blockedAt, String matchedPattern, String logType) {
        this.brandId = brandId;
        this.brandName = brandName;
        this.phoneNumber = phoneNumber;
        this.blockedAt = blockedAt;
        this.matchedPattern = matchedPattern;
        this.logType = logType != null ? logType : TYPE_CALL;
    }

    @Ignore
    public BlockedLog(long brandId, String brandName, String phoneNumber, long blockedAt, String matchedPattern) {
        this(brandId, brandName, phoneNumber, blockedAt, matchedPattern, TYPE_CALL);
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

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }
}
