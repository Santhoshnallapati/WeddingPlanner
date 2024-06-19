package com.example.weddingplanner;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editTextUsername;
    Button btn_signUp, btn_login;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        editTextEmail = findViewById(R.id.etEmail);
        editTextPassword = findViewById(R.id.etPassword);
        editTextUsername = findViewById(R.id.etUsername);
        btn_signUp = findViewById(R.id.btnSignUp);
        btn_login = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference("users");


        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = editTextUsername.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if(username.isEmpty() || email.isEmpty() || password.isEmpty()){
                    Toast.makeText(SignUpActivity.this, "Please fill all the details ", Toast.LENGTH_SHORT).show();
                } else {
                   mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                       @Override
                       public void onSuccess(AuthResult authResult) {
                           Toast.makeText(SignUpActivity.this, "Sign up success", Toast.LENGTH_SHORT).show();
                           FirebaseUser user = mAuth.getCurrentUser();
                           if (user != null) {
                               // Write user details to Firebase Database
                               writeUserDetails(user.getUid(), email, username);
                           }
                           goHome(email);
                       }


                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(SignUpActivity.this, "Sign Up failed", Toast.LENGTH_SHORT).show();

                       }
                   });
                }
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void writeUserDetails(String userId, String email, String name) {
        User user = new User(userId, name);
        mUsersDatabase.child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "User details saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Failed to save user details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void goHome(String email) {
        Intent intent;
        if (email.equals("admin@gmail.com")) {
            intent = new Intent(SignUpActivity.this, AdminActivity.class);
        } else {
            intent = new Intent(SignUpActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
