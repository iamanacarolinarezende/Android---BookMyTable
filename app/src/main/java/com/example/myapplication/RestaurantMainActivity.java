package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMainActivity extends AppCompatActivity {

    TextView showEmail;
    FirebaseAuth firebaseAuth;
    RecyclerView pendingReservationsList, ongoingReservationsList;
    PendingReservationsAdapter pendingReservationAdapter;
    OnGoingReservationsAdapter ongoingReservationAdapter;

    List<Reservation> pendingReservations = new ArrayList<>(); // List for pending reservations
    List<Reservation> ongoingReservations = new ArrayList<>(); // List for ongoing reservations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurant_main);

        // Initialize RecyclerViews
        pendingReservationsList = findViewById(R.id.pendingReservationsRecyclerView);
        ongoingReservationsList = findViewById(R.id.onGoingReservationsRecyclerView);

        pendingReservationsList.setLayoutManager(new LinearLayoutManager(this));
        ongoingReservationsList.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapters
        pendingReservationAdapter = new PendingReservationsAdapter(pendingReservations, new PendingReservationsAdapter.OnReservationActionListener() {
            @Override
            public void onAccept(int position) {
                // Logic for accepting a reservation
                Reservation reservation = pendingReservations.get(position);
                ongoingReservations.add(reservation); // Add to ongoing reservations
                pendingReservations.remove(position); // Remove from pending
                pendingReservationAdapter.notifyItemRemoved(position);
                ongoingReservationAdapter.notifyDataSetChanged(); // Refresh ongoing reservations
            }

            @Override
            public void onReject(int position) {
                // Logic for rejecting a reservation
                pendingReservations.remove(position);
                pendingReservationAdapter.notifyItemRemoved(position);
            }
        });

        ongoingReservationAdapter = new OnGoingReservationsAdapter(ongoingReservations);

        pendingReservationsList.setAdapter(pendingReservationAdapter);
        ongoingReservationsList.setAdapter(ongoingReservationAdapter);

        // Call method to load data from Firebase
        loadReservationsFromFirebase();
    }

    private void loadReservationsFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("reservations");

        // Add listener to retrieve data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pendingReservations.clear();
                ongoingReservations.clear();

                for (DataSnapshot reservationSnapshot : dataSnapshot.getChildren()) {
                    String customer = reservationSnapshot.child("Customer").getValue(String.class);
                    String numberOfPeople = reservationSnapshot.child("NumberOfPeople").getValue(String.class);
                    String status = reservationSnapshot.child("Status").getValue(String.class); // Assuming you have a status field

                    Reservation reservation = new Reservation(customer, numberOfPeople, status);

                    if ("pending".equalsIgnoreCase(status)) {
                        pendingReservations.add(reservation); // Add to pending reservations
                    } else if ("accepted".equalsIgnoreCase(status)) {
                        ongoingReservations.add(reservation); // Add to ongoing reservations
                    }
                }

                // Notify adapters that data has changed
                pendingReservationAdapter.notifyDataSetChanged();
                ongoingReservationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "loadReservations:onCancelled", databaseError.toException());
            }
        });
    }
}
