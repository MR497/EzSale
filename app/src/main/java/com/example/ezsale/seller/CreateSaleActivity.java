package com.example.ezsale.seller;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ezsale.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;

public class CreateSaleActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private Uri imageUri;
    //private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sale);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db =  FirebaseFirestore.getInstance();

        Spinner stateList = (Spinner) findViewById(R.id.create_sale_state_spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(CreateSaleActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.states));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateList.setAdapter(myAdapter);

        Button uploadPic = findViewById(R.id.upload_pic_btn);
        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        Button postSaleButton = findViewById(R.id.post_sale_button);
        postSaleButton.setOnClickListener(view -> {
            String itemName = ((EditText)findViewById(R.id.item_name_text)).getText().toString();
            String itemDesc = ((EditText)findViewById(R.id.item_description_text)).getText().toString();
            String itemCost = ((EditText)findViewById(R.id.item_cost_text)).getText().toString();
            String city = ((EditText)findViewById(R.id.zipcode_text)).getText().toString().toUpperCase();
            String state = stateList.getSelectedItem().toString();

            String zipcode = city+", "+state;

            if(TextUtils.isEmpty(itemName) || TextUtils.isEmpty(itemDesc) || TextUtils.isEmpty(itemCost)
                    || TextUtils.isEmpty(zipcode)) {
                Toast.makeText(CreateSaleActivity.this, "Form is incomplete!", Toast.LENGTH_LONG).show();
            }
            else {
                String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                final ProgressDialog pd = new ProgressDialog(this);
                pd.setMessage("Putting Item Up For Sale...");
                pd.show();

                if(imageUri != null) {
                    final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("uploads/itemPics/" + currentUser + "/")
                            .child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

                    fileRef.putFile(imageUri).addOnCompleteListener(task -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String url = uri.toString();
                        createSale(itemName, itemDesc, itemCost, zipcode, url);
                        Log.d("DownloadURL", url);
                        pd.dismiss();
                    }));
                }
                //createSale(itemName, itemDesc, itemCost, zipcode);
            }
        });
    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        GalleryActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> GalleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageUri = Objects.requireNonNull(result.getData()).getData();
                        ImageView image = findViewById(R.id.item_picture);
                        image.setImageURI(imageUri);
                        //uploadImage();
                    }
                }
            });

    private String getFileExtension (Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

//    private void uploadImage() {
//        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//        final ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage("Uploading Picture...");
//        pd.show();
//
//        if(imageUri != null) {
//            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("uploads/itemPics/" + currentUser + "/")
//                    .child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
//
//            fileRef.putFile(imageUri).addOnCompleteListener(task -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                String url = uri.toString();
//                Log.d("DownloadURL", url);
//                pd.dismiss();
//            }));
//        }
//    }

    private void createSale(String itemName, String itemDesc, String itemCost, String zipcode, String url) {
        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DocumentReference docRef = db.collection("users").document(currentUser);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("userInformation", "DocumentSnapshot data: " + document.getData());
                    String userName = (String) Objects.requireNonNull(document.getData()).get("userName");
                    String email = (String) Objects.requireNonNull(document.getData()).get("email");

                    HashMap<String, String> userInfo = new HashMap<>();
                    userInfo.put("userName", userName);
                    db.collection("Items Being Sold").document(currentUser).set(userInfo);

                    HashMap<String, String> salesMap = new HashMap<>();
                    salesMap.put("email", email);
                    salesMap.put("author", userName);
                    salesMap.put("name", itemName);
                    salesMap.put("description", itemDesc);
                    salesMap.put("cost", itemCost);
                    salesMap.put("zipcode", zipcode);
                    salesMap.put("picture", url);
                    db.collection("Items Being Sold").document(currentUser).collection("User's Listings").document()
                            .set(salesMap).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(CreateSaleActivity.this, "Item Put Up For Sale!", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(CreateSaleActivity.this, SellerMode.class));
                                    finish();
                                } else {
                                    Toast.makeText(CreateSaleActivity.this, "Oops an error has occurred! Please try again", Toast.LENGTH_LONG).show();
                                }
                            });
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
