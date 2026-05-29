package com.example.peakfita; 

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {

    private MaterialCardView cardWorkout, cardHistory, cardGemini, cardSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        cardWorkout = view.findViewById(R.id.cardWorkout);
        cardHistory = view.findViewById(R.id.cardHistory);
        cardGemini = view.findViewById(R.id.cardGemini);
        cardSettings = view.findViewById(R.id.cardSettings);

        
        cardWorkout.setOnClickListener(v -> loadFragment(new WorkoutsFragment()));

        
        cardHistory.setOnClickListener(v -> loadFragment(new HistoryFragment()));

        
        cardGemini.setOnClickListener(v -> loadFragment(new GeminiFragment()));

        
        cardSettings.setOnClickListener(v -> loadFragment(new SettingsFragment()));

        return view;
    }

    
    private void loadFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) 
                    .commit();
        }
    }
}