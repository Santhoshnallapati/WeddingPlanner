package com.example.weddingplanner;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventDetailsActivity extends AppCompatActivity {
    private TextView tvCustomerName, tvDate, tvTime, tvPlace, tvDescription, tvEventManager, tvBudgetRange, tvPaymentStatus;
    private DatabaseReference mDatabase, mManagersDatabase, mUsersDatabase;
    private String eventId, managerId, customerId;
    Button btnBack, btnPay;
    String PublishableKey = "xxx";
    String SecretKey = "xxx";
    String CustomerId;
    String ClientSecret;
    String EphemeralKey;
    PaymentSheet paymentSheet;
    public String PaymentAmount;

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
        btnBack = findViewById(R.id.btnBack);
        btnPay = findViewById(R.id.btnPay);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentFlow();
            }
        });

        PaymentConfiguration.init(this, PublishableKey);
        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {
            onPaymetResult(paymentSheetResult);
        });
        getCustomerId();

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
                        PaymentAmount = event.getBudgetRange();
                        tvPaymentStatus.setText(event.getPaymentStatus());

                        if(event.getPaymentStatus().equals("Not completed")){
                            btnPay.setVisibility(View.VISIBLE);
                        } else {
                            btnPay.setVisibility(View.GONE);
                        }


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

    private void getCustomerId() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            CustomerId = object.getString("id");
                            //Toast.makeText(SettingsActivity.this, "CustomerId:" + CustomerId, Toast.LENGTH_SHORT).show();
                            getEphemeralKeys();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EventDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+ SecretKey);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void paymentFlow() {
        paymentSheet.presentWithPaymentIntent(ClientSecret, new PaymentSheet.Configuration("Santhosh", new PaymentSheet.CustomerConfiguration(
                CustomerId,
                EphemeralKey
        )));
    }

    private void onPaymetResult(PaymentSheetResult paymentSheetResult) {
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            tvPaymentStatus.setText("Payment Done");
            btnPay.setVisibility(View.GONE);
            mDatabase.child(eventId).child("paymentStatus").setValue("payment done")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Successfully updated the payment status
                            Toast.makeText(EventDetailsActivity.this, "Payment status updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to update the payment status
                            Toast.makeText(EventDetailsActivity.this, "Failed to update payment status", Toast.LENGTH_SHORT).show();
                        }
                    });
            Toast.makeText(this, "Payment success", Toast.LENGTH_SHORT).show();
        }
    }

    private void getEphemeralKeys() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            EphemeralKey = object.getString("id");
                            // Toast.makeText(SettingsActivity.this, "EphemeralKey:" + EphemeralKey, Toast.LENGTH_SHORT).show();
                            getClientSecret(CustomerId,EphemeralKey);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EventDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+ SecretKey);
                header.put("Stripe-Version" , "2024-06-20");
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer",CustomerId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void getClientSecret(String customerId, String ephemeralKey) {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");
                            // Toast.makeText(SettingsActivity.this, "ClientSecret:" + ClientSecret, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EventDetailsActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization", "Bearer "+ SecretKey);
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer",CustomerId);
                params.put("amount",PaymentAmount+"00");
                params.put("currency","cad");
                params.put("automatic_payment_methods[enabled]","true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}