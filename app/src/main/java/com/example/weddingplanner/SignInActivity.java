package com.example.weddingplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button btn_signIn, btn_register;
    TextView tv_forgotPassword;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        editTextEmail = findViewById(R.id.etEmail);
        editTextPassword = findViewById(R.id.etPassword);
        btn_signIn = findViewById(R.id.btnSignIn);
        btn_register = findViewById(R.id.btnRegister);
        tv_forgotPassword = findViewById(R.id.tvForgotPassword);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                if(!email.isEmpty()){
                    if (!password.isEmpty()){
                        if(password.length() > 5){
                            mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(SignInActivity.this, "Sign in success", Toast.LENGTH_SHORT).show();
                                    goHome(email);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignInActivity.this, "Sign in failed try again"+ e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
                            editTextPassword.setError("6 characters need");
                            editTextPassword.requestFocus();
                        }


                    }else {
                        editTextPassword.setError("Password can't be empty");
                        editTextPassword.requestFocus();
                    }


                }else{
                    editTextEmail.setError("Email can't be empty");
                    editTextEmail.requestFocus();
                }


            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        tv_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    public void goHome(String email){
        Intent intent;
        if (email.equals("admin@gmail.com")) {
            intent = new Intent(SignInActivity.this, AdminActivity.class);
        }else {
            intent = new Intent(SignInActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
