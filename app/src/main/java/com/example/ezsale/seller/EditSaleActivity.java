package com.example.ezsale.seller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ezsale.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditSaleActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sale);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();



    }
}