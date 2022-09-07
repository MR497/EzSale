package com.example.ezsale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class UserModesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_modes);

        Button logoutButton = findViewById(R.id.logout_button);

        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(UserModesActivity.this, "Logged Out!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(UserModesActivity.this, MainActivity.class));
        });

    }
}