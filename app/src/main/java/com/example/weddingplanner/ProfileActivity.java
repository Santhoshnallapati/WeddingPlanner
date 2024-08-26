package com.example.weddingplanner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvUsername, tvEmail;
    private ImageView ivProfilePhoto;
    private Button btnDeleteAccount,btnEditAccount, btnBack;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mUsersDatabase;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnBack = findViewById(R.id.btnBack);
        btnEditAccount = findViewById(R.id.btnEditAccount);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference("users");

        if (currentUser != null) {
            tvUsername.setText(currentUser.getDisplayName());
            tvEmail.setText(currentUser.getEmail());
//            if (currentUser.getPhotoUrl() != null) {
//                Glide.with(this).load(currentUser.getPhotoUrl()).into(ivProfilePhoto);
//            }
            // Retrieve the event customer's ID
            String customerId = currentUser.getUid();
            if (customerId != null) {
                // Fetch customer details using customerId
                mUsersDatabase.child(customerId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                        if (userSnapshot.exists()) {
                            String customerName = userSnapshot.child("name").getValue(String.class);
                            tvUsername.setText(customerName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors
                    }
                });
            }
        }


        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAccount();
            }
        });
    }

    private void editAccount() {
        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }


    private void deleteAccount() {
        if (currentUser != null) {
            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            currentUser.delete()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProfileActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish(); // Close the activity after account deletion
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }


}