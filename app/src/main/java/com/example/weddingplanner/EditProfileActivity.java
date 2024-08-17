package com.example.weddingplanner;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mUsersDatabase;
    private String customerId;

    private EditText editTextUsername;
    private Button buttonSave, buttonBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference("users");

        editTextUsername = findViewById(R.id.editTextUsername);
        buttonBack = findViewById(R.id.btnBack);
        buttonSave = findViewById(R.id.btnSaveAccount);

        if (currentUser != null) {
            customerId = currentUser.getUid();
            mUsersDatabase.child(customerId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Set the current username in the EditText
                    String currentUsername = dataSnapshot.getValue(String.class);
                    if (currentUsername != null) {
                        editTextUsername.setText(currentUsername);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors
                    Toast.makeText(EditProfileActivity.this, "Failed to load username.", Toast.LENGTH_SHORT).show();
                }
            });
        }


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = editTextUsername.getText().toString().trim();


                if (!TextUtils.isEmpty(newUsername)) {
                    mUsersDatabase.child(customerId).child("name").setValue(newUsername)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditProfileActivity.this, "Username updated successfully.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditProfileActivity.this, "Failed to update username.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(EditProfileActivity.this, "Please enter a valid username.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}