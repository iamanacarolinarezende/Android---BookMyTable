package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

public class RegisterCompanyInfo extends AppCompatActivity {

    // Declare FirebaseAuth as a class member
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_company_info);

        EditText restaurantNameEditText = findViewById(R.id.restaurantNameEditText);
        EditText restaurantAddressEditText = findViewById(R.id.restaurantAddressEditText);
        EditText restaurantPhoneEditText = findViewById(R.id.restaurantPhoneEditText);
        Button restaurantRegisterButton = findViewById(R.id.restaurantRegisterbtn);
        Button restaurantCancelButton = findViewById(R.id.loginregbtn);

        firebaseAuth = FirebaseAuth.getInstance();

        restaurantCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterCompanyInfo.this, RegisterCustomer.class);
                startActivity(intent);
                finish();
            }
        });

        restaurantRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get input from EditTexts
                String restaurantName = restaurantNameEditText.getText().toString().trim();
                String address = restaurantAddressEditText.getText().toString().trim();
                String phoneNumber = restaurantPhoneEditText.getText().toString().trim();

                completeRestaurantRegistration(restaurantName, address, phoneNumber);
            }
        });
    }

    private void completeRestaurantRegistration(String restaurantName, String address, String phoneNumber) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

            // Creating restaurant data to save in the database
            HashMap<String, String> restaurantData = new HashMap<>();
            restaurantData.put("name", restaurantName);
            restaurantData.put("address", address);
            restaurantData.put("phone", phoneNumber);
            restaurantData.put("type", "restaurant"); // Identifying that it's a restaurant

            // Save data to the database
            databaseReference.child(userId).setValue(restaurantData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterCompanyInfo.this, "Restaurant registered successfully", Toast.LENGTH_SHORT).show();
                        // Redirect to the main screen of the restaurant
                        startActivity(new Intent(RegisterCompanyInfo.this, RestaurantMainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterCompanyInfo.this, "Failed to save restaurant data", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}