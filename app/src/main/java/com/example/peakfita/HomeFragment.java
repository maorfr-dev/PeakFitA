package com.example.peakfita; // ודא שזה תואם לשם הפקאג' שלך

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

        // לחיצה על כרטיסיית אימון
        cardWorkout.setOnClickListener(v -> loadFragment(new WorkoutsFragment()));

        // לחיצה על כרטיסיית היסטוריה (ממנה נכנסים גם לארון הגביעים)
        cardHistory.setOnClickListener(v -> loadFragment(new HistoryFragment()));

        // לחיצה על כרטיסיית עוזר חכם
        cardGemini.setOnClickListener(v -> loadFragment(new GeminiFragment()));

        // לחיצה על כרטיסיית פרופיל
        cardSettings.setOnClickListener(v -> loadFragment(new SettingsFragment()));

        return view;
    }

    // מתודת עזר שמחליפה את המסך הנוכחי במסך שנבחר ומוסיפה אותו להיסטוריית החזרה (BackStack)
    private void loadFragment(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // מאפשר למשתמש לחזור למסך הבית בלחיצה על כפתור חזור בטלפון
                    .commit();
        }
    }
}