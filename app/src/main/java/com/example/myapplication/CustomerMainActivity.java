package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

    private ListView restaurantListView;
    private Button restaurantSelectionBtn;
    private ArrayList<String> restaurantList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);

        restaurantListView = findViewById(R.id.restaurantListView);
        restaurantSelectionBtn = findViewById(R.id.restaurantSelectionBtnId);


        restaurantList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, restaurantList);
        restaurantListView.setAdapter(adapter);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users"); // ajuste o nome da tabela se necessário


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                restaurantList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Restaurant restaurant = snapshot.getValue(Restaurant.class);
                    if (restaurant != null && "restaurant".equals(restaurant.getType())) {
                        restaurantList.add(restaurant.getName());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CustomerMainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar o botão de seleção de restaurante
        restaurantSelectionBtn.setOnClickListener(view -> {

        });
    }
}

