package com.example.ezsale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.auth.User;

import java.util.Objects;

public class UserModesActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recentContactsList;
    private FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_modes);

        recentContactsList = findViewById(R.id.recent_contacts_list);

        String currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        db = FirebaseFirestore.getInstance();

        getUserData(currentUser);

        Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(UserModesActivity.this, "Logged Out!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(UserModesActivity.this, MainActivity.class));
            finish();
        });

        Button sellButton = findViewById(R.id.user_mode_sell_button);
        sellButton.setOnClickListener(view -> startActivity(new Intent(UserModesActivity.this, SellerMode.class)));

        Button buyButton = findViewById(R.id.user_mode_buy_button);
        buyButton.setOnClickListener(view -> startActivity(new Intent(UserModesActivity.this, BuyerModeActivity.class)));

        Query query = db.collection("Contacted Sellers").document(currentUser).collection("Sellers Emailed");
        FirestoreRecyclerOptions<RecentContactsListingModel> options = new FirestoreRecyclerOptions.Builder<RecentContactsListingModel>()
                .setQuery(query, RecentContactsListingModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<RecentContactsListingModel, ContactsViewHolder>(options) {
            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_contacts_items, parent, false);
                return new ContactsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ContactsViewHolder holder, int position, @NonNull RecentContactsListingModel model) {
                holder.item.setText(model.getItem());
                holder.seller.setText(model.getSeller());
                holder.date.setText(model.getDate());
                int pos = holder.getAbsoluteAdapterPosition();
                holder.deleteBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog dialog = new AlertDialog.Builder(UserModesActivity.this)
                                .setTitle("Confirmation")
                                .setMessage("Delete this recent contact? Action Cannot be undone")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        getSnapshots().getSnapshot(pos).getReference().delete();
                                        Toast.makeText(UserModesActivity.this, "Recent Contact Deleted", Toast.LENGTH_LONG).show();
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

        recentContactsList.setHasFixedSize(true);
        recentContactsList.setLayoutManager(new LinearLayoutManager(this));
        recentContactsList.setAdapter(adapter);
    }

    private void getUserData(String userId) {
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(task -> {
           if(task.isSuccessful()) {
               DocumentSnapshot document = task.getResult();
               if(document.exists()) {
                   Log.d("userInformation", "DocumentSnapshot data: " + document.getData());
                   String userName = (String) Objects.requireNonNull(document.getData()).get("userName");
                   ((TextView) findViewById(R.id.current_user_welcome)).setText("Welcome " +userName);
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

    private class ContactsViewHolder extends RecyclerView.ViewHolder {

        private TextView date, item, seller;
        private Button deleteBTN;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.contacts_date_placeholder);
            item = itemView.findViewById(R.id.contacts_item_name);
            seller = itemView.findViewById(R.id.contacts_seller_placeholder);
            deleteBTN = itemView.findViewById(R.id.contacts_delete_btn);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}