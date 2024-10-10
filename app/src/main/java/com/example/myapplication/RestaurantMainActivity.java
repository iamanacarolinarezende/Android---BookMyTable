package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RestaurantMainActivity extends AppCompatActivity {

    TextView showEmail;
    FirebaseAuth firebaseAuth;
    RecyclerView pendingReservationsList;
    ReservationAdapter reservationAdapter;
    List<String> reservations = new ArrayList<>(); // Lista de reservas a ser populada com os dados do Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_restaurant_main);

        // Inicializa o RecyclerView
        pendingReservationsList = findViewById(R.id.pendingReservationsRecyclerView);
        pendingReservationsList.setLayoutManager(new LinearLayoutManager(this));

        // Inicializa o Adapter
        reservationAdapter = new ReservationAdapter(reservations, new ReservationAdapter.OnReservationActionListener() {
            @Override
            public void onAccept(int position) {
                // Lógica para aceitar a reserva
                String reservation = reservations.get(position);
                // Exemplo de ação: remover da lista
                reservations.remove(position);
                reservationAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onReject(int position) {
                // Lógica para rejeitar a reserva
                String reservation = reservations.get(position);
                // Exemplo de ação: remover da lista
                reservations.remove(position);
                reservationAdapter.notifyItemRemoved(position);
            }
        });

        pendingReservationsList.setAdapter(reservationAdapter);

        // Chama o método para buscar dados no Firebase
        loadReservationsFromFirebase();
    }

    private void loadReservationsFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("reservations");

        // Adiciona o listener para recuperar os dados do Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reservations.clear(); // Limpa a lista antes de adicionar novos dados

                // Itera sobre cada reserva no banco de dados
                for (DataSnapshot reservationSnapshot : dataSnapshot.getChildren()) {
                    // Aqui você pega os campos "Customer" e "NumberOfPeople"
                    String customer = reservationSnapshot.child("Customer").getValue(String.class);
                    String numberOfPeople = reservationSnapshot.child("NumberOfPeople").getValue(String.class);

                    // Concatena os campos
                    String reservationText = customer + " - " + numberOfPeople + " people";

                    // Adiciona à lista
                    reservations.add(reservationText);
                }

                // Notifica o Adapter que os dados mudaram
                reservationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "loadReservations:onCancelled", databaseError.toException());
            }
        });
    }
}
