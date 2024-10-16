package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

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

        String info = "Date: " + reservation.getDate() + "\n" +
                "Time: " + reservation.getTime() + "\n" +
                "Party Size: " + reservation.getPartySize() + "\n" +
                "Customer: " + reservation.getEmail();
        holder.reservationInfo.setText(info);

        holder.acceptButton.setOnClickListener(v -> {
            updateReservationStatus(reservation.getEmail(), "Accepted", reservation.getDate(), reservation.getTime(), reservation.getPartySize(), reservation.getRestaurantName());
        });

        holder.rejectButton.setOnClickListener(v -> {
            updateReservationStatus(reservation.getEmail(), "Rejected", reservation.getDate(), reservation.getTime(), reservation.getPartySize(), reservation.getRestaurantName());
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

    private void updateReservationStatus(String email, String newStatus, String date, String time, String partySize, String restaurantName) {
        DatabaseReference reservationRef = FirebaseDatabase.getInstance().getReference("reservations");

        // Listening for pending reservations
        reservationRef.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Reservation reservation = snapshot.getValue(Reservation.class);

                    // Comparing reservation details
                    if (reservation != null &&
                            reservation.getEmail().equals(email) &&
                            reservation.getDate().equals(date) &&
                            reservation.getTime().equals(time) &&
                            reservation.getPartySize().equals(partySize) &&
                            reservation.getRestaurantName().equals(restaurantName)) {
                        snapshot.getRef().child("status").setValue(newStatus)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Reservation updated successfully", Toast.LENGTH_SHORT).show();
                                        fetchPendingReservations(); // Update the list
                                    } else {
                                        Toast.makeText(context, "Error updating reservation", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        found = true; // Reservation found
                    }
                }
                if (!found) {
                    Toast.makeText(context, "No pending reservation found with the provided details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Error loading data.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void fetchPendingReservations() {
        String safeEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", ",");
        DatabaseReference reservationsRef = FirebaseDatabase.getInstance().getReference("reservations").child(safeEmail);

        reservationsRef.orderByChild("status").equalTo("Pending").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    reservationList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Reservation reservation = snapshot.getValue(Reservation.class);
                        reservationList.add(reservation);
                    }
                    notifyDataSetChanged();
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Error fetching data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
