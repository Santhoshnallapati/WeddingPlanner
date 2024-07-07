package com.example.weddingplanner;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class EventAdapter extends BaseAdapter {

    private Context context;
    private List<Event> events;
    private DatabaseReference  mUsersDatabase;

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
        mUsersDatabase = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        final Event event = events.get(position);

        TextView tvCustomerName = convertView.findViewById(R.id.tvCustomerName);
        TextView tvDate = convertView.findViewById(R.id.tvDate);
        TextView tvPlace = convertView.findViewById(R.id.tvPlace);
        Button btnView = convertView.findViewById(R.id.btnView);


        // Retrieve the event customer's ID
        String customerId = event.getCustomerId();
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
        tvDate.setText(event.getDate());
        tvPlace.setText(event.getPlace());

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetailsActivity.class);
                intent.putExtra("eventId", event.getId());
                context.startActivity(intent);
            }
        });


        return convertView;
    }
}
