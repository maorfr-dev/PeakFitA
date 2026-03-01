package com.example.peakfita;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    private Button[] dayButtons = new Button[7];
    private String[] weekDates = new String[7];
    private int currentSelectedDay = 1;
    private String currentSelectedDate;

    private RecyclerView rvWorkouts;
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

        // 1. חישוב התאריכים של השבוע הנוכחי! (ממלא את המערך weekDates)
        setupWeekDates();

        // 2. הגדרת הרשימה (RecyclerView)
        rvWorkouts = view.findViewById(R.id.rvWorkouts);
        rvWorkouts.setLayoutManager(new LinearLayoutManager(getContext()));
        workoutList = new ArrayList<>();
        adapter = new WorkoutAdapter(workoutList);
        rvWorkouts.setAdapter(adapter);

        // 3. חיבור הכפתורים
        dayButtons[0] = view.findViewById(R.id.btnDay1);
        dayButtons[1] = view.findViewById(R.id.btnDay2);
        dayButtons[2] = view.findViewById(R.id.btnDay3);
        dayButtons[3] = view.findViewById(R.id.btnDay4);
        dayButtons[4] = view.findViewById(R.id.btnDay5);
        dayButtons[5] = view.findViewById(R.id.btnDay6);
        dayButtons[6] = view.findViewById(R.id.btnDay7);

        // 4. הגדרת לחיצה על כל כפתור יום
        for (int i = 0; i < dayButtons.length; i++) {
            final int dayIndex = i + 1;
            final String dateForThisButton = weekDates[i]; // שולף את התאריך הספציפי לכפתור הזה

            dayButtons[i].setOnClickListener(v -> {
                currentSelectedDay = dayIndex;
                currentSelectedDate = dateForThisButton; // מעדכן את התאריך שנבחר
                updateSelectedDay(dayIndex);
                loadWorkoutsForDate(currentSelectedDate); // <--- שולף מפיירבייס לפי תאריך!
            });
        }

        // 5. לחיצה על כפתור ההוספה (+)
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddWorkout);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddWorkoutActivity.class);
            intent.putExtra("dayOfWeek", currentSelectedDay);
            intent.putExtra("selectedDate", currentSelectedDate); // <--- שולח למסך הבא את התאריך המדויק
            startActivity(intent);
        });

        // 6. הפעלה ראשונית: זיהוי היום הנוכחי וסימונו
        Calendar calendar = Calendar.getInstance();
        int todayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        currentSelectedDay = todayIndex;
        currentSelectedDate = weekDates[todayIndex - 1]; // מתאים את התאריך ליום של היום
        updateSelectedDay(todayIndex);
        loadWorkoutsForDate(currentSelectedDate);
    }

    // פונקציה חדשה: מחשבת מה התאריכים של יום א' עד שבת השבוע
    private void setupWeekDates() {
        Calendar calendar = Calendar.getInstance();
        // מחזיר את לוח השנה ליום ראשון של השבוע הנוכחי
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // רץ 7 פעמים, שומר את התאריך במערך, ומוסיף יום אחד קדימה
        for (int i = 0; i < 7; i++) {
            weekDates[i] = sdf.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void updateSelectedDay(int selectedDay) {
        for (int i = 0; i < dayButtons.length; i++) {
            dayButtons[i].setSelected(i == (selectedDay - 1));
        }
    }

    // הפונקציה ששונתה: עכשיו מקבלת String (תאריך) במקום int (יום)
    private void loadWorkoutsForDate(String date) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                .child(userId).child("workouts");

        // <--- השינוי הענק: מחפשים רק אימונים שה-date שלהם שווה לתאריך שלחצנו עליו!
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Peakfita", "Database error: " + error.getMessage());
                    }
                });
    }
}