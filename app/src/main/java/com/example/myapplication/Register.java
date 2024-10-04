package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    EditText emailEditText, passwordEditText, confirmPasswordEditText;
    Button registerButton, loginregButton;
    FirebaseAuth firebaseAuth;
    RadioGroup userTypeRadioGroup;
    RadioButton selectedRadioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        emailEditText = findViewById(R.id.emailregister);
        passwordEditText = findViewById(R.id.passwordregister);
        confirmPasswordEditText = findViewById(R.id.confirmpasswordregister);
        registerButton = findViewById(R.id.registerbtn);
        loginregButton = findViewById(R.id.loginregbtn);
        userTypeRadioGroup = findViewById(R.id.rdbtn);

        firebaseAuth = FirebaseAuth.getInstance();

        loginregButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
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

        int selectedId = userTypeRadioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(Register.this, "Please select a user type", Toast.LENGTH_SHORT).show();
            return;
        }
        selectedRadioButton = findViewById(selectedId);
        final String userType = selectedRadioButton.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(Register.this, "All fields are required", Toast.LENGTH_SHORT).show();
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

        // if user is here, everything is valid and passed
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this, MainActivity.class));
                            finish();
                        }
                        else{
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(Register.this, "This email is already registered, please log in.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Register.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}