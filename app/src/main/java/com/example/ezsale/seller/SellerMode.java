package com.example.ezsale.seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ezsale.R;
import com.example.ezsale.models.SellerListingsModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class SellerMode extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView sellerListings;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_mode);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button seeSoldItems = findViewById(R.id.see_sold_items_button);
        seeSoldItems.setOnClickListener(view -> startActivity(new Intent(SellerMode.this, SoldItemsActivity.class)));

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
                String pictureURL = model.getPicture();
                Picasso.get().load(pictureURL).into(holder.image);
                int pos = holder.getAbsoluteAdapterPosition();
                holder.deletePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog dialog = new AlertDialog.Builder(SellerMode.this)
                                .setTitle("Confirmation")
                                .setMessage("Delete Item Listing?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        getSnapshots().getSnapshot(pos).getReference().delete();
                                        Toast.makeText(SellerMode.this, "Item Listing Deleted", Toast.LENGTH_LONG).show();
                                        dialogInterface.dismiss();
                                        startActivity(getIntent());
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create();
                        dialog.show();
                    }
                });
                holder.soldPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog dialog = new AlertDialog.Builder(SellerMode.this)
                                .setTitle("Confirmation")
                                .setMessage("Mark this item as sold?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String soldItemName = (String) getSnapshots().getSnapshot(pos).getData().get("name");
                                        String soldItemDesc = (String) getSnapshots().getSnapshot(pos).getData().get("description");
                                        String soldItemCost = (String) getSnapshots().getSnapshot(pos).getData().get("cost");
                                        String soldItemZipcode = (String) getSnapshots().getSnapshot(pos).getData().get("zipcode");
                                        String soldItemPicture = (String) getSnapshots().getSnapshot(pos).getData().get("picture");
                                        createSoldItemCollection(currentUser, soldItemName, soldItemDesc, soldItemCost, soldItemZipcode, soldItemPicture);
                                        getSnapshots().getSnapshot(pos).getReference().delete();
                                        dialogInterface.dismiss();
                                        startActivity(getIntent());
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create();
                        dialog.show();
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
        private Button deletePost, soldPost;
        private ImageView image;

        public SellerListingViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_placeholder);
            itemDesc = itemView.findViewById(R.id.description_placeholder);
            itemCost = itemView.findViewById(R.id.amount_placeholder);
            zipcode = itemView.findViewById(R.id.zip_placeholder);
            deletePost = itemView.findViewById(R.id.delete_post_button);
            soldPost = itemView.findViewById(R.id.sold_post_button);
            image = itemView.findViewById(R.id.sellerView_list_image);
        }
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    private void createSoldItemCollection (String currentUser, String name, String desc, String cost, String zipcode, String picture){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateFormat.format(cal.getTime());

        DocumentReference docRef = db.collection("users").document(currentUser);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("userInformation", "DocumentSnapshot data: " + document.getData());
                    String userName = (String) Objects.requireNonNull(document.getData()).get("userName");

                    HashMap<String, String> salesMap = new HashMap<>();
                    salesMap.put("author", userName);
                    salesMap.put("name", name);
                    salesMap.put("description", desc);
                    salesMap.put("cost", cost);
                    salesMap.put("zipcode", zipcode);
                    salesMap.put("date", date);
                    salesMap.put("picture", picture);

                    db.collection("Sold Items").document(currentUser).collection("User's Sold Items").document()
                            .set(salesMap).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(SellerMode.this, "Item Marked As Sold", Toast.LENGTH_LONG).show();

                                } else {
                                    Toast.makeText(SellerMode.this, "Oops an error has occurred! Please try again", Toast.LENGTH_LONG).show();
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