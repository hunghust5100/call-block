package com.example.callblock;

import android.app.Application;

import com.example.callblock.data.AppDatabase;
import com.example.callblock.utils.NotificationHelper;

public class BrandBlockApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Notification Channels
        NotificationHelper.createNotificationChannel(this);
        // Initialize Database
        AppDatabase.getInstance(this);
    }
}
