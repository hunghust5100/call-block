package com.example.callblock.data.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class BrandWithNumbers {

    @Embedded
    public Brand brand;

    @Relation(
        parentColumn = "id",
        entityColumn = "brand_id"
    )
    public List<BlockedNumber> numbers;

    public Brand getBrand() {
        return brand;
    }

    public List<BlockedNumber> getNumbers() {
        return numbers;
    }
}
