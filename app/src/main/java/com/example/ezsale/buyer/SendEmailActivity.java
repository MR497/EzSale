package com.example.ezsale.buyer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ezsale.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class SendEmailActivity extends AppCompatActivity {

    private EditText emailAddress, emailSubject, emailMessage;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Contact Seller");

        db = FirebaseFirestore.getInstance();

        emailAddress = findViewById(R.id.email_address_input);
        emailSubject = findViewById(R.id.email_subject_input);
        emailMessage = findViewById(R.id.email_message_input);

        Intent i = getIntent();
        String getSeller = i.getExtras().getString("sellerName");
        String getEmail = i.getExtras().getString("emailAddress");
        String getItemName = i.getExtras().getString("itemName");
        String getItemDesc = i.getExtras().getString("itemDesc");
        String getItemCost = i.getExtras().getString("itemCost");
        String getItemLocation = i.getExtras().getString("itemLocation");
        emailAddress.setText(getEmail);
        emailSubject.setText("Looking to buy "+getItemName+" from your listing on EzSale");
        emailMessage.setText(getItemName+" \n"+getItemDesc+" \n$"+getItemCost+" \n"+getItemLocation);

        Button send = findViewById(R.id.send_email_btn);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail();
                contactedSellersCollection(getSeller, getItemName, getItemDesc, getItemCost, getItemLocation);
                finish();
            }
        });

    }

    private void sendMail() {
        String email = emailAddress.getText().toString();
        String[] emailList = email.split(",");
        String subject = emailSubject.getText().toString();
        String message = emailMessage.getText().toString();

        //Intent intent = new Intent(Intent.ACTION_SEND);
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL, emailList);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        //intent.setType("message/rfc822");
        intent.setData(Uri.parse("mailto:"));
        startActivity(Intent.createChooser(intent, "Choose An Email Client"));
    }

    private void contactedSellersCollection(String sellerName, String itemName, String desc, String cost, String location){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateFormat.format(cal.getTime());

        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        HashMap<String, String> contactsMap = new HashMap<>();
        contactsMap.put("seller", sellerName);
        contactsMap.put("item", itemName);
        contactsMap.put("description", desc);
        contactsMap.put("cost", cost);
        contactsMap.put("location", location);
        contactsMap.put("date", date);

        db.collection("Contacted Sellers").document(currentUser).collection("Sellers Emailed").document()
                .set(contactsMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("collectionStatus", "document created");
                    }
                    else {
                        Log.d("collectionStatus", "document not added", task.getException());
                    }
                });
    }

}