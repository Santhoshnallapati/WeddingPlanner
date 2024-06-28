package com.example.weddingplanner;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mEventsDatabase, mManagersDatabase;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private TextView tvNoData;

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
        mManagersDatabase = FirebaseDatabase.getInstance().getReference("managers");
        tvNoData = findViewById(R.id.tvNoData);


        ListView listView = findViewById(R.id.listView);
        List<Event> events = new ArrayList<>();


//        events.add(new Event("John Doe", "2024-07-15", "18:00", "Old Montreal", "A beautiful evening event at the historical Old Montreal."));
//        events.add(new Event("Jane Smith", "2024-08-20", "15:30", "Central Park", "A public gathering in Central Park with activities for all ages."));
//        events.add(new Event("David Johnson", "2024-09-10", "12:00", "Grand Plaza Hotel", "A corporate conference at the Grand Plaza Hotel, featuring keynote speakers and networking opportunities."));
//        events.add(new Event("Emily Brown", "2024-08-05", "17:00", "Beachside Resort", "A music festival at the Beachside Resort, showcasing local and international artists."));
//        events.add(new Event("Michael Wilson", "2024-07-30", "14:00", "Riverside Gardens", "A charity fundraiser at the Riverside Gardens, raising awareness and funds for a cause."));
//        events.add(new Event("Sarah Davis", "2024-09-15", "16:30", "Mountain View Lodge", "A retreat at the Mountain View Lodge, offering workshops and activities for personal growth."));
//        events.add(new Event("Robert Miller", "2024-08-12", "18:30", "Skyline Ballroom", "A gala dinner at the Skyline Ballroom, honoring achievements and contributions in the community."));
//        events.add(new Event("Jessica Taylor", "2024-09-05", "13:45", "Lakeview Pavilion", "A trade show at the Lakeview Pavilion, featuring exhibitors from various industries."));
//        events.add(new Event("William Martinez", "2024-07-25", "11:00", "Garden Estate", "A garden party at the Garden Estate, celebrating a special occasion with friends and family."));
//        events.add(new Event("Olivia White", "2024-08-18", "16:15", "Riverfront Park", "A cultural festival at Riverfront Park, showcasing music, dance, and cuisine from around the world."));
//        events.add(new Event("Daniel Anderson", "2024-09-08", "14:45", "City Hall", "A town hall meeting at City Hall, discussing important community issues and initiatives."));
//        events.add(new Event("Sophia Clark", "2024-08-28", "17:45", "The Manor House", "A historical reenactment at The Manor House, bringing the past to life with period costumes and activities."));
//        events.add(new Event("Ethan Thomas", "2024-07-22", "12:30", "Botanical Gardens", "A nature walk at the Botanical Gardens, exploring the diverse plant life and ecosystems."));
//        events.add(new Event("Mia Rodriguez", "2024-09-20", "15:00", "Oceanfront Resort", "A beach cleanup at the Oceanfront Resort, promoting environmental conservation and sustainability."));
//        events.add(new Event("James Wilson", "2024-08-10", "18:45", "Rooftop Terrace", "A rooftop party with a city view, featuring music, drinks, and panoramic views of the skyline."));
//        events.add(new Event("Amelia Garcia", "2024-07-28", "14:15", "Country Club", "A golf tournament at the Country Club, raising funds for local charities."));
//        events.add(new Event("Benjamin Martinez", "2024-09-12", "16:00", "Vineyard Estate", "A wine tasting event at the Vineyard Estate, sampling wines from local vineyards."));
//        events.add(new Event("Charlotte Lee", "2024-08-08", "13:30", "Mansion Gardens", "A historical tour of the Mansion Gardens, exploring the architecture and history of the estate."));
//        events.add(new Event("Henry Young", "2024-07-18", "17:30", "Lakeside Lodge", "A fishing derby at the Lakeside Lodge, competing for prizes and enjoying the outdoors."));
//        events.add(new Event("Grace Brown", "2024-09-02", "12:15", "Park Pavilion", "A picnic at the Park Pavilion, enjoying food and games in a scenic park setting."));
//        events.add(new Event("Andrew Taylor", "2024-08-22", "18:15", "Chapel Hill", "A community concert at Chapel Hill, featuring local musicians and performers."));
//        for (Event event : events) {
//            String eventId = mDatabase.push().getKey();
//            event.setId(eventId);
//            mEventsDatabase.child(eventId).setValue(event);
//        }

//        List<Manager> managers = new ArrayList<>();
//        managers.add(new Manager("Benjamin Martinez", "$1000 - $2000", "4.5", "A wine tasting event at the Vineyard Estate, sampling wines from local vineyards."));
//        managers.add(new Manager("Charlotte Lee", "$1500 - $2500", "4.8", "A historical tour of the Mansion Gardens, exploring the architecture and history of the estate."));
//        managers.add(new Manager("Henry Young", "$1200 - $2200", "4.2", "A fishing derby at the Lakeside Lodge, competing for prizes and enjoying the outdoors."));
//        managers.add(new Manager("Grace Brown", "$800 - $1500", "4.6", "A picnic at the Park Pavilion, enjoying food and games in a scenic park setting."));
//        managers.add(new Manager("Andrew Taylor", "$2000 - $3000", "4.9", "A community concert at Chapel Hill, featuring local musicians and performers."));
//
//        for (Manager manager : managers) {
//            String managerId = mManagersDatabase.push().getKey();
//            manager.setId(managerId);
//            mManagersDatabase.child(managerId).setValue(manager);
//        }


        EventAdapter adapter = new EventAdapter(this, events);
        listView.setAdapter(adapter);
        // Assuming you have the current user ID stored in a variable
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mEventsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Event event = snapshot.getValue(Event.class);
                        if (event != null && event.getCustomerId().equals(currentUserId)) {
                            event.setId(snapshot.getKey());
                            events.add(event);
                            tvNoData.setVisibility(View.GONE);
                        }else {
                            tvNoData.setVisibility(View.VISIBLE);
                        }
                    }

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
                if(firebaseAuth.getCurrentUser() == null){
                    Log.d(TAG,"user not sign");
                    Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    if (firebaseAuth.getCurrentUser().getEmail().equals("admin@gmail.com")) {
                        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        Log.d(TAG,"user signed");
                    }
                }
            }
        });
    }
    public void signOut(){
        Intent intent = new Intent(MainActivity.this, SignOutActivity.class);
        startActivity(intent);
    }
    public void profile(){
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }



}