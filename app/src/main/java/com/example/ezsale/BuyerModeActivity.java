package com.example.ezsale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.Objects;

public class BuyerModeActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView buyerItemsList;
    private FirestoreRecyclerAdapter adapter;
    String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_mode);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        buyerItemsList = findViewById(R.id.buyer_listings);

        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DocumentReference docRef = db.collection("users").document(currentUser);
        docRef.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()) {
                    Log.d("userInformation", "DocumentSnapshot data: " + document.getData());
                    userName = (String) Objects.requireNonNull(document.getData()).get("userName");
                }
                else {
                    Log.d("userInformation", "No such document");
                }
            }
            else {
                Log.d("userInformation", "get failed with ", task.getException());
            }
        });

        //Query query = db.collectionGroup("User's Listings");
        Query query = db.collectionGroup("User's Listings").whereNotEqualTo("author", userName);
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
                holder.itemName.setText(model.getName());
                holder.itemDesc.setText(model.getDescription());
                holder.itemCost.setText(model.getCost());
                holder.zipcode.setText(model.getZipcode());

            }
        };

        buyerItemsList.setHasFixedSize(true);
        buyerItemsList.setLayoutManager(new LinearLayoutManager(this));
        buyerItemsList.setAdapter(adapter);

    }

    private class BuyerItemsViewHolder extends RecyclerView.ViewHolder {

        private TextView itemName, itemDesc, itemCost, zipcode;
        private Button contactSeller;

        public BuyerItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.buyer_item_placeholder);
            itemDesc = itemView.findViewById(R.id.buyer_description_placeholder);
            itemCost = itemView.findViewById(R.id.buyer_amount_placeholder);
            zipcode = itemView.findViewById(R.id.buyer_zip_placeholder);
            contactSeller = itemView.findViewById(R.id.contact_seller_button);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}