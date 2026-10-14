package com.example.callblock.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.callblock.data.entity.BlockedNumber;

import java.util.List;

@Dao
public interface BlockedNumberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BlockedNumber> numbers);

    @Query("DELETE FROM blocked_numbers WHERE brand_id = :brandId")
    void deleteByBrandId(long brandId);

    @Query("SELECT * FROM blocked_numbers WHERE brand_id = :brandId")
    LiveData<List<BlockedNumber>> getNumbersByBrandId(long brandId);

    @Query("SELECT * FROM blocked_numbers WHERE brand_id = :brandId")
    List<BlockedNumber> getNumbersByBrandIdSync(long brandId);
}
