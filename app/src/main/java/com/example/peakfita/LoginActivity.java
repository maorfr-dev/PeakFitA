package com.example.peakfita;

import static com.example.peakfita.FBRef.refAuth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    FirebaseAuth mAuth;
    EditText LeTEmail, LeTPass;
    Button login;
    TextView tVMsg, tVRegister;
    CheckBox stayConnect;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        boolean stayIn = sharedPref.getBoolean("isStayConnected", false);

        mAuth = FBRef.refAuth;

        if (mAuth.getCurrentUser() != null && stayIn) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        LeTEmail = findViewById(R.id.LeTEmail);
        LeTPass = findViewById(R.id.LeTPass);
        login = findViewById(R.id.login);
        stayConnect = findViewById(R.id.stayConnect);
        tVMsg = findViewById(R.id.tVResult);
        tVRegister = findViewById(R.id.tVRegister);
        tVRegister.setOnClickListener(v -> {
            Intent intent =new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(v);
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    public void loginUser(View view) {
        String email = LeTEmail.getText().toString().trim();
        String pass = LeTPass.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            tVMsg.setText("Please fill all fields");
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Connecting");
        pd.setMessage("Logging in user...");
        pd.show();

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pd.dismiss();
                        if (task.isSuccessful()) {

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("isStayConnected", stayConnect.isChecked());
                            editor.apply();

                            Log.i(TAG, "signInWithEmailAndPassword:success");
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            handleLoginError(task.getException());
                        }
                    }
                });
    }

    private void handleLoginError(Exception exp) {
        if (exp instanceof FirebaseAuthInvalidUserException) {
            tVMsg.setText("Invalid email address.");
        } else if (exp instanceof FirebaseAuthInvalidCredentialsException) {
            tVMsg.setText("Wrong password or email.");
        } else if (exp instanceof FirebaseNetworkException) {
            tVMsg.setText("Network error.");
        } else {
            tVMsg.setText("Error: " + exp.getMessage());
        }
    }
}