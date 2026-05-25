package com.example.peakfita;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private List<Workout> workoutList;

    public WorkoutAdapter(List<Workout> workoutList) {
        this.workoutList = workoutList;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_item, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workoutList.get(position);
        holder.tvTitle.setText(workout.getTitle());
        // הגדרת התיאור של האימון
        if (workout.getDescription() != null && !workout.getDescription().trim().isEmpty()) {
            holder.tvDesc.setVisibility(View.VISIBLE);
            holder.tvDesc.setText(workout.getDescription());
        } else {
            // אם אין תיאור, מסתירים את תיבת הטקסט כדי שלא יהיה חלל ריק
            holder.tvDesc.setVisibility(View.GONE);
        }

        // מעבר למסך פרטי האימון (הקוד המקורי שלך)
        holder.btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), WorkoutDetailsActivity.class);

            intent.putExtra("WORKOUT_ID", workout.getWorkoutId());
            intent.putExtra("WORKOUT_TITLE", workout.getTitle());

            v.getContext().startActivity(intent);
        });

        // תוספת: לחיצה ארוכה למחיקת אימון
        holder.itemView.setOnLongClickListener(v -> {
            Context context = v.getContext(); // שולפים את ה-Context מהרכיב עליו לחצנו

            new AlertDialog.Builder(context)
                    .setTitle("Delete Workout")
                    .setMessage("Are you sure you want to delete '" + workout.getTitle() + "'?")
                    .setPositiveButton("Yes, Delete", (dialog, which) -> {

                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        FirebaseDatabase.getInstance().getReference("users")
                                .child(userId)
                                .child("workouts")
                                .child(workout.getWorkoutId()) // מחיקה לפי ה-ID הייחודי
                                .removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Workout deleted successfully", Toast.LENGTH_SHORT).show();
                                    // הערה: אם אתה משתמש ב-ValueEventListener במסך הקודם,
                                    // הרשימה תתרענן אוטומטית כי פיירבייס מזהה שינוי.
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .create()
                    .show();

            return true; // מסמן למערכת שהלחיצה הארוכה טופלה
        });
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;
        Button btnStart;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvWorkoutTitle);
            tvDesc = itemView.findViewById(R.id.tvWorkoutDesc);
            btnStart = itemView.findViewById(R.id.btnStartWorkout);
        }
    }
}