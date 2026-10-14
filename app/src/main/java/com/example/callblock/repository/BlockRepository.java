package com.example.callblock.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.callblock.data.AppDatabase;
import com.example.callblock.data.dao.BlockedLogDao;
import com.example.callblock.data.dao.BlockedNumberDao;
import com.example.callblock.data.dao.BrandDao;
import com.example.callblock.data.entity.BlockedLog;
import com.example.callblock.data.entity.BlockedNumber;
import com.example.callblock.data.entity.Brand;
import com.example.callblock.data.entity.BrandWithNumbers;
import com.example.callblock.utils.PhoneNumberUtils;

import java.util.ArrayList;
import java.util.List;

public class BlockRepository {

    private final BrandDao brandDao;
    private final BlockedNumberDao blockedNumberDao;
    private final BlockedLogDao blockedLogDao;

    public BlockRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        this.brandDao = db.brandDao();
        this.blockedNumberDao = db.blockedNumberDao();
        this.blockedLogDao = db.blockedLogDao();
    }

    public LiveData<List<BrandWithNumbers>> getAllBrandsWithNumbers() {
        return brandDao.getAllBrandsWithNumbers();
    }

    public LiveData<BrandWithNumbers> getBrandWithNumbersById(long id) {
        return brandDao.getBrandWithNumbersById(id);
    }

    public LiveData<List<BlockedLog>> getAllBlockedLogs() {
        return blockedLogDao.getAllLogs();
    }

    public LiveData<Integer> getTotalBlockedCount() {
        return blockedLogDao.getTotalBlockedCount();
    }

    public LiveData<Integer> getTotalBlockedCallsCount() {
        return blockedLogDao.getTotalBlockedCallsCount();
    }

    public LiveData<Integer> getTotalBlockedSmsCount() {
        return blockedLogDao.getTotalBlockedSmsCount();
    }

    public LiveData<String> getMostBlockedBrandName() {
        return blockedLogDao.getMostBlockedBrandName();
    }

    public void insertBrandWithNumbers(String name, String description, List<String> patterns, Runnable onComplete) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long now = System.currentTimeMillis();
            Brand brand = new Brand(name, description, true, now);
            long brandId = brandDao.insert(brand);

            List<BlockedNumber> numbers = new ArrayList<>();
            for (String rawPattern : patterns) {
                String normalizedPattern = PhoneNumberUtils.normalize(rawPattern);
                if (normalizedPattern.isEmpty()) continue;

                String matchType = PhoneNumberUtils.isPrefixPattern(rawPattern)
                        ? BlockedNumber.MATCH_PREFIX
                        : BlockedNumber.MATCH_EXACT;

                numbers.add(new BlockedNumber(brandId, normalizedPattern, matchType, now));
            }

            if (!numbers.isEmpty()) {
                blockedNumberDao.insertAll(numbers);
            }

            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    public void updateBrandWithNumbers(long brandId, String name, String description, List<String> patterns, Runnable onComplete) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Brand existing = brandDao.getBrandByIdSync(brandId);
            if (existing != null) {
                existing.setName(name);
                existing.setDescription(description);
                brandDao.update(existing);

                // Delete existing numbers and insert updated numbers
                blockedNumberDao.deleteByBrandId(brandId);

                long now = System.currentTimeMillis();
                List<BlockedNumber> numbers = new ArrayList<>();
                for (String rawPattern : patterns) {
                    String normalizedPattern = PhoneNumberUtils.normalize(rawPattern);
                    if (normalizedPattern.isEmpty()) continue;

                    String matchType = PhoneNumberUtils.isPrefixPattern(rawPattern)
                            ? BlockedNumber.MATCH_PREFIX
                            : BlockedNumber.MATCH_EXACT;

                    numbers.add(new BlockedNumber(brandId, normalizedPattern, matchType, now));
                }

                if (!numbers.isEmpty()) {
                    blockedNumberDao.insertAll(numbers);
                }
            }

            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    public void deleteBrandById(long brandId, Runnable onComplete) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Brand existing = brandDao.getBrandByIdSync(brandId);
            if (existing != null) {
                brandDao.delete(existing);
                blockedNumberDao.deleteByBrandId(brandId);
            }
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    public void setBrandEnabled(long brandId, boolean isEnabled) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            brandDao.setBrandEnabled(brandId, isEnabled);
        });
    }

    public void deleteBrand(Brand brand) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            brandDao.delete(brand);
        });
    }

    public interface BlockCheckCallback {
        void onCheckResult(boolean isBlocked, String brandName, String matchedPattern);
    }

    /**
     * Synchronous check for CallScreeningService running on background thread.
     */
    public void checkAndLogBlockedCallSync(String incomingNumber, BlockCheckCallback callback) {
        String normalizedIncoming = PhoneNumberUtils.normalize(incomingNumber);
        if (normalizedIncoming.isEmpty()) {
            callback.onCheckResult(false, null, null);
            return;
        }

        List<BrandWithNumbers> enabledBrands = brandDao.getEnabledBrandsWithNumbersSync();
        for (BrandWithNumbers brandWithNumbers : enabledBrands) {
            if (!brandWithNumbers.brand.isEnabled()) continue;

            for (BlockedNumber number : brandWithNumbers.numbers) {
                boolean match = false;
                if (BlockedNumber.MATCH_EXACT.equals(number.getMatchType())) {
                    match = normalizedIncoming.equals(number.getNumberPattern());
                } else if (BlockedNumber.MATCH_PREFIX.equals(number.getMatchType())) {
                    match = normalizedIncoming.startsWith(number.getNumberPattern());
                }

                if (match) {
                    long now = System.currentTimeMillis();
                    BlockedLog log = new BlockedLog(
                            brandWithNumbers.brand.getId(),
                            brandWithNumbers.brand.getName(),
                            normalizedIncoming,
                            now,
                            number.getNumberPattern(),
                            BlockedLog.TYPE_CALL
                    );
                    blockedLogDao.insert(log);

                    callback.onCheckResult(true, brandWithNumbers.brand.getName(), number.getNumberPattern());
                    return;
                }
            }
        }

        callback.onCheckResult(false, null, null);
    }
}
