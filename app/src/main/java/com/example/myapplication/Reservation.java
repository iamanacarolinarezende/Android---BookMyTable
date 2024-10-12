package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Reservation extends AppCompatActivity {

    private TextView showEmail, showRestaurantName, showRestaurantAddress;
    private Spinner tableSpinner;
    private DatePicker datePicker;
    private Button makeReservationBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        // Inicializa as views
        showEmail = findViewById(R.id.showEmail);
        showRestaurantName = findViewById(R.id.showRestaurantName);
        showRestaurantAddress = findViewById(R.id.showRestaurantAddress);
        tableSpinner = findViewById(R.id.TableSpinner);
        datePicker = findViewById(R.id.datePickerInline);
        makeReservationBtn = findViewById(R.id.makeReservationBtn);

        // Inicializa Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Obtém o usuário atual
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            showEmail.setText(currentUser.getEmail());
        }

        // Recebe o restaurante selecionado e o endereço
        String selectedRestaurant = getIntent().getStringExtra("selectedRestaurant");
        String restaurantAddress = getIntent().getStringExtra("restaurantAddress");

        if (selectedRestaurant != null) {
            showRestaurantName.setText(selectedRestaurant);
            showRestaurantAddress.setText(restaurantAddress);
            Log.d("Reservation", "Selected Restaurant: " + selectedRestaurant);
        }
    }
}
