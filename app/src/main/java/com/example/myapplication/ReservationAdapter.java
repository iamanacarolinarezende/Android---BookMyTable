package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    private List<Reservation> reservationList;
    private Context context;

    public ReservationAdapter(Context context, List<Reservation> reservationList) {
        this.context = context;
        this.reservationList = reservationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);

        // Exibir informações da reserva
        String info = "Date: " + reservation.getDate() + "\n" +
                "Time: " + reservation.getTime() + "\n" +
                "Party Size: " + reservation.getPartySize() + "\n" +
                "Customer: " + reservation.getEmail();
        holder.reservationInfo.setText(info);

        // Ações dos botões "Aceitar" e "Rejeitar"
        holder.acceptButton.setOnClickListener(v -> {
            // Atualizar o status para "Accepted"
            updateReservationStatus(reservation, "Accepted");
        });

        holder.rejectButton.setOnClickListener(v -> {
            // Atualizar o status para "Rejected"
            updateReservationStatus(reservation, "Rejected");
        });
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView reservationInfo;
        Button acceptButton, rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reservationInfo = itemView.findViewById(R.id.reservationInfo);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }

    private void updateReservationStatus(Reservation reservation, String newStatus) {
        // Aqui você pode implementar a lógica para atualizar o status no Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("reservations");
        databaseReference.child(reservation.getEmail())
                .child("status").setValue(newStatus);
    }
}
