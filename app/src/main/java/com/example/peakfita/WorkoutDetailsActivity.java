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

        // 1. קבלת הנתונים מהמסך הקודם (כפתור ה-Start)
        workoutId = getIntent().getStringExtra("WORKOUT_ID");
        workoutTitle = getIntent().getStringExtra("WORKOUT_TITLE");

        // 2. חיבור האלמנטים מהעיצוב לקוד
        tvTitle = findViewById(R.id.tvActiveWorkoutTitle);
        etName = findViewById(R.id.etExerciseName);
        etSets = findViewById(R.id.etSets);
        etReps = findViewById(R.id.etReps);
        etWeight = findViewById(R.id.etWeight);
        btnAddExercise = findViewById(R.id.btnAddExercise);
        btnEndWorkout = findViewById(R.id.btnEndWorkout);
        rvExercises = findViewById(R.id.rvExercises);
        // חיבור ה-TextView המוסתר מהעיצוב
        tvLastTimeHint = findViewById(R.id.tvLastTimeHint);

// הגדרת המאזין לתיבת הטקסט של שם התרגיל
        etName.setOnFocusChangeListener((v, hasFocus) -> {
            // hasFocus אומר לנו אם המשתמש כרגע בתוך התיבה או שיצא ממנה
            if (!hasFocus) {
                String exerciseName = etName.getText().toString().trim();
                if (!exerciseName.isEmpty()) {
                    // אם הוא יצא מהתיבה ויש בה טקסט - מפעילים את החיפוש!
                    searchLastTimeExercise(exerciseName);
                } else {
                    // אם התיבה ריקה, מסתירים את הרמז
                    tvLastTimeHint.setVisibility(View.GONE);
                }
            }
        });
        if (workoutTitle != null) {
            tvTitle.setText(workoutTitle);
        }

        // 3. הגדרת הרשימה (RecyclerView) של התרגילים
        exerciseList = new ArrayList<>();
        adapter = new ExerciseAdapter(exerciseList);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        rvExercises.setAdapter(adapter);

        // 4. לחיצה על כפתור "+ Add Exercise"
        btnAddExercise.setOnClickListener(v -> addExerciseToList());

        // 5. לחיצה על כפתור "End Workout"
        btnEndWorkout.setOnClickListener(v -> saveWorkoutToFirebase());
    }

    // פונקציה שמחפשת בהיסטוריה את הפעם האחרונה שהתרגיל בוצע
    private void searchLastTimeExercise(String searchName) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("history");

        // קוראים פעם אחת את כל תיקיית ההיסטוריה
        historyRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                Exercise lastFoundExercise = null;
                String lastWorkoutDate = "";

                // לולאה ראשונה: עוברים על כל האימונים בהיסטוריה
                for (com.google.firebase.database.DataSnapshot workoutSnapshot : snapshot.getChildren()) {
                    Workout workout = workoutSnapshot.getValue(Workout.class);

                    if (workout != null && workout.getExercises() != null) {
                        // לולאה שנייה: עוברים על כל התרגילים בתוך האימון הספציפי
                        for (Exercise ex : workout.getExercises()) {
                            // equalsIgnoreCase מוודא שזה ימצא גם אם הקלדנו "bench" והיה שמור "Bench"
                            if (ex.getName().equalsIgnoreCase(searchName)) {
                                // מצאנו התאמה! נשמור אותה (מכיוון שהלולאה רצה מהישן לחדש, ההתאמה האחרונה שתישמר היא הכי עדכנית)
                                lastFoundExercise = ex;
                                lastWorkoutDate = workout.getDate(); // שומרים גם את התאריך אם קיים
                            }
                        }
                    }
                }

                // אחרי שסיימנו לסרוק הכל, בודקים אם מצאנו משהו
                if (lastFoundExercise != null) {
                    // מרכיבים את המשפט שיוצג למשתמש
                    String hint = "בפעם הקודמת: " +
                            lastFoundExercise.getSets() + " סטים | " +
                            lastFoundExercise.getReps() + " חזרות | " +
                            lastFoundExercise.getWeight() + " ק\"ג";

                    tvLastTimeHint.setText(hint);
                    tvLastTimeHint.setVisibility(View.VISIBLE); // מדליקים את הטקסט!
                } else {
                    // לא מצאנו תרגיל כזה בהיסטוריה
                    tvLastTimeHint.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {
                // במקרה של שגיאה בחיבור, פשוט נשאיר את הטקסט מוסתר
                tvLastTimeHint.setVisibility(View.GONE);
            }
        });
    }
    private void addExerciseToList() {
        String name = etName.getText().toString().trim();
        String setsStr = etSets.getText().toString().trim();
        String repsStr = etReps.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();

        // מוודאים שהמשתמש מילא את כל השדות
        if (name.isEmpty() || setsStr.isEmpty() || repsStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // הופכים את הטקסט למספרים
        int sets = Integer.parseInt(setsStr);
        int reps = Integer.parseInt(repsStr);
        double weight = Double.parseDouble(weightStr);

        // יוצרים אובייקט "תרגיל" ומוסיפים לרשימה
        Exercise newExercise = new Exercise(name, sets, reps, weight);
        exerciseList.add(newExercise);
        adapter.notifyDataSetChanged(); // מרעננים את הרשימה על המסך

        // מנקים את השדות כדי שיהיה קל להזין את התרגיל הבא
        etName.setText("");
        etSets.setText("");
        etReps.setText("");
        etWeight.setText("");
        etName.requestFocus();
    }

    // פונקציה ששומרת את כל הרשימה לפיירבייס ומסיימת את האימון
// פונקציה שמעבירה את האימון להיסטוריה ומוחקת אותו מהשבוע הנוכחי
    private void saveWorkoutToFirebase() {
        if (exerciseList.isEmpty()) {
            Toast.makeText(this, "Add at least one exercise to finish", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // מגדירים את הכתובת של האימון הנוכחי, ואת הכתובת החדשה שלו בהיסטוריה
        DatabaseReference currentWorkoutRef = userRef.child("workouts").child(workoutId);
        DatabaseReference historyWorkoutRef = userRef.child("history").child(workoutId);

        // שלב 1: שולפים את כל הנתונים של האימון הנוכחי (כמו התאריך, היום והשם שלו)
        currentWorkoutRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                Workout workout = snapshot.getValue(Workout.class);

                if (workout != null) {
                    // שלב 2: מכניסים לתוך האימון את כל התרגילים שעשינו הרגע
                    workout.setExercises(exerciseList);

                    // שלב 3: שומרים את האימון המלא בתוך תיקיית history
                    historyWorkoutRef.setValue(workout).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            // שלב 4 (הקסם!): מוחקים את האימון מתיקיית workouts
                            currentWorkoutRef.removeValue();

                            Toast.makeText(WorkoutDetailsActivity.this, "Workout saved to History!", Toast.LENGTH_SHORT).show();
                            finish(); // חוזרים למסך הראשי
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