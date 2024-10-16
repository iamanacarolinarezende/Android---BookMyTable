package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

    ReservationAdapter reservationAdapter;
    List<Reservation> reservationList = new ArrayList<>();
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_main);

        String userName = getIntent().getStringExtra("email");
        TextView restaurantGreeting = findViewById(R.id.restaurantGreeting);
        restaurantGreeting.setText("Welcome, " + userName);

        reservationList = new ArrayList<>();
        reservationAdapter = new ReservationAdapter(this, reservationList);
        firebaseAuth = FirebaseAuth.getInstance();

        //Menu Images
        ImageView home = findViewById(R.id.navigation_home_res);
        ImageView user = findViewById(R.id.navigation_user_res);
        ImageView logout = findViewById(R.id.navigation_logout_res);

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
                Log.e("Firebase", "Error loading data", error.toException());
            }
        });

        //Menu Start
        // Home button
        home.setSelected(true);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantMainActivity.this, RestaurantMainActivity.class);
                startActivity(intent);
            }
        });

        // Send to register page
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantMainActivity.this, RegisterCompany.class);
                startActivity(intent);
            }
        });

        //Logout button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Toast.makeText(RestaurantMainActivity.this, "You are logged out", Toast.LENGTH_SHORT).show();

                // Redirect to Login activity
                Intent intent = new Intent(RestaurantMainActivity.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                startActivity(intent);
                finish();
            }
        });
    }
}