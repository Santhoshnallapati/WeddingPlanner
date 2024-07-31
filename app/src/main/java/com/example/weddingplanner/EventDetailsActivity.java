package com.example.weddingplanner;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventDetailsActivity extends AppCompatActivity {
    private TextView tvCustomerName, tvDate, tvTime, tvPlace, tvDescription, tvEventManager, tvBudgetRange, tvPaymentStatus;
    private DatabaseReference mDatabase, mManagersDatabase, mUsersDatabase;
    private String eventId, managerId, customerId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Initialize UI components
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvPlace = findViewById(R.id.tvPlace);
        tvDescription = findViewById(R.id.tvDescription);
        tvEventManager = findViewById(R.id.tvEventManager);
        tvBudgetRange = findViewById(R.id.tvBudgetRange);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        Button btnBack = findViewById(R.id.btnBack);

        // Initialize Firebase Database references
        mDatabase = FirebaseDatabase.getInstance().getReference("events");
        mManagersDatabase = FirebaseDatabase.getInstance().getReference("managers");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference("users");

        // Retrieve eventId from the Intent
        eventId = getIntent().getStringExtra("eventId");
        if (eventId != null) {
            // Fetch event details from the database
            mDatabase.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Event event = dataSnapshot.getValue(Event.class);
                    if (event != null) {
                        tvCustomerName.setText(event.getName());
                        tvDate.setText(event.getDate());
                        tvTime.setText(event.getTime());
                        tvPlace.setText(event.getPlace());
                        tvDescription.setText(event.getDescription());
                        tvBudgetRange.setText(event.getBudgetRange());
                        tvPaymentStatus.setText(event.getPaymentStatus());

                        // Retrieve the event manager's ID
                        managerId = event.getManagerId();
                        if (managerId != null) {
                            // Fetch manager details using managerId
                            mManagersDatabase.child(managerId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot managerSnapshot) {
                                    if (managerSnapshot.exists()) {
                                        String managerName = managerSnapshot.child("name").getValue(String.class);
                                        tvEventManager.setText(managerName);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle possible errors
                                }
                            });
                        }

                        // Retrieve the event customer's ID
                        customerId = event.getCustomerId();
                        if (customerId != null) {
                            // Fetch customer details using customerId
                            mUsersDatabase.child(customerId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                    if (userSnapshot.exists()) {
                                        String customerName = userSnapshot.child("name").getValue(String.class);
                                        tvCustomerName.setText(customerName);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle possible errors
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle possible errors
                }
            });
        }

        // Set click listener to view manager details
        tvEventManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (managerId != null) {
                    Intent intent = new Intent(EventDetailsActivity.this, ManagerDetailsActivity.class);
                    intent.putExtra("managerId", managerId);
                    startActivity(intent);
                }
            }
        });

        // Set click listener to the back button
        btnBack.setOnClickListener(v -> finish());
    }
}