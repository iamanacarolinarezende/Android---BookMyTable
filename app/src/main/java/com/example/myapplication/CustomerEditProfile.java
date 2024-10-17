package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CustomerEditProfile extends AppCompatActivity {
    private EditText emailEditText, phoneEditText;
    private Button updateButton, deleteButton, forgotButton;
    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_customer_profile); // Nome do layout XML

        emailEditText = findViewById(R.id.emailregister);
        phoneEditText = findViewById(R.id.phoneregister);
        updateButton = findViewById(R.id.updateCustomer);
        forgotButton = findViewById(R.id.forgotbtn);
        deleteButton = findViewById(R.id.deleteCustomer);

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
        builder.setTitle("Delete Account");
        builder.setMessage("Please enter your password to confirm account deletion.");

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("Delete", (dialog, which) -> {
            String currentPassword2 = input.getText().toString().trim();
            deleteUserAccount(currentPassword2); // Chamar a função de deletar a conta
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void deleteUserAccount(String currentPassword) {
        // Reautenticação do usuário com o e-mail e a senha atual
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);

        currentUser.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
            if (reauthTask.isSuccessful()) {
                // Exclui a conta da Authentication
                currentUser.delete().addOnCompleteListener(deleteTask -> {
                    if (deleteTask.isSuccessful()) {
                        // Remove os dados do usuário do Realtime Database
                        userRef.removeValue().addOnCompleteListener(dbTask -> {
                            if (dbTask.isSuccessful()) {
                                Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                auth.signOut();
                                Intent intent = new Intent(this, Login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "Error deleting user data: " + dbTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Error deleting account", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Authentication required. Please enter your password again.", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void resetPassword() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(CustomerEditProfile.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CustomerEditProfile.this, "Password reset email sent", Toast.LENGTH_SHORT).show();

                auth.signOut();
                Intent intent = new Intent(CustomerEditProfile.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(CustomerEditProfile.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
