package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RestaurantEditProfile extends AppCompatActivity {
    private EditText emailEditText, restaurantNameEditText, restaurantAddressEditText, restaurantPhoneEditText;
    private Button updateButton, deleteButton, forgotButton;
    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_restaurant_profile);

        emailEditText = findViewById(R.id.emailregister);
        restaurantNameEditText = findViewById(R.id.restaurantNameEdit);
        restaurantAddressEditText = findViewById(R.id.restaurantAddressEdit);
        restaurantPhoneEditText = findViewById(R.id.restaurantPhoneEdit);
        updateButton = findViewById(R.id.registerbtn);
        forgotButton = findViewById(R.id.forgotbtn);
        deleteButton = findViewById(R.id.loginregbtn);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        loadUserData();

        // Menu
        ImageView home = findViewById(R.id.navigation_home);
        ImageView user = findViewById(R.id.navigation_user);
        ImageView logout = findViewById(R.id.navigation_logout);

        updateButton.setOnClickListener(view -> updateUserData());
        deleteButton.setOnClickListener(view -> showDeleteConfirmationDialog());

        home.setOnClickListener(v -> startActivity(new Intent(this, RestaurantMainActivity.class)));
        user.setSelected(true);
        user.setOnClickListener(v -> startActivity(new Intent(this, RestaurantEditProfile.class)));

        logout.setOnClickListener(v -> {
            auth.signOut();
            Toast.makeText(this, "You are logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String restaurantName = snapshot.child("name").getValue(String.class);
                    String restaurantAddress = snapshot.child("address").getValue(String.class);
                    String restaurantPhone = snapshot.child("phone").getValue(String.class);

                    emailEditText.setText(email);
                    restaurantNameEditText.setText(restaurantName);
                    restaurantAddressEditText.setText(restaurantAddress);
                    restaurantPhoneEditText.setText(restaurantPhone);
                } else {
                    Toast.makeText(RestaurantEditProfile.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(RestaurantEditProfile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserData() {
        String newEmail = emailEditText.getText().toString().trim();
        String newRestaurantName = restaurantNameEditText.getText().toString().trim();
        String newRestaurantAddress = restaurantAddressEditText.getText().toString().trim();
        String newRestaurantPhone = restaurantPhoneEditText.getText().toString().trim();

        if (newEmail.isEmpty() || newRestaurantName.isEmpty() || newRestaurantAddress.isEmpty() || newRestaurantPhone.isEmpty()) {
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("email", newEmail);
        updates.put("name", newRestaurantName);
        updates.put("address", newRestaurantAddress);
        updates.put("phone", newRestaurantPhone);

        currentUser.updateEmail(newEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userRef.updateChildren(updates).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(this, "User data updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update Realtime Database: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "To change your email, send us a message", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reason for Account Deletion");
        builder.setMessage("Please provide a reason for deleting your account:");

        EditText reasonInput = new EditText(this);
        builder.setView(reasonInput);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            String reason = reasonInput.getText().toString().trim();
            if (!reason.isEmpty()) {
                Toast.makeText(this, "We received your communication. Our company will contact you soon.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Reason cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(RestaurantEditProfile.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RestaurantEditProfile.this, "Password reset email sent", Toast.LENGTH_SHORT).show();

                auth.signOut();
                Intent intent = new Intent(RestaurantEditProfile.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(RestaurantEditProfile.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
