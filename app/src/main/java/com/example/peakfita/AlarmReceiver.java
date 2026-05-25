package com.example.peakfita;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "השעון עובד! מנסה להציג התראה...", Toast.LENGTH_LONG).show();
        // מקבלים את שם האימון שנשלח מהמסך הקודם
        String workoutTitle = intent.getStringExtra("workoutTitle");
        if (workoutTitle == null) {
            workoutTitle = "Workout";
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "workout_reminders";

        // יצירת ערוץ התרעות - חובה החל מאנדרואיד 8 (Oreo) ומעלה
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Workout Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        // בניית ההתרעה (Notification) באנגלית
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // אייקון שעון מובנה
                .setContentTitle("Time to Sweat! 💪")
                .setContentText("Your workout '" + workoutTitle + "' is starting in 10 minutes!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // שליחת ההתרעה למכשיר
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}