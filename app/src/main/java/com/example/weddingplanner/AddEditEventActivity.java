package com.example.weddingplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddEditEventActivity extends AppCompatActivity {
    private Spinner spinnerCustomerName, spinnerEventManager;
    private EditText etDate, etTime, etPlace, etDescription, etBudgetRange;
    private TextView tvPaymentStatus;
    private  String PaymentStatus_value;
    private Button btnSave, btnBack;
    private DatabaseReference mDatabase, mManagersDatabase, mUsersDatabase;
    private String eventId;
    private List<String> managerNames;
    private List<String> customerNames;
    private List<Manager> managersList;
    private List<User> usersList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_event);

        spinnerCustomerName = findViewById(R.id.spinnerCustomerName);
        spinnerEventManager = findViewById(R.id.spinnerEventManager);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etPlace = findViewById(R.id.etPlace);
        etDescription = findViewById(R.id.etDescription);
        etBudgetRange = findViewById(R.id.etBudgetRange);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        mDatabase = FirebaseDatabase.getInstance().getReference("events");
        mManagersDatabase = FirebaseDatabase.getInstance().getReference("managers");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference("users");

        managerNames = new ArrayList<>();
        customerNames = new ArrayList<>();
        managersList = new ArrayList<>();
        usersList = new ArrayList<>();

        // Load managers and customers from Firebase
        loadManagers();
        loadCustomers();

        // Check if event ID was passed for editing an existing event
        eventId = getIntent().getStringExtra("eventId");
        if (eventId != null) {
            loadEventDetails(eventId);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEvent();
            }
        });

        btnBack.setOnClickListener(v -> finish());

        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current time
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddEditEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // Format the time and set it to the EditText
                                String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                                etTime.setText(formattedTime);
                            }
                        },
                        hour, minute, true); // Set to true for 24-hour format

                timePickerDialog.show();
            }
        });
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current date
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddEditEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Set the selected date to the EditText
                                etDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        },
                        year, month, day);

                // Set the minimum date to the current date
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

                datePickerDialog.show();
            }
        });
    }

    private void loadManagers() {
        mManagersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                managerNames.clear();
                managersList.clear();
                for (DataSnapshot managerSnapshot : dataSnapshot.getChildren()) {
                    Manager manager = managerSnapshot.getValue(Manager.class);
                    if (manager != null) {
                        managerNames.add(manager.getName());
                        managersList.add(manager);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddEditEventActivity.this, android.R.layout.simple_spinner_item, managerNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerEventManager.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddEditEventActivity.this, "Failed to load managers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCustomers() {
        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customerNames.clear();
                usersList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        if(!user.getId().equals("eyJwQRDFwiaLQQXzxillIeIuIPG2")){
                            customerNames.add(user.getName());
                            usersList.add(user);
                        }

                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddEditEventActivity.this, android.R.layout.simple_spinner_item, customerNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCustomerName.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddEditEventActivity.this, "Failed to load customers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEventDetails(String eventId) {
        mDatabase.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                if (event != null) {
                    etDate.setText(event.getDate());
                    etTime.setText(event.getTime());
                    etPlace.setText(event.getPlace());
                    etDescription.setText(event.getDescription());
                    etBudgetRange.setText(event.getBudgetRange());
                    tvPaymentStatus.setText(event.getPaymentStatus());
                    PaymentStatus_value = event.getPaymentStatus();

                    // Set the customer in the spinner
                    for (int i = 0; i < usersList.size(); i++) {
                        if (usersList.get(i).getId().equals(event.getCustomerId())) {
                            spinnerCustomerName.setSelection(i);
                            break;
                        }
                    }

                    // Set the manager in the spinner
                    for (int i = 0; i < managersList.size(); i++) {
                        if (managersList.get(i).getId().equals(event.getManagerId())) {
                            spinnerEventManager.setSelection(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddEditEventActivity.this, "Failed to load event details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveEvent() {
        String customerName = spinnerCustomerName.getSelectedItem().toString();
        String customerId = usersList.get(spinnerCustomerName.getSelectedItemPosition()).getId();
        String managerName = spinnerEventManager.getSelectedItem().toString();
        String managerId = managersList.get(spinnerEventManager.getSelectedItemPosition()).getId();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String place = etPlace.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String budget = etBudgetRange.getText().toString().trim();


        if (date.isEmpty() || time.isEmpty() || place.isEmpty() || description.isEmpty() || budget.isEmpty() || customerId.isEmpty() || managerId.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!validateBudget(budget)){
            etBudgetRange.setError("Invalid number");
            etBudgetRange.requestFocus();
            return;
        }
        if(!validateTime(time)){
            etTime.setError("Invalid time");
            etTime.requestFocus();
            return;
        }

        if(!validateDate(date)){
            etDate.setError("Invalid date");
            etDate.requestFocus();
            return;
        }

        if (eventId == null) {
            // Add new event
            String id = mDatabase.push().getKey();
            Event event = new Event(id, customerId, managerId, date, time, place, description, budget,"Not completed");
            if (id != null) {
                mDatabase.child(id).setValue(event);
            }
        } else {
            // Update existing event
            Event event = new Event(eventId, customerId, managerId, date, time, place, description, budget, PaymentStatus_value);
            mDatabase.child(eventId).setValue(event);
        }

        finish();
    }

    private boolean validateBudget(String budget) {
        String regex = "^[1-9]\\d*$";
        return budget.matches(regex);

    }

    private boolean validateDate(String date) {
        String regex = "^(19|20)\\d\\d-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\\d|3[01])$";
        return date.matches(regex);
    }

    private boolean validateTime(String time) {
        String regex = "^([01]\\d|2[0-3]):([0-5]\\d)$";
        return time.matches(regex);
    }
}