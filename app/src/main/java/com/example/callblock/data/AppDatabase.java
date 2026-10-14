package com.example.callblock.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.callblock.data.dao.BlockedLogDao;
import com.example.callblock.data.dao.BlockedNumberDao;
import com.example.callblock.data.dao.BrandDao;
import com.example.callblock.data.entity.BlockedLog;
import com.example.callblock.data.entity.BlockedNumber;
import com.example.callblock.data.entity.Brand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
    entities = {Brand.class, BlockedNumber.class, BlockedLog.class},
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "brand_block_db";
    private static volatile AppDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public abstract BrandDao brandDao();
    public abstract BlockedNumberDao blockedNumberDao();
    public abstract BlockedLogDao blockedLogDao();

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DB_NAME
                    )
                    .addCallback(sRoomDatabaseCallback)
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                if (INSTANCE != null) {
                    BrandDao brandDao = INSTANCE.brandDao();
                    BlockedNumberDao numberDao = INSTANCE.blockedNumberDao();

                    long now = System.currentTimeMillis();

                    // Seed Brand 1 (Tín dụng & Vay tiêu dùng SPAM)
                    Brand brand1 = new Brand("Tín dụng & Vay tiêu dùng SPAM", "Các số gọi điện chào mời vay vốn, tín dụng đen", true, now);
                    long b1Id = brandDao.insert(brand1);

                    List<BlockedNumber> b1Numbers = new ArrayList<>();
                    b1Numbers.add(new BlockedNumber(b1Id, "02473008899", BlockedNumber.MATCH_EXACT, now));
                    b1Numbers.add(new BlockedNumber(b1Id, "02473009900", BlockedNumber.MATCH_EXACT, now));
                    b1Numbers.add(new BlockedNumber(b1Id, "02899998888", BlockedNumber.MATCH_EXACT, now));
                    b1Numbers.add(new BlockedNumber(b1Id, "190066", BlockedNumber.MATCH_PREFIX, now));
                    numberDao.insertAll(b1Numbers);

                    // Seed Brand 2 (Bất động sản Quảng cáo)
                    Brand brand2 = new Brand("Bất động sản Quảng cáo", "Quảng cáo đất nền, căn hộ, biệt thự nghỉ dưỡng", true, now);
                    long b2Id = brandDao.insert(brand2);

                    List<BlockedNumber> b2Numbers = new ArrayList<>();
                    b2Numbers.add(new BlockedNumber(b2Id, "0901234567", BlockedNumber.MATCH_EXACT, now));
                    b2Numbers.add(new BlockedNumber(b2Id, "0912345678", BlockedNumber.MATCH_EXACT, now));
                    numberDao.insertAll(b2Numbers);

                    // Seed Brand 3 (Chứng khoán Quốc tế)
                    Brand brand3 = new Brand("Chứng khoán Quốc tế", "Các sàn chứng khoán quốc tế, forex tự xưng", true, now);
                    long b3Id = brandDao.insert(brand3);

                    List<BlockedNumber> b3Numbers = new ArrayList<>();
                    b3Numbers.add(new BlockedNumber(b3Id, "02871000000", BlockedNumber.MATCH_PREFIX, now));
                    numberDao.insertAll(b3Numbers);
                }
            });
        }
    };
}
