package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class Reservation extends AppCompatActivity {

    TextView showEmail;
    FirebaseAuth firebaseAuth;
    private Spinner tableSpinner;
    private Spinner dateSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reservation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerCustomer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        showEmail = findViewById(R.id.showEmail);
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String emailUser = " ";
        if (currentUser != null) {
            emailUser = currentUser.getEmail();
            showEmail.setText(emailUser);
        }

        // Spinner to select the size of the party. From 1 to 4, in this case.
        // for more restaurants, need to take it from the database
        tableSpinner = findViewById(R.id.TableSpinner);
        List<String> numberOfPersons = Arrays.asList("1", "2", "3", "4");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, numberOfPersons);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableSpinner.setAdapter(adapter);


        dateSpinner = findViewById(R.id.dateSpinner);
        dateSpinner.setSelection(0); // The today date
        // to select another date
        dateSpinner.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    Reservation.this,
                    (view, year, month, dayOfMonth) -> {
                        // Formatar a data selecionada como "dd/MM/yyyy"
                        String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        // Definir a data selecionada no Spinner
                        tableSpinner.setSelection(0);
                        ArrayAdapter<String> newAdapter = new ArrayAdapter<>(this,
                                android.R.layout.simple_spinner_item, Arrays.asList(selectedDate));
                        newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        tableSpinner.setAdapter(newAdapter);
                    },
                    java.util.Calendar.getInstance().get(java.util.Calendar.YEAR),
                    java.util.Calendar.getInstance().get(java.util.Calendar.MONTH),
                    java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }
}