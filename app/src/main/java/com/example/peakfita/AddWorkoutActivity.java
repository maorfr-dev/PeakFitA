package com.example.peakfita;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddWorkoutActivity extends AppCompatActivity {

    private EditText etTitle, etDesc, etTime;
    private Button btnSave;
    private int selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        etTitle = findViewById(R.id.etWorkoutTitle);
        etDesc = findViewById(R.id.etWorkoutDesc);
        etTime = findViewById(R.id.etScheduledTime);
        btnSave = findViewById(R.id.btnSaveWorkout);
        selectedDay = getIntent().getIntExtra("dayOfWeek", 1);
        btnSave.setOnClickListener(v -> {
            saveWorkoutToFirebase();
        });
    }
    private void saveWorkoutToFirebase() {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        if(title.isEmpty())
        {
            etTitle.setError("Title is Required");
            etTitle.requestFocus();
            return;
        }
        String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();
        com.google.firebase.database.DatabaseReference ref =
                com.google.firebase.database.FirebaseDatabase.getInstance().getReference("users")
                        .child(userId)
                        .child("workouts");
        String workoutId = ref.push().getKey();
        if (desc.isEmpty()) { desc = null; }
        if (time.isEmpty()) { time = null; }
        Workout newWorkout = new Workout(workoutId, title, desc, selectedDay);
        newWorkout.setScheduledTime(time);
        ref.child(workoutId).setValue(newWorkout)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Toast.makeText(AddWorkoutActivity.this, "Workout saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {

                        Toast.makeText(AddWorkoutActivity.this, "Failed to save: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });










}


}