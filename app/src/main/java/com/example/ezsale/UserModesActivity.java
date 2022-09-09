package com.example.ezsale;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.Objects;

public class UserModesActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_modes);

        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        db = FirebaseFirestore.getInstance();

//        getUserData(db, Objects.requireNonNull(user).getUid(), user1 -> {
//            String userFirstAndLastName = "Welcome " + user1.getUserName();
//            ((TextView) findViewById(R.id.current_user_welcome)).setText(userFirstAndLastName);
//
//        });

        getUserData(currentUser);

        Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(UserModesActivity.this, "Logged Out!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(UserModesActivity.this, MainActivity.class));
        });

    }


    private void getUserData(String userId) {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(task -> {
           if(task.isSuccessful()) {
               DocumentSnapshot document = task.getResult();
               if(document.exists()) {
                   Log.d("userInformation", "DocumentSnapshot data: " + document.getData());
                   String userName = (String) Objects.requireNonNull(document.getData()).get("userName");
                   ((TextView) findViewById(R.id.current_user_welcome)).setText("Welcome " +userName);
               }
               else {
                   Log.d("userInformation", "No such document");
               }
           }
           else {
               Log.d("userInformation", "get failed with ", task.getException());
           }
        });
    }

}