package com.example.peakfita;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class GeminiFragment extends Fragment {

    private EditText eTMuscle;
    private TextView tVResult;
    private Button btnSendPrompt;
    private GeminiManager geminiManager;
    private final String TAG = "GeminiFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.fragment_gemini, container, false);

        eTMuscle = view.findViewById(R.id.eTMuscle);
        tVResult = view.findViewById(R.id.tVResult);
        btnSendPrompt = view.findViewById(R.id.btnSendPrompt);

        
        tVResult.setMovementMethod(new ScrollingMovementMethod());

        geminiManager = GeminiManager.getInstance();

        
        btnSendPrompt.setOnClickListener(v -> textPrompt());

        return view;
    }

    private void textPrompt() {
        String muscle = eTMuscle.getText().toString().trim();

        if (muscle.isEmpty()) {
            tVResult.setText("Please enter a target muscle.");
            return;
        }

        
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        
        String prompt = "Recommend 3 exercises in the gym for the following muscle: " + muscle +
                ". Return the name of the exercise, which specific part of the muscle it targets, and its advantages. Keep it in 150 words max.";

        ProgressDialog pD = new ProgressDialog(getContext());
        pD.setTitle("PeakForm AI");
        pD.setMessage("Generating workout plan...");
        pD.setCancelable(false);
        pD.show();

        geminiManager.sendTextPrompt(prompt, new GeminiCallback() {
            @Override
            public void onSuccess(String result) {
                
                requireActivity().runOnUiThread(() -> {
                    pD.dismiss();
                    tVResult.setText(result);
                });
            }

            @Override
            public void onFailure(Throwable error) {
                requireActivity().runOnUiThread(() -> {
                    pD.dismiss();
                    tVResult.setText("Failed connecting to PeakForm AI.");
                    Log.e(TAG, "Error: " + error.getMessage());
                });
            }
        });
    }
}