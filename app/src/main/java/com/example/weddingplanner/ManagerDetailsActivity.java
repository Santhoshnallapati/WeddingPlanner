package com.example.weddingplanner;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ManagerDetailsActivity extends AppCompatActivity {

    private TextView tvManagerName, tvManagerBudgetRange, tvManagerRatings, tvPreviousEvents;
    private DatabaseReference mManagersDatabase;
    private String managerId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_details);

        tvManagerName = findViewById(R.id.tvManagerName);
        tvManagerBudgetRange = findViewById(R.id.tvManagerBudgetRange);
        tvManagerRatings = findViewById(R.id.tvManagerRatings);
        tvPreviousEvents = findViewById(R.id.tvPreviousEvents);
        Button btnBack = findViewById(R.id.btnBack);

        mManagersDatabase = FirebaseDatabase.getInstance().getReference("managers");

        managerId = getIntent().getStringExtra("managerId");
        if (managerId != null) {
            mManagersDatabase.child(managerId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Manager manager = dataSnapshot.getValue(Manager.class);
                    if (manager != null){
                        tvManagerName.setText(manager.getName());
                        tvManagerBudgetRange.setText(manager.getBudgetRange());
                        tvManagerRatings.setText(manager.getRatings());
                        tvPreviousEvents.setText(manager.getPreviousEvents());
                    } else {
                        // Manager with given ID not found
                        Toast.makeText(ManagerDetailsActivity.this, "Manager not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors
                    Toast.makeText(ManagerDetailsActivity.this, "Failed to load manager details", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // No managerId provided
            Toast.makeText(this, "Manager ID not provided", Toast.LENGTH_SHORT).show();
        }

        // Set click listener to the back button
        btnBack.setOnClickListener(v -> finish());
    }

}