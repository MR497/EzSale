package com.example.ezsale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import android.widget.Filter;
import android.widget.Filterable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class BuyerModeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView buyerItemsList;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_mode);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        buyerItemsList = findViewById(R.id.buyer_listings);

        Spinner stateList = (Spinner) findViewById(R.id.buyer_list_spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(BuyerModeActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.states));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateList.setAdapter(myAdapter);

        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DocumentReference docRef = db.collection("users").document(currentUser);
        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()) {
                    Log.d("userInformation", "DocumentSnapshot data: " + document.getData());
                    String userName = (String) Objects.requireNonNull(document.getData()).get("userName");
                    ImageButton searchButton = findViewById(R.id.searchButton);
                    searchButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String city = ((EditText)findViewById(R.id.buyer_search_bar)).getText().toString().toUpperCase();
                            String state = stateList.getSelectedItem().toString();
                            String searchInput = city+", "+state;
                            setUpRecyclerView(userName, searchInput);
                        }
                    });
                    //setUpRecyclerView(userName);
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

    private void setUpRecyclerView(String userName,String searchInput){
        Query query = db.collectionGroup("User's Listings").whereNotEqualTo("author", userName).whereEqualTo("zipcode", searchInput);

        FirestoreRecyclerOptions<BuyerListingsModel> options = new FirestoreRecyclerOptions.Builder<BuyerListingsModel>()
                .setQuery(query, BuyerListingsModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<BuyerListingsModel, BuyerItemsViewHolder>(options) {
            @NonNull
            @Override
            public BuyerItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_buyer_items, parent, false);
                return new BuyerItemsViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull BuyerItemsViewHolder holder, int position, @NonNull BuyerListingsModel model) {
                holder.sellerName.setText(model.getAuthor());
                holder.itemName.setText(model.getName());
                holder.itemDesc.setText(model.getDescription());
                holder.itemCost.setText(model.getCost());
                holder.zipcode.setText(model.getZipcode());
                holder.contactSeller.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String sellerName = model.getAuthor();
                        String email = model.getEmail();
                        String itemNameEmail = model.getName();
                        String itemDescEmail = model.getDescription();
                        String itemCostEmail = model.getCost();
                        String itemLocationEmail = model.getZipcode();
                        Intent i = new Intent(BuyerModeActivity.this, SendEmailActivity.class);
                        i.putExtra("sellerName", sellerName);
                        i.putExtra("emailAddress", email);
                        i.putExtra("itemName", itemNameEmail);
                        i.putExtra("itemDesc", itemDescEmail);
                        i.putExtra("itemCost", itemCostEmail);
                        i.putExtra("itemLocation", itemLocationEmail);
                        startActivity(i);
                    }
                });
            }

        };
        buyerItemsList.setHasFixedSize(true);
        buyerItemsList.setLayoutManager(new LinearLayoutManager(this));
        buyerItemsList.setAdapter(adapter);
        adapter.startListening();
    }

    private class BuyerItemsViewHolder extends RecyclerView.ViewHolder {

        private TextView sellerName, itemName, itemDesc, itemCost, zipcode;
        private Button contactSeller;

        public BuyerItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            sellerName = itemView.findViewById(R.id.seller_name_placeholder);
            itemName = itemView.findViewById(R.id.buyer_item_placeholder);
            itemDesc = itemView.findViewById(R.id.buyer_description_placeholder);
            itemCost = itemView.findViewById(R.id.buyer_amount_placeholder);
            zipcode = itemView.findViewById(R.id.buyer_zip_placeholder);
            contactSeller = itemView.findViewById(R.id.contact_seller_button);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }
}