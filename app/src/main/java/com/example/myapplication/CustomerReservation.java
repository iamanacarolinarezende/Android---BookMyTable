package com.example.myapplication;

import android.content.Intent;
import android.widget.Button;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerReservation extends AppCompatActivity {

    EditText nameInput;
    EditText addressInput;
    EditText editTextDate;
    EditText editTextTime;
    EditText partySizeEditText;
    Button submitBtn;

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

        String name = getIntent().getStringExtra("selectedRestaurant");
        String address = getIntent().getStringExtra("restaurantAddress");
        String user = getIntent().getStringExtra("email");

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
                    user
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
        });;
    }
}
