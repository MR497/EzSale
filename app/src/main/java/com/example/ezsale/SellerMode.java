package com.example.ezsale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class SellerMode extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView sellerListings;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_mode);

        Button sellNewItem = findViewById(R.id.seller_mode_sell_button);
        sellNewItem.setOnClickListener(view -> startActivity(new Intent(SellerMode.this, CreateSaleActivity.class)));

        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        db = FirebaseFirestore.getInstance();
        sellerListings = findViewById(R.id.seller_listings);

        Query query = db.collection("Items Being Sold").document(currentUser).collection("User's Listings");
        FirestoreRecyclerOptions<SellerListingsModel> options = new FirestoreRecyclerOptions.Builder<SellerListingsModel>()
                .setQuery(query, SellerListingsModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<SellerListingsModel, SellerListingViewHolder>(options) {
            @NonNull
            @Override
            public SellerListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_seller_items, parent, false);
                return new SellerListingViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull SellerListingViewHolder holder, int position, @NonNull SellerListingsModel model) {
                holder.itemName.setText(model.getName());
                holder.itemDesc.setText(model.getDescription());
                holder.itemCost.setText(model.getCost());
                holder.zipcode.setText(model.getZipcode());
                int pos = holder.getAbsoluteAdapterPosition();
                holder.deletePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getSnapshots().getSnapshot(pos).getReference().delete();
                    }
                });
            }
        };

        sellerListings.setHasFixedSize(true);
        sellerListings.setLayoutManager(new LinearLayoutManager(this));
        sellerListings.setAdapter(adapter);

    }

    private class SellerListingViewHolder extends RecyclerView.ViewHolder{

        private TextView itemName, itemDesc, itemCost, zipcode;
        private Button deletePost;

        public SellerListingViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.item_placeholder);
            itemDesc = itemView.findViewById(R.id.description_placeholder);
            itemCost = itemView.findViewById(R.id.amount_placeholder);
            zipcode = itemView.findViewById(R.id.zip_placeholder);
            deletePost = itemView.findViewById(R.id.delete_post_button);
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