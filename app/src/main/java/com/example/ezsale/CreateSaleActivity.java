package com.example.ezsale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class CreateSaleActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sale);

        db =  FirebaseFirestore.getInstance();

        Button postSaleButton = findViewById(R.id.post_sale_button);
        postSaleButton.setOnClickListener(view -> {
            String itemName = ((EditText)findViewById(R.id.item_name_text)).getText().toString();
            String itemDesc = ((EditText)findViewById(R.id.item_description_text)).getText().toString();
            String itemCost = ((EditText)findViewById(R.id.item_cost_text)).getText().toString();
            String zipcode = ((EditText)findViewById(R.id.zipcode_text)).getText().toString();

            if(TextUtils.isEmpty(itemName) || TextUtils.isEmpty(itemDesc) || TextUtils.isEmpty(itemCost)
                    || TextUtils.isEmpty(zipcode)) {
                Toast.makeText(CreateSaleActivity.this, "Form is incomplete!", Toast.LENGTH_LONG).show();
            }
            else {
                createSale(itemName, itemDesc, itemCost, zipcode);
            }
        });
    }

    private void createSale(String itemName, String itemDesc, String itemCost, String zipcode) {
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        HashMap<String, String> salesMap = new HashMap<>();
        salesMap.put("author", currentUser);
        salesMap.put("name", itemName);
        salesMap.put("description", itemDesc);
        salesMap.put("cost", itemCost);
        salesMap.put("zipcode", zipcode);

        db.collection("Items Being Sold").document(currentUser).collection("User's Listings").document(itemName)
                .set(salesMap).addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               Toast.makeText(CreateSaleActivity.this, "Item Put Up For Sale!", Toast.LENGTH_LONG).show();
               startActivity(new Intent(CreateSaleActivity.this, SellerMode.class));
           }
           else {
               Toast.makeText(CreateSaleActivity.this, "Oops an error has occurred! Please try again", Toast.LENGTH_LONG).show();
           }
        });
    }
}