package com.example.peakfita;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.ArrayList;
import java.util.List;



import java.util.Calendar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkoutsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutsFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Button[] dayButtons = new Button[7];
    private androidx.recyclerview.widget.RecyclerView rvWorkouts;
    private WorkoutAdapter adapter;
    private java.util.List<Workout> workoutList;
    private int currentSelectedDay = 1;


    private String mParam1;
    private String mParam2;

    public WorkoutsFragment() {
    }


    public static WorkoutsFragment newInstance(String param1, String param2) {
        WorkoutsFragment fragment = new WorkoutsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Calendar calendar = Calendar.getInstance();
        return inflater.inflate(R.layout.fragment_workouts, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvWorkouts = view.findViewById(R.id.rvWorkouts);
        rvWorkouts.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        workoutList = new java.util.ArrayList<>();
        adapter = new WorkoutAdapter(workoutList);
        rvWorkouts.setAdapter(adapter);
        com.google.android.material.floatingactionbutton.FloatingActionButton fabAdd = view.findViewById(R.id.fabAddWorkout);

        dayButtons[0] = view.findViewById(R.id.btnDay1);
        dayButtons[1] = view.findViewById(R.id.btnDay2);
        dayButtons[2] = view.findViewById(R.id.btnDay3);
        dayButtons[3] = view.findViewById(R.id.btnDay4);
        dayButtons[4] = view.findViewById(R.id.btnDay5);
        dayButtons[5] = view.findViewById(R.id.btnDay6);
        dayButtons[6] = view.findViewById(R.id.btnDay7);


        for (int i = 0; i < dayButtons.length; i++) {
            final int dayIndex = i + 1;
            dayButtons[i].setOnClickListener(v -> {
                currentSelectedDay = dayIndex;
                updateSelectedDay(dayIndex);
                loadWorkoutsForDay(dayIndex);
            });
        }


        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        currentSelectedDay=today;

        updateSelectedDay(today);
        loadWorkoutsForDay(today);
        fabAdd.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getActivity(), AddWorkoutActivity.class);
            intent.putExtra("dayOfWeek", currentSelectedDay);
            startActivity(intent);
        });
    }
    private void updateSelectedDay(int selectedDay) {
        for (int i = 0; i < dayButtons.length; i++) {
            dayButtons[i].setSelected(i == (selectedDay - 1));
        }
    }

    private void loadWorkoutsForDay(int day) {
        // 1. קבלת ה-ID של המשתמש שלנו
        String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 2. הפניה לנתיב האימונים בפיירבייס
        com.google.firebase.database.DatabaseReference ref =
                com.google.firebase.database.FirebaseDatabase.getInstance().getReference("users")
                        .child(userId)
                        .child("workouts");

        // 3. שליפת האימונים ששייכים רק ליום שנבחר
        ref.orderByChild("dayOfWeek").equalTo(day)
                .addValueEventListener(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        workoutList.clear(); // מנקה את הרשימה הישנה מהמסך

                        // מעבר על כל התוצאות מפיירבייס והכנסתן לרשימה שלנו
                        for (com.google.firebase.database.DataSnapshot data : snapshot.getChildren()) {
                            Workout w = data.getValue(Workout.class);
                            if (w != null) {
                                workoutList.add(w);
                            }
                        }

                        // פקודת קסם: אומרת ל-Adapter "הנתונים השתנו, תצייר את המסך מחדש!"
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {
                        android.util.Log.e("Peakfita", "Database error: " + error.getMessage());
                    }
                });
    }



}