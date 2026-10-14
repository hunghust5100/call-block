package com.example.callblock.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.callblock.R;
import com.example.callblock.ui.main.MainActivity;

public class NotificationHelper {

    public static final String CHANNEL_ID = "brand_block_channel";
    private static final String CHANNEL_NAME = "Thống kê & Chặn SPAM";
    private static final String CHANNEL_DESC = "Thông báo khi hệ thống tự động chặn cuộc gọi quấy rối theo thương hiệu";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void showBlockedNotification(Context context, String brandName, String phoneNumber) {
        Intent openAppIntent = new Intent(context, MainActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String displayPhone = PhoneNumberUtils.formatDisplay(phoneNumber);
        String title = "Đã tự động chặn cuộc gọi SPAM";
        String content = "Thương hiệu: " + brandName + " (Số: " + displayPhone + "). Cuộc gọi đã bị ngắt ngầm không đổ chuông.";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(0, "Xem chi tiết", pendingIntent);

        try {
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            int notificationId = (int) System.currentTimeMillis();
            manager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
