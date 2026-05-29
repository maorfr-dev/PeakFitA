package com.example.peakfita;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import java.util.Calendar;

public class AddWorkoutActivity extends AppCompatActivity {

    private EditText etTitle, etDesc, etTime;
    private Button btnSave;
    private int selectedDay;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        etTitle = findViewById(R.id.etWorkoutTitle);
        etDesc = findViewById(R.id.etWorkoutDesc);
        etTime = findViewById(R.id.etScheduledTime);
        btnSave = findViewById(R.id.btnSaveWorkout);
        selectedDay = getIntent().getIntExtra("dayOfWeek", 1);
        selectedDate= getIntent().getStringExtra("selectedDate");
        if (selectedDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            selectedDate = sdf.format(new Date());
        }
        btnSave.setOnClickListener(v -> {
            saveWorkoutToFirebase();
        });
    }
    private void setWorkoutAlarm(String title, String timeStr, String dateStr) {
        try {
            
            String[] timeParts = timeStr.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour); 
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            
            calendar.add(Calendar.MINUTE, -10);

            long triggerTime = calendar.getTimeInMillis();
            long currentTime = System.currentTimeMillis();

            
            if (triggerTime <= currentTime) {
                
                triggerTime = currentTime + (2 * 60000);
                Toast.makeText(this, "הזמן כבר עבר! מכוון אוטומטית ל-2 דקות מעכשיו לבדיקה", Toast.LENGTH_LONG).show();
            } else {
                long diffMinutes = (triggerTime - currentTime) / 60000;
                Toast.makeText(this, "מעולה! ההתראה תצלצל בעוד " + diffMinutes + " דקות", Toast.LENGTH_LONG).show();
            }

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("workoutTitle", title);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    (int) triggerTime,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }

        } catch (Exception e) {
            Toast.makeText(this, "שגיאה בפורמט השעה. נא להזין HH:mm", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void saveWorkoutToFirebase() {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String timeInput = etTime.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Title is Required");
            etTitle.requestFocus();
            return;
        }

        
        final String finalDesc = desc.isEmpty() ? null : desc;
        final String finalTime = timeInput.isEmpty() ? null : timeInput;

        String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        com.google.firebase.database.DatabaseReference ref =
                com.google.firebase.database.FirebaseDatabase.getInstance().getReference("users")
                        .child(userId)
                        .child("workouts");

        String workoutId = ref.push().getKey();
        Workout newWorkout = new Workout(workoutId, title, finalDesc, selectedDay, selectedDate);
        newWorkout.setScheduledTime(finalTime);

        ref.child(workoutId).setValue(newWorkout)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        
                        if (finalTime != null) {
                            setWorkoutAlarm(title, finalTime, selectedDate);
                        }
                        Toast.makeText(AddWorkoutActivity.this, "Workout saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddWorkoutActivity.this, "Failed to save: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}