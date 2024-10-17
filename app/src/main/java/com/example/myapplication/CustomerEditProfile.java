package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

public class CustomerEditProfile extends AppCompatActivity {
    private EditText emailEditText, passwordEditText, phoneEditText;
    private Button updateButton, deleteButton;
    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_customer_profile); // Nome do layout XML

        emailEditText = findViewById(R.id.emailregister);
        passwordEditText = findViewById(R.id.passwordregister);
        phoneEditText = findViewById(R.id.phoneregister);
        updateButton = findViewById(R.id.updateCustomer);
        deleteButton = findViewById(R.id.deleteCustomer);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        loadUserData();

        // Menu
        ImageView home = findViewById(R.id.navigation_home);
        ImageView user = findViewById(R.id.navigation_user);
        ImageView logout = findViewById(R.id.navigation_logout);

        updateButton.setOnClickListener(view -> updateUserData());
        deleteButton.setOnClickListener(view -> deleteUserAccount());

        home.setOnClickListener(v -> startActivity(new Intent(this, CustomerMainActivity.class)));
        user.setSelected(true);
        user.setOnClickListener(v -> startActivity(new Intent(this, CustomerEditProfile.class)));

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

                    emailEditText.setText(email);
                    phoneEditText.setText(phone);
                } else {
                    Toast.makeText(CustomerEditProfile.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(CustomerEditProfile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserData() {
        String newEmail = emailEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString().trim();
        String newPhone = phoneEditText.getText().toString().trim();

        if (newEmail.isEmpty() || newPhone.isEmpty()) {
            Toast.makeText(this, "Email and phone must be filled", Toast.LENGTH_SHORT).show();
            return;
        }

        // New map with updated data
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", newEmail);
        updates.put("phone", newPhone);

        // Update Firebase
        currentUser.updateEmail(newEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userRef.updateChildren(updates).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        if (!newPassword.isEmpty()) {
                            currentUser.updatePassword(newPassword).addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    Toast.makeText(this, "User data updated successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "To change your password, send us a message", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(this, "User data updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to update Realtime Database: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "To change your email, send us a message", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteUserAccount() {
        currentUser.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userRef.removeValue().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                        auth.signOut();
                        Intent intent = new Intent(this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Error deleting user data", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Error deleting account", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
