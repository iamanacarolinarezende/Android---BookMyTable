package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMainActivity extends AppCompatActivity {

    RecyclerView reservationsRecyclerView;
    ReservationAdapter reservationAdapter;
    List<Reservation> reservationList = new ArrayList<>();
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);

        String userName = getIntent().getStringExtra("email");
        TextView restaurantGreeting = findViewById(R.id.restaurantGreeting);
        restaurantGreeting.setText("Welcome, " + userName);

        reservationsRecyclerView = findViewById(R.id.reservationsRecyclerView);
        reservationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        reservationList = new ArrayList<>();
        reservationAdapter = new ReservationAdapter(this, reservationList);
        reservationsRecyclerView.setAdapter(reservationAdapter);

        // Buscando reservas pendentes para o restaurante logado
        databaseReference = FirebaseDatabase.getInstance().getReference("reservations");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reservationList.clear();
                for (DataSnapshot reservationSnapshot : snapshot.getChildren()) {
                    Reservation reservation = reservationSnapshot.getValue(Reservation.class);
                    if (reservation != null && reservation.getRestaurantName().equals("email") &&
                            reservation.getStatus().equals("Pending")) {
                        reservationList.add(reservation);
                    }
                }
                reservationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Erro ao carregar dados", error.toException());
            }
        });
    }
}
