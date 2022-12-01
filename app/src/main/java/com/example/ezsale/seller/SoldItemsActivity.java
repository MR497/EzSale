package com.example.ezsale.seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ezsale.R;
import com.example.ezsale.models.SoldListingsModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class SoldItemsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView soldItemsList;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold_items);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sold Items");

        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        db = FirebaseFirestore.getInstance();
        soldItemsList = findViewById(R.id.sold_items_listing);

        Query query = db.collection("Sold Items").document(currentUser).collection("User's Sold Items");
        FirestoreRecyclerOptions<SoldListingsModel> options = new FirestoreRecyclerOptions.Builder<SoldListingsModel>()
                .setQuery(query, SoldListingsModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<SoldListingsModel, SoldItemsViewHolder>(options) {
            @NonNull
            @Override
            public SoldItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_sold_items, parent, false);
                return new SoldItemsViewHolder(view) ;
            }

            @Override
            protected void onBindViewHolder(@NonNull SoldItemsViewHolder holder, int position, @NonNull SoldListingsModel model) {
                holder.soldItemName.setText(model.getName());
                holder.soldItemDesc.setText(model.getDescription());
                holder.soldItemCost.setText(model.getCost());
                holder.soldItemZipcode.setText(model.getZipcode());
                holder.soldItemDate.setText(model.getDate());
                String pictureURL = model.getPicture();
                Picasso.get().load(pictureURL).into(holder.soldItemImage);
            }
        };

        soldItemsList.setHasFixedSize(true);
        soldItemsList.setLayoutManager(new LinearLayoutManager(this));
        soldItemsList.setAdapter(adapter);
    }

    private class SoldItemsViewHolder extends RecyclerView.ViewHolder {

        private TextView soldItemName, soldItemDesc, soldItemCost, soldItemZipcode, soldItemDate;
        private ImageView soldItemImage;

        public SoldItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            soldItemName = itemView.findViewById(R.id.sold_item_name_text);
            soldItemDesc = itemView.findViewById(R.id.sold_item_description_placeholder);
            soldItemCost = itemView.findViewById(R.id.sold_item_amount_placeholder);
            soldItemZipcode = itemView.findViewById(R.id.sold_item_zipcode_placeholder);
            soldItemDate = itemView.findViewById(R.id.sold_item_date_placeholder);
            soldItemImage = itemView.findViewById(R.id.sold_item_list_image);
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