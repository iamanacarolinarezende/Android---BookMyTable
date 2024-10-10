package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    EditText emailEditText, passwordEditText;
    Button registerButton, loginButton, forgotButton;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailEditText = findViewById(R.id.emaillogin);
        passwordEditText = findViewById(R.id.passwordlogin);
        loginButton = findViewById(R.id.loginbtn);
        forgotButton = findViewById(R.id.forgotbtn);
        registerButton = findViewById(R.id.regbtn);

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, RegisterCustomer.class);
                startActivity(intent);
                finish();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotUser();
            }
        });

    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Login.this, "Login Success", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    String userId = user.getUid(); // Obtenha o ID do usuário
                    redirectToAppropriateActivity(userId); // Redirecionar baseado no tipo
                }
                else{
                    Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void forgotUser() {
        String email = emailEditText.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(Login.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Login.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void redirectToAppropriateActivity(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    String userType = dataSnapshot.child("type").getValue(String.class);

                    Intent intent;
                    if ("customer".equals(userType)) {
                        String phone = dataSnapshot.child("phone").getValue(String.class);

                        intent = new Intent(Login.this, CustomerMainActivity.class);
                        intent.putExtra("userPhone", phone);
                    } else if ("restaurant".equals(userType)) {
                        // Carrega os dados do restaurante
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String address = dataSnapshot.child("address").getValue(String.class);
                        String tables = dataSnapshot.child("tables").getValue(String.class);
                        String phone = dataSnapshot.child("phone").getValue(String.class);

                        intent = new Intent(Login.this, RestaurantMainActivity.class);
                        intent.putExtra("restaurantName", name);
                        intent.putExtra("restaurantAddress", address);
                        intent.putExtra("restaurantTables", tables);
                        intent.putExtra("restaurantPhone", phone);
                    } else {
                        Toast.makeText(Login.this, "User type not recognized", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Login.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //To remember the login when user come back
//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        if (currentUser != null) {
//            Intent intent = new Intent(login.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }

}