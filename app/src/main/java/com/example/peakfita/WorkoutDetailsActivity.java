package com.example.peakfita;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;

public class WorkoutDetailsActivity extends AppCompatActivity {

    private TextView tvTitle,tvLastTimeHint;
    private EditText etName, etSets, etReps, etWeight;
    private Button btnAddExercise, btnEndWorkout;
    private RecyclerView rvExercises;

    private ExerciseAdapter adapter;
    private List<Exercise> exerciseList;

    private String workoutId;
    private String workoutTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_details);

        
        workoutId = getIntent().getStringExtra("WORKOUT_ID");
        workoutTitle = getIntent().getStringExtra("WORKOUT_TITLE");

        
        tvTitle = findViewById(R.id.tvActiveWorkoutTitle);
        etName = findViewById(R.id.etExerciseName);
        etSets = findViewById(R.id.etSets);
        etReps = findViewById(R.id.etReps);
        etWeight = findViewById(R.id.etWeight);
        btnAddExercise = findViewById(R.id.btnAddExercise);
        btnEndWorkout = findViewById(R.id.btnEndWorkout);
        rvExercises = findViewById(R.id.rvExercises);
        
        tvLastTimeHint = findViewById(R.id.tvLastTimeHint);


        etName.setOnFocusChangeListener((v, hasFocus) -> {
            
            if (!hasFocus) {
                String exerciseName = etName.getText().toString().trim();
                if (!exerciseName.isEmpty()) {
                    
                    searchLastTimeExercise(exerciseName);
                } else {
                    
                    tvLastTimeHint.setVisibility(View.GONE);
                }
            }
        });
        if (workoutTitle != null) {
            tvTitle.setText(workoutTitle);
        }

        
        exerciseList = new ArrayList<>();
        adapter = new ExerciseAdapter(exerciseList);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        rvExercises.setAdapter(adapter);

        
        btnAddExercise.setOnClickListener(v -> addExerciseToList());

        
        btnEndWorkout.setOnClickListener(v -> saveWorkoutToFirebase());
    }

    
    private void searchLastTimeExercise(String searchName) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("history");

        
        historyRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                Exercise lastFoundExercise = null;
                String lastWorkoutDate = "";

                
                for (com.google.firebase.database.DataSnapshot workoutSnapshot : snapshot.getChildren()) {
                    Workout workout = workoutSnapshot.getValue(Workout.class);

                    if (workout != null && workout.getExercises() != null) {
                        
                        for (Exercise ex : workout.getExercises()) {
                            
                            if (ex.getName().equalsIgnoreCase(searchName)) {
                                
                                lastFoundExercise = ex;
                                lastWorkoutDate = workout.getDate(); 
                            }
                        }
                    }
                }

                
                if (lastFoundExercise != null) {
                    
                    String hint = "Last Time: " +
                            lastFoundExercise.getSets() + " Sets | " +
                            lastFoundExercise.getReps() + " Reps | " +
                            lastFoundExercise.getWeight() + " kg";

                    tvLastTimeHint.setText(hint);
                    tvLastTimeHint.setVisibility(View.VISIBLE); 
                } else {
                    
                    tvLastTimeHint.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {
                
                tvLastTimeHint.setVisibility(View.GONE);
            }
        });
    }
    private void addExerciseToList() {
        String name = etName.getText().toString().trim();
        String setsStr = etSets.getText().toString().trim();
        String repsStr = etReps.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();

        
        if (name.isEmpty() || setsStr.isEmpty() || repsStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        
        int sets = Integer.parseInt(setsStr);
        int reps = Integer.parseInt(repsStr);
        double weight = Double.parseDouble(weightStr);

        
        Exercise newExercise = new Exercise(name, sets, reps, weight);
        exerciseList.add(newExercise);
        adapter.notifyDataSetChanged(); 

        
        etName.setText("");
        etSets.setText("");
        etReps.setText("");
        etWeight.setText("");
        etName.requestFocus();
    }

    

    private void saveWorkoutToFirebase() {
        if (exerciseList.isEmpty()) {
            Toast.makeText(this, "Add at least one exercise to finish", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        
        DatabaseReference currentWorkoutRef = userRef.child("workouts").child(workoutId);
        DatabaseReference historyWorkoutRef = userRef.child("history").child(workoutId);

        
        currentWorkoutRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                Workout workout = snapshot.getValue(Workout.class);

                if (workout != null) {
                    
                    workout.setExercises(exerciseList);

                    
                    historyWorkoutRef.setValue(workout).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            
                            currentWorkoutRef.removeValue();

                            Toast.makeText(WorkoutDetailsActivity.this, "Workout saved to History!", Toast.LENGTH_SHORT).show();
                            finish(); 
                        } else {
                            Toast.makeText(WorkoutDetailsActivity.this, "Failed to save to history", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {
                Toast.makeText(WorkoutDetailsActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}