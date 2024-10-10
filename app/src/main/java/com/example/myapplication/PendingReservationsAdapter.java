package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class PendingReservationsAdapter extends RecyclerView.Adapter<PendingReservationsAdapter.ViewHolder> {

    private List<Reservation> reservations;

    public PendingReservationsAdapter(List<Reservation> reservations) {
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
        // Handle Accept/Reject buttons here...
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
        public String customerName;
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