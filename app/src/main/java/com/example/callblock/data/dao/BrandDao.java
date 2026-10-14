package com.example.callblock.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.callblock.data.entity.Brand;
import com.example.callblock.data.entity.BrandWithNumbers;

import java.util.List;

@Dao
public interface BrandDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Brand brand);

    @Update
    void update(Brand brand);

    @Delete
    void delete(Brand brand);

    @Query("SELECT * FROM brands ORDER BY id DESC")
    LiveData<List<Brand>> getAllBrands();

    @Transaction
    @Query("SELECT * FROM brands ORDER BY id DESC")
    LiveData<List<BrandWithNumbers>> getAllBrandsWithNumbers();

    @Transaction
    @Query("SELECT * FROM brands WHERE id = :id LIMIT 1")
    LiveData<BrandWithNumbers> getBrandWithNumbersById(long id);

    @Transaction
    @Query("SELECT * FROM brands WHERE is_enabled = 1")
    List<BrandWithNumbers> getEnabledBrandsWithNumbersSync();

    @Query("SELECT * FROM brands WHERE id = :id LIMIT 1")
    Brand getBrandByIdSync(long id);

    @Query("UPDATE brands SET is_enabled = :isEnabled WHERE id = :brandId")
    void setBrandEnabled(long brandId, boolean isEnabled);
}
