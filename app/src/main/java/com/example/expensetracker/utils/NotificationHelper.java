package com.example.expensetracker.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.expensetracker.R;

public class NotificationHelper {

    // 🔔 LOCAL notification (Budget warning)
    public static void showBudgetWarning(Context context, String remark) {
        showNotification(
                context,
                "Budget Warning!",
                "You have exceeded your budget for \"" + remark + "\""
        );
    }

    // 🔔 PUSH notification (FCM)
    public static void showPushNotification(
            Context context,
            String title,
            String body
    ) {
        showNotification(context, title, body);
    }

    // 🔒 Internal shared method
    private static void showNotification(
            Context context,
            String title,
            String body
    ) {
        // Android 13+ permission check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "budget_channel")
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat.from(context)
                .notify((int) System.currentTimeMillis(), builder.build());
    }
}
