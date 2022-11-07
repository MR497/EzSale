package com.example.ezsale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SendEmailActivity extends AppCompatActivity {
    private EditText emailAddress, emailSubject, emailMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        emailAddress = findViewById(R.id.email_address_input);
        emailSubject = findViewById(R.id.email_subject_input);
        emailMessage = findViewById(R.id.email_message_input);

        Intent i = getIntent();
        String getEmail = i.getExtras().getString("emailAddress");
        emailAddress.setText(getEmail);

        Button send = findViewById(R.id.send_email_btn);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail();
            }
        });

    }

    private void sendMail() {
        String email = emailAddress.getText().toString();
        String subject = emailSubject.getText().toString();
        String message = emailMessage.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose An Email Client"));
    }

}