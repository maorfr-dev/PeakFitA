package com.example.peakfita;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView rvHistoryWorkouts;
    private HistoryWorkoutAdapter adapter;
    private List<Workout> historyList;
    private Button btnTrophyCabinet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        btnTrophyCabinet = view.findViewById(R.id.btnTrophyCabinet);
        rvHistoryWorkouts = view.findViewById(R.id.rvHistoryWorkouts);

        // הגדרת הרשימה (RecyclerView)
        historyList = new ArrayList<>();
        adapter = new HistoryWorkoutAdapter(historyList);
        rvHistoryWorkouts.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistoryWorkouts.setAdapter(adapter);

        // הפעלת פונקציית הטעינה מפיירבייס
        loadHistoryFromFirebase();

        // לחיצה על כפתור ארון הגביעים
        btnTrophyCabinet.setOnClickListener(v -> {
            // עוברים למסך ארון הגביעים החדש
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new TrophyCabinetFragment())
                    // מוסיפים את המסך הקודם להיסטוריה כדי שכפתור ה"חזור" בטלפון יעבוד
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }

    private void loadHistoryFromFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("history");

        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                historyList.clear(); // מנקים את הרשימה כדי למנוע כפילויות

                for (DataSnapshot workoutSnapshot : snapshot.getChildren()) {
                    Workout workout = workoutSnapshot.getValue(Workout.class);
                    if (workout != null) {
                        historyList.add(workout);
                    }
                }

                // הופכים את הרשימה כדי שהאימון *האחרון* שעשינו יופיע ראשון למעלה!
                Collections.reverse(historyList);

                adapter.notifyDataSetChanged(); // מעדכנים את המסך
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load history", Toast.LENGTH_SHORT).show();
            }
        });
    }
}