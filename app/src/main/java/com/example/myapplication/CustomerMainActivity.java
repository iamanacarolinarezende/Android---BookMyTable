package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerMainActivity extends AppCompatActivity {

    Button createReservationBtn;
    ListView restaurantListView;
    ArrayList<Restaurant> restaurantList;
    RestaurantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_main);

        String userName = getIntent().getStringExtra("email");
        TextView customerGreeting = findViewById(R.id.customerGreeting);
        customerGreeting.setText("Welcome, " + userName);

        restaurantListView = findViewById(R.id.restaurantListView);
        createReservationBtn = findViewById(R.id.createReservationBtn);

        restaurantList = new ArrayList<>();
        adapter = new RestaurantAdapter(this, restaurantList);
        restaurantListView.setAdapter(adapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                restaurantList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
                    if (restaurant != null && "restaurant".equals(restaurant.getType())) {
                        restaurantList.add(restaurant);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerMainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        createReservationBtn.setOnClickListener(view -> {
            int selectedPosition = restaurantListView.getCheckedItemPosition();
            if (selectedPosition != ListView.INVALID_POSITION) {
                Restaurant selectedRestaurantObj = restaurantList.get(selectedPosition);
                String selectedRestaurantName = selectedRestaurantObj.getName();
                String selectedRestaurantAddress = selectedRestaurantObj.getAddress();

                Intent intent = new Intent(CustomerMainActivity.this, CustomerReservation.class);
                intent.putExtra("selectedRestaurant", selectedRestaurantName);
                intent.putExtra("restaurantAddress", selectedRestaurantAddress);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a restaurant", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
