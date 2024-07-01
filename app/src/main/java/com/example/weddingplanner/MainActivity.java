package com.example.weddingplanner;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mEventsDatabase, mManagersDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private TextView tvNoData;
    private List<Event> events;
    private EventAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Event Planner");
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        checkUser();

        mEventsDatabase = FirebaseDatabase.getInstance().getReference("events");
        tvNoData = findViewById(R.id.tvNoData);

        ListView listView = findViewById(R.id.listView);
        events = new ArrayList<>();
        adapter = new EventAdapter(this, events);
        listView.setAdapter(adapter);

        if (currentUser != null) {
            fetchEvents(currentUser.getUid());
        }
    }

    private void fetchEvents(String currentUserId) {
        mEventsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events.clear();
                boolean eventsFound = false;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    if (event != null && event.getCustomerId().equals(currentUserId)) {
                        event.setId(snapshot.getKey());
                        events.add(event);
                        eventsFound = true;
                    }
                }

                if (eventsFound) {
                    tvNoData.setVisibility(View.GONE);
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MainActivity", "Database error: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                profile();
                return true;
            case R.id.menu_sign_out:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkUser() {
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Log.d(TAG, "user not signed in");
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (firebaseAuth.getCurrentUser().getEmail().equals("admin@gmail.com")) {
                        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d(TAG, "user signed in");
                    }
                }
            }
        });
    }

    public void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    public void profile() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}
