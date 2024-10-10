package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterCustomer extends AppCompatActivity {
    EditText emailEditText, passwordEditText, confirmPasswordEditText, phoneNumberEditText;
    Button registerButton, loginregButton, restaurantButton;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        emailEditText = findViewById(R.id.emailregister);
        passwordEditText = findViewById(R.id.passwordregister);
        confirmPasswordEditText = findViewById(R.id.confirmpasswordregister);
        phoneNumberEditText = findViewById(R.id.phoneregister);
        registerButton = findViewById(R.id.registerbtn);
        loginregButton = findViewById(R.id.loginregbtn);
        restaurantButton = findViewById(R.id.restaurantbtn);

        firebaseAuth = FirebaseAuth.getInstance();

        loginregButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterCustomer.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        restaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterCustomer.this, RegisterCompany.class);
                startActivity(intent);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }
    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(RegisterCustomer.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email");
            emailEditText.requestFocus();
            return;
        }

        if (password.length() < 8){
            passwordEditText.setError("Password must be at least 8 characters");
            passwordEditText.requestFocus();
            return;
        }
        if (!password.equals(confirmPassword)){
            confirmPasswordEditText.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return;
        }

        if (phoneNumber.length() != 10) {
            phoneNumberEditText.setError("Invalid phone number");
            phoneNumberEditText.requestFocus();
            return;
        }

        // if user is here, everything is valid and passed
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if (user != null) {
                                String userId = user.getUid();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

                                HashMap<String, String> customerData = new HashMap<>();
                                customerData.put("email", user.getEmail());
                                customerData.put("type", "customer"); // identificando que é cliente
                                customerData.put("phone", phoneNumber);

                                databaseReference.child(userId).setValue(customerData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(RegisterCustomer.this, "Customer registered successfully", Toast.LENGTH_SHORT).show();
                                            // Direcionar para a tela principal do cliente
                                            startActivity(new Intent(RegisterCustomer.this, CustomerMainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterCustomer.this, "Failed to save customer data", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                        else{
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(RegisterCustomer.this, "This email is already registered, please log in.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterCustomer.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}