package com.example.peakfita;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WorkoutsFragment extends Fragment {

    
    private MaterialButton[] dayButtons = new MaterialButton[7];
    private String[] weekDates = new String[7];
    private int currentSelectedDay = 1;
    private String currentSelectedDate;

    private RecyclerView rvWorkouts;
    private LinearLayout llEmptyState; 
    private WorkoutAdapter adapter;
    private List<Workout> workoutList;

    public WorkoutsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workouts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupWeekDates();

        llEmptyState = view.findViewById(R.id.llEmptyState);
        rvWorkouts = view.findViewById(R.id.rvWorkouts);
        rvWorkouts.setLayoutManager(new LinearLayoutManager(getContext()));
        workoutList = new ArrayList<>();
        adapter = new WorkoutAdapter(workoutList);
        rvWorkouts.setAdapter(adapter);

        dayButtons[0] = view.findViewById(R.id.btnDay1);
        dayButtons[1] = view.findViewById(R.id.btnDay2);
        dayButtons[2] = view.findViewById(R.id.btnDay3);
        dayButtons[3] = view.findViewById(R.id.btnDay4);
        dayButtons[4] = view.findViewById(R.id.btnDay5);
        dayButtons[5] = view.findViewById(R.id.btnDay6);
        dayButtons[6] = view.findViewById(R.id.btnDay7);

        for (int i = 0; i < dayButtons.length; i++) {
            final int dayIndex = i + 1;
            final String dateForThisButton = weekDates[i];

            dayButtons[i].setOnClickListener(v -> {
                currentSelectedDay = dayIndex;
                currentSelectedDate = dateForThisButton;
                updateSelectedDay(dayIndex);
                loadWorkoutsForDate(currentSelectedDate);
            });
        }

        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddWorkout);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddWorkoutActivity.class);
            intent.putExtra("dayOfWeek", currentSelectedDay);
            intent.putExtra("selectedDate", currentSelectedDate);
            startActivity(intent);
        });

        Calendar calendar = Calendar.getInstance();
        int todayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        currentSelectedDay = todayIndex;
        currentSelectedDate = weekDates[todayIndex - 1];
        updateSelectedDay(todayIndex);
        loadWorkoutsForDate(currentSelectedDate);
    }

    private void setupWeekDates() {
        Calendar calendar = Calendar.getInstance();
        
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            weekDates[i] = sdf.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    
    private void updateSelectedDay(int selectedDay) {
        for (int i = 0; i < dayButtons.length; i++) {
            if (i == (selectedDay - 1)) {
                
                dayButtons[i].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFD700")));
                dayButtons[i].setTextColor(Color.parseColor("#121212"));
            } else {
                
                dayButtons[i].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E1E1E")));
                dayButtons[i].setTextColor(Color.parseColor("#FFFFFF"));
            }
        }
    }

    private void loadWorkoutsForDate(String date) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                .child(userId).child("workouts");

        ref.orderByChild("date").equalTo(date)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        workoutList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Workout w = data.getValue(Workout.class);
                            if (w != null) {
                                workoutList.add(w);
                            }
                        }
                        adapter.notifyDataSetChanged();

                        
                        if (workoutList.isEmpty()) {
                            rvWorkouts.setVisibility(View.GONE);
                            llEmptyState.setVisibility(View.VISIBLE);
                        } else {
                            rvWorkouts.setVisibility(View.VISIBLE);
                            llEmptyState.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Peakfita", "Database error: " + error.getMessage());
                    }
                });
    }
}