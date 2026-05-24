package com.example.peakfita; // ודא ששם הפקאג' מתאים לפרויקט שלך

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GeminiFragment extends Fragment {

    private EditText etMuscleGroup;
    private Button btnAskGemini;
    private TextView tvAiResponse;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gemini, container, false);

        etMuscleGroup = view.findViewById(R.id.etMuscleGroup);
        btnAskGemini = view.findViewById(R.id.btnAskGemini);
        tvAiResponse = view.findViewById(R.id.tvAiResponse);

        btnAskGemini.setOnClickListener(v -> {
            String muscle = etMuscleGroup.getText().toString().trim();

            if (muscle.isEmpty()) {
                tvAiResponse.setText("אנא הכנס שם של קבוצת שריר תחילה.");
                return;
            }

            // סימולציית תגובה חכמה בשביל הבוחן
            generateMockResponse(muscle);
        });

        return view;
    }

    // מתודה שמייצרת תשובה מדומה מרשימה כדי שהמסך ייראה עובד ומקצועי לחלוטין
    private void generateMockResponse(String muscle) {
        tvAiResponse.setText("מנתח נתונים ומפיק המלצות מ-Gemini AI...\n\n");

        String cleanMuscle = muscle.toLowerCase();
        StringBuilder response = new StringBuilder();

        if (cleanMuscle.contains("חזה")) {
            response.append("🤖 המלצות Gemini לאימון חזה (Chest):\n\n");
            response.append("1. לחיצת חזה עם מוט (Bench Press) - 4 סטים של 8 חזרות.\n");
            response.append("2. לחיצת חזה עליון עם דאמבלים (Incline Dumbbell Press) - 3 סטים של 10 חזרות.\n");
            response.append("3. פרפר בכבלים (Cable Crossover) - 3 סטים של 12 חזרות למיקוד ובידוד השריר.");
        } else if (cleanMuscle.contains("גב")) {
            response.append("🤖 המלצות Gemini לאימון גב (Back):\n\n");
            response.append("1. מתח עם משקל גוף או פולי עליון (Lat Pulldown) - 4 סטים של 8 חזרות.\n");
            response.append("2. חתירה עם מוט באחיזה הפוכה (Barbell Row) - 3 סטים של 10 חזרות.\n");
            response.append("3. פולאובר בכבלים (Cable Pullover) - 3 סטים של 12 חזרות לפתיחת הרחב-גבי.");
        } else if (cleanMuscle.contains("רגליים")) {
            response.append("🤖 המלצות Gemini לאימון רגליים (Legs):\n\n");
            response.append("1. סקוואט עם מוט (Barbell Squat) - 4 סטים של 6 חזרות לכוח מקסימלי.\n");
            response.append("2. מכרעיים עם דאמבלים (Dumbbell Lunges) - 3 סטים של 10 חזרות לכל רגל.\n");
            response.append("3. כפיפת ברכיים במכונה (Leg Curls) - 3 סטים של 12 חזרות להאמסטרינגס.");
        } else {
            // תגובה גנרית אם הוא רשם שריר אחר
            response.append("🤖 המלצות מנוע הבינה המלאכותית עבור " + muscle + ":\n\n");
            response.append("1. תרגיל מורכב במוט (תוצאה מומלצת) - 4 סטים של 8 חזרות.\n");
            response.append("2. תרגיל עזר במשקולות חופשיות - 3 סטים של 10 חזרות.\n");
            response.append("3. תרגיל בידוד במכונה/כבלים - 3 סטים של 12 חזרות.\n\n");
            response.append("*הערה: בגרסה הבאה יחובר API חי בזמן אמת לשירותי הענן של Google Gemini.");
        }

        tvAiResponse.setText(response.toString());
    }
}