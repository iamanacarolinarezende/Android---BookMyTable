package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OnGoingReservationsAdapter extends RecyclerView.Adapter<OnGoingReservationsAdapter.ViewHolder> {

    private List<Reservation> reservations;

    public OnGoingReservationsAdapter(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.customerName = reservation.getCustomer() + " - " + reservation.getNumberOfPeople();

        // Here you can handle actions for ongoing reservations if needed
        // For example, you may want to add a button to end the reservation
        holder.rejectButton.setVisibility(View.GONE); // Optionally hide the reject button for ongoing reservations
        holder.acceptButton.setText("End Reservation"); // Change the text of the accept button if needed

        // Add onClickListener for the buttons here if needed
        holder.acceptButton.setOnClickListener(v -> {
            // Handle the logic to end the ongoing reservation
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public void setData(List<Reservation> newReservations) {
        this.reservations = newReservations;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public String  customerName;
        public Button acceptButton;
        public Button rejectButton;

        public ViewHolder(View itemView) {
            super(itemView);
            customerName = "";
            acceptButton = itemView.findViewById(R.id.acceptButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}
