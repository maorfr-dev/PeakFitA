package com.example.peakfita;

import static com.example.peakfita.FBRef.refAuth;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setItemIconTintList(null);

        if (savedInstanceState == null) {
            replaceFragment(new WorkoutsFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_workouts);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_workouts) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WorkoutsFragment()).commit();
                return true;
            } else if (id == R.id.nav_history) {
                Toast.makeText(this, "היסטוריה בקרוב", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HistoryFragment()).commit();
                return true;
            } else if (id == R.id.nav_gemini) {
                Toast.makeText(this, "Gemini AI בקרוב", Toast.LENGTH_SHORT).show();
                // replaceFragment(new GeminiFragment());
                return true;
            } else if (id == R.id.nav_settings) {
                logoutUser();
                return true;
            }
            return false;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void logoutUser() {
        refAuth.signOut();
        Toast.makeText(this, "התנתקת בהצלחה", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}