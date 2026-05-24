package com.example.peakfita;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.peakfita.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {

    private TextView tvUserEmail;
    private Button btnShareApp, btnContactSupport, btnLogout;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        btnShareApp = view.findViewById(R.id.btnShareApp);
        btnContactSupport = view.findViewById(R.id.btnContactSupport);
        btnLogout = view.findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // הצגת האימייל של המשתמש בלי לגשת למסד הנתונים
        if (currentUser != null && currentUser.getEmail() != null) {
            tvUserEmail.setText("מחובר כ: " + currentUser.getEmail());
        } else {
            tvUserEmail.setText("משתמש לא מזוהה");
        }

        // פעולת שיתוף האפליקציה (פותח תפריט שיתוף של אנדרואיד)
        btnShareApp.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "PeakForm");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "היי! ממליץ לך לנסות את PeakForm - האפליקציה המושלמת למעקב אחרי אימוני הכושר והשיאים שלך בחדר הכושר!");
            startActivity(Intent.createChooser(shareIntent, "שתף באמצעות:"));
        });

        // פעולת יצירת קשר (פותח אפליקציית אימייל)
        btnContactSupport.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:maorfrisher11@gmail.com")); // אפשר לשנות למייל שלך
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "פנייה מתמיכה - PeakForm");
            startActivity(Intent.createChooser(emailIntent, "בחר אפליקציית אימייל:"));
        });

        // פעולת התנתקות
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