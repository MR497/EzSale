package com.example.ezsale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        Button signupButton = findViewById(R.id.main_register_button);
        Button loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(view -> {
            String email = ((EditText)findViewById(R.id.email)).getText().toString();
            String password = ((EditText)findViewById(R.id.password)).getText().toString();


            if (!(email.isEmpty() || password.isEmpty())) {
                loginUser(email, password);
            } else {
                Toast.makeText(MainActivity.this, "Incomplete Sign-in Form", Toast.LENGTH_SHORT).show();
            }
        });

        signupButton.setOnClickListener(this::goToSignUpActivity);
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Logged In Successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, UserModesActivity.class));
            } else {
                Toast.makeText(MainActivity.this, "Wrong credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToSignUpActivity(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }


}