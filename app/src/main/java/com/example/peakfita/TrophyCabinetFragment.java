package com.example.peakfita;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrophyCabinetFragment extends Fragment {

    private RecyclerView rvTrophies;
    private TrophyAdapter adapter;
    private List<TrophyAdapter.TrophyRecord> trophyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trophy_cabinet, container, false);

        rvTrophies = view.findViewById(R.id.rvTrophies);
        trophyList = new ArrayList<>();
        adapter = new TrophyAdapter(trophyList);

        rvTrophies.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTrophies.setAdapter(adapter);

        loadPersonalRecords();

        return view;
    }

    private void loadPersonalRecords() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("history");

        historyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                
                HashMap<String, Double> personalRecords = new HashMap<>();

                for (DataSnapshot workoutSnapshot : snapshot.getChildren()) {
                    Workout workout = workoutSnapshot.getValue(Workout.class);
                    if (workout != null && workout.getExercises() != null) {
                        for (Exercise ex : workout.getExercises()) {
                            String exerciseName = ex.getName().trim().toLowerCase();
                            double currentWeight = ex.getWeight();

                            
                            if (personalRecords.containsKey(exerciseName)) {
                                if (currentWeight > personalRecords.get(exerciseName)) {
                                    personalRecords.put(exerciseName, currentWeight);
                                }
                            } else {
                                personalRecords.put(exerciseName, currentWeight);
                            }
                        }
                    }
                }

                
                trophyList.clear();
                for (Map.Entry<String, Double> entry : personalRecords.entrySet()) {
                    trophyList.add(new TrophyAdapter.TrophyRecord(entry.getKey(), entry.getValue()));
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load PRs", Toast.LENGTH_SHORT).show();
            }
        });
    }
}