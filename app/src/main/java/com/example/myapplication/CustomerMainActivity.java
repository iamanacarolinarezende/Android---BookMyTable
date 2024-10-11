package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class CustomerMainActivity extends AppCompatActivity {

    Button restaurantSelectionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_main);

        restaurantSelectionBtn = findViewById(R.id.restaurantSelectionBtnId);

        /*
        restaurantSelectionBtn.setOnClickListener(view -> {
            Intent intent = new Intent(CustomerMainActivity.this, Reservation.class);
            startActivity(intent);
            finish();
        });
        */

        restaurantSelectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerMainActivity.this, Reservation.class);
                startActivity(intent);
                finish();
            }
        });


    }
}
