package com.example.expensetracker;

import android.util.Log;

import com.example.expensetracker.utils.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM_DEBUG";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);

        Log.d(TAG, "FCM message received");

        if (message.getNotification() != null) {
            String title = message.getNotification().getTitle();
            String body = message.getNotification().getBody();

            // Reuse your existing notification system
            NotificationHelper.showPushNotification(
                    getApplicationContext(),
                    title != null ? title : "Expense Tracker",
                    body != null ? body : "You have a new notification"
            );

        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d(TAG, "FCM Token: " + token);
    }
}
