package com.example.callblock.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.callblock.data.entity.BlockedLog;

import java.util.List;

@Dao
public interface BlockedLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(BlockedLog log);

    @Query("SELECT * FROM blocked_logs ORDER BY blocked_at DESC")
    LiveData<List<BlockedLog>> getAllLogs();

    @Query("SELECT COUNT(*) FROM blocked_logs")
    LiveData<Integer> getTotalBlockedCount();

    @Query("SELECT COUNT(*) FROM blocked_logs WHERE brand_id = :brandId")
    LiveData<Integer> getBlockedCountByBrand(long brandId);

    @Query("SELECT brand_name FROM blocked_logs GROUP BY brand_id ORDER BY COUNT(*) DESC LIMIT 1")
    LiveData<String> getMostBlockedBrandName();

    @Query("DELETE FROM blocked_logs")
    void clearAllLogs();
}
