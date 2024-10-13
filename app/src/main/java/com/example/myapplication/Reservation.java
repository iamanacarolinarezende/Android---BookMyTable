package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Reservation extends AppCompatActivity {

    private TextView showEmail, showRestaurantName, showRestaurantAddress;
    private EditText partySizeEditText;
    private Spinner tableSpinner;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Button makeReservationBtn;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference reservationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // Initialize Firebase Auth and DatabaseReference
        firebaseAuth = FirebaseAuth.getInstance();
        reservationsRef = FirebaseDatabase.getInstance().getReference("reservations");

        showEmail = findViewById(R.id.showEmail);
        showRestaurantName = findViewById(R.id.showRestaurantName);
        showRestaurantAddress = findViewById(R.id.showRestaurantAddress);
        partySizeEditText = findViewById(R.id.partySizeEditText);
        tableSpinner = findViewById(R.id.TableSpinner);
        datePicker = findViewById(R.id.datePickerInline);
        timePicker = findViewById(R.id.timePicker);
        makeReservationBtn = findViewById(R.id.makeReservationBtn);

        // Get data from Intent
        String restaurantName = getIntent().getStringExtra("selectedRestaurant");
        String restaurantAddress = getIntent().getStringExtra("restaurantAddress");

        // Display the restaurant details
        showRestaurantName.setText(restaurantName);
        showRestaurantAddress.setText(restaurantAddress);

        makeReservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get form data
                String partySize = partySizeEditText.getText().toString();
                int selectedHour = timePicker.getHour();
                int selectedMinute = timePicker.getMinute();
                int selectedDay = datePicker.getDayOfMonth();
                int selectedMonth = datePicker.getMonth();
                int selectedYear = datePicker.getYear();

                if (partySize.isEmpty()) {
                    Toast.makeText(Reservation.this, "Please enter the number of people", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get current user from Firebase
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();

                    // Create a unique ID for the reservation
                    String reservationId = reservationsRef.push().getKey();

                    // Create a HashMap to store reservation details
                    HashMap<String, Object> reservationData = new HashMap<>();
                    reservationData.put("restaurantName", restaurantName);
                    reservationData.put("restaurantAddress", restaurantAddress);
                    reservationData.put("partySize", partySize);
                    reservationData.put("date", selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                    reservationData.put("time", selectedHour + ":" + String.format("%02d", selectedMinute));
                    reservationData.put("status", "Pending"); // Add status field with default value "Pending"

                    // Save reservation to Firebase Database under the user's node
                    reservationsRef.child(userId).child(reservationId).setValue(reservationData)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Reservation.this, "Reservation made successfully", Toast.LENGTH_SHORT).show();
                                        // Redirect user if necessary
                                    } else {
                                        Toast.makeText(Reservation.this, "Failed to make reservation", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(Reservation.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
