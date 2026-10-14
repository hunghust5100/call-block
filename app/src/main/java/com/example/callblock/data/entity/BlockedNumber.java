package com.example.callblock.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "blocked_numbers",
    foreignKeys = @ForeignKey(
        entity = Brand.class,
        parentColumns = "id",
        childColumns = "brand_id",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("brand_id")}
)
public class BlockedNumber {

    public static final String MATCH_EXACT = "EXACT";
    public static final String MATCH_PREFIX = "PREFIX";

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "brand_id")
    private long brandId;

    @NonNull
    @ColumnInfo(name = "number_pattern")
    private String numberPattern;

    @NonNull
    @ColumnInfo(name = "match_type")
    private String matchType;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public BlockedNumber(long brandId, @NonNull String numberPattern, @NonNull String matchType, long createdAt) {
        this.brandId = brandId;
        this.numberPattern = numberPattern;
        this.matchType = matchType;
        this.createdAt = createdAt;
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

    @NonNull
    public String getNumberPattern() {
        return numberPattern;
    }

    public void setNumberPattern(@NonNull String numberPattern) {
        this.numberPattern = numberPattern;
    }

    @NonNull
    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(@NonNull String matchType) {
        this.matchType = matchType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
