package com.example.myapplication;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.view.MenuItem;


public class CustomerReservation extends AppCompatActivity {

    EditText nameInput;
    EditText addressInput;
    EditText editTextDate;
    EditText editTextTime;
    EditText partySizeEditText;
    Button submitBtn;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_reservation);

        nameInput = findViewById(R.id.nameInput);
        addressInput = findViewById(R.id.addressInput);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        partySizeEditText = findViewById(R.id.partySizeEditText);
        submitBtn = findViewById(R.id.submit);
        firebaseAuth = FirebaseAuth.getInstance();

        //Menu Images
        ImageView home = findViewById(R.id.navigation_home);
        ImageView user = findViewById(R.id.navigation_user);
        ImageView logout = findViewById(R.id.navigation_logout);


        String name = getIntent().getStringExtra("selectedRestaurant");
        String address = getIntent().getStringExtra("restaurantAddress");
        String userEmail = getIntent().getStringExtra("email");

        nameInput.setText(name);
        addressInput.setText(address);

        editTextDate.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view1, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        editTextDate.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        editTextTime.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (view1, hourOfDay, minute1) -> {
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
                        editTextTime.setText(selectedTime);
                    },
                    hour, minute, true
            );
            timePickerDialog.show();
        });

        submitBtn.setOnClickListener(view -> {
            String restaurantName = nameInput.getText().toString();
            String restaurantAddress = addressInput.getText().toString();
            String date = editTextDate.getText().toString();
            String time = editTextTime.getText().toString();
            String partySize = partySizeEditText.getText().toString();

            Reservation reservation = new Reservation(
                    date,
                    partySize,
                    restaurantAddress,
                    restaurantName,
                    "Pending",
                    time,
                    userEmail
            );

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("reservations");
            String reservationId = databaseReference.push().getKey(); // Gera uma chave única para cada reserva
            if (reservationId != null) {
                databaseReference.child(reservationId).setValue(reservation)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(CustomerReservation.this, "Reservation submitted successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CustomerReservation.this, CustomerMainActivity.class);
                            } else {
                                Toast.makeText(CustomerReservation.this, "Failed to submit reservation.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        //Menu Start
        // Home button
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerReservation.this, CustomerMainActivity.class);
                startActivity(intent);
            }
        });

        // Send to register page
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerReservation.this, RegisterCustomer.class);
                startActivity(intent);
            }
        });

        //Logout button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Toast.makeText(CustomerReservation.this, "You are logged out", Toast.LENGTH_SHORT).show();

                // Redirect to Login activity
                Intent intent = new Intent(CustomerReservation.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                startActivity(intent);
                finish();
            }
        });


    }
}
