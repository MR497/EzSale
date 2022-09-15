package com.example.ezsale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SellerMode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_mode);

        Button sellNewItem = findViewById(R.id.seller_mode_sell_button);
        sellNewItem.setOnClickListener(view -> startActivity(new Intent(SellerMode.this, CreateSaleActivity.class)));

    }
}