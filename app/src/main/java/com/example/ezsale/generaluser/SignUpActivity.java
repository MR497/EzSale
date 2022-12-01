package com.example.ezsale.generaluser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ezsale.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create New Account");

        auth = FirebaseAuth.getInstance();
        //db = FirebaseFirestore.getInstance();

        Button registerButton = findViewById(R.id.register_submit);
        registerButton.setOnClickListener(view -> {
            // Get registration form fields
            String userName = ((EditText)findViewById(R.id.register_username)).getText().toString();
            String email = ((EditText)findViewById(R.id.register_email)).getText().toString();
            String password = ((EditText)findViewById(R.id.register_password)).getText().toString();

            if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(SignUpActivity.this, "Incomplete form!", Toast.LENGTH_LONG).show();
            } else if (password.length() < 6) {
                Toast.makeText(SignUpActivity.this, "Password needs to be at least 6 characters!", Toast.LENGTH_LONG).show();
            } else {
                // If form is in good standing then register user
                registerUsers(userName, email, password);
            }
        });

    }

    private void registerUsers(String userName, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, task -> {
                if (task.isSuccessful()){
                    Map<String, String> userFields = new HashMap<>();
                    userFields.put("userName", userName);
                    userFields.put("email", email);

                    String uid = Objects.requireNonNull(task.getResult().getUser()).getUid();

                    db.collection("users").document(uid).set(userFields).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Added User Fields!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    Log.d("registered", "registration to firebase complete");
                    Toast.makeText(SignUpActivity.this, "Registration Complete!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignUpActivity.this, UserModesActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(SignUpActivity.this, "Registration FAILED - Email in use", Toast.LENGTH_LONG).show();
                }
        });
    }
}
