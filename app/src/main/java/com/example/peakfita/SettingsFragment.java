package com.example.peakfita;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {

    private TextView tvUserEmail;
    private Button btnShareApp, btnContactSupport, btnLogout;
    private SwitchMaterial switchNotifications; // המשתנה החדש למתג
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences; // מנגנון השמירה המקומית

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        btnShareApp = view.findViewById(R.id.btnShareApp);
        btnContactSupport = view.findViewById(R.id.btnContactSupport);
        btnLogout = view.findViewById(R.id.btnLogout);
        switchNotifications = view.findViewById(R.id.switchNotifications); // חיבור המתג מהעיצוב

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // אתחול SharedPreferences - יצירת קובץ שמירה מקומי בשם "PeakfitaPrefs"
        sharedPreferences = requireActivity().getSharedPreferences("PeakfitaPrefs", Context.MODE_PRIVATE);

        // טעינת מצב ההתראות השמור (ברירת המחדל היא true - מופעל)
        boolean isNotificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);
        switchNotifications.setChecked(isNotificationsEnabled);

        // מאזין לשינויים במתג ההתראות ושמירת המצב החדש ב-SharedPreferences
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notifications_enabled", isChecked);
            editor.apply(); // ביצוע השמירה ברקע

            String status = isChecked ? "הופעלו" : "כובו";
            Toast.makeText(getContext(), "התראות אימונים " + status, Toast.LENGTH_SHORT).show();
        });

        if (currentUser != null && currentUser.getEmail() != null) {
            tvUserEmail.setText("מחובר כ: " + currentUser.getEmail());
        } else {
            tvUserEmail.setText("משתמש לא מזוהה");
        }

        btnShareApp.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "PeakForm");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "היי! ממליץ לך לנסות את PeakForm - האפליקציה המושלמת למעקב אחרי אימוני הכושר והשיאים שלך בחדר הכושר!");
            startActivity(Intent.createChooser(shareIntent, "שתף באמצעות:"));
        });

        btnContactSupport.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:maorfrisher11@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "פנייה מתמיכה - PeakForm");
            startActivity(Intent.createChooser(emailIntent, "בחר אפליקציית אימייל:"));
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }
}