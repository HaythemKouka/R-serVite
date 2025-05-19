package com.example.bib;

import android.content.Intent;
import android.os.Bundle;
import android.view.View; // Don't forget to import View
import android.widget.Button;
import android.widget.TextView; // Don't forget to import TextView
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bib.database.DBHelper;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText emailEditText, passwordEditText, confirmPasswordEditText; // Added confirmPasswordEditText
    Button registerBtn;
    DBHelper dbHelper;
    TextView tvLoginLink; // Declared here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // --- Initialize UI elements ---
        // For Login Link
        tvLoginLink = findViewById(R.id.tvLoginLink);
        // For Email, Password, Register Button
        emailEditText = findViewById(R.id.etEmail); // Corresponds to etEmail in your XML
        passwordEditText = findViewById(R.id.etPassword); // Corresponds to etPassword in your XML
        confirmPasswordEditText = findViewById(R.id.etConfirmPassword); // Added for confirm password
        registerBtn = findViewById(R.id.btnRegisterSubmit); // Corresponds to btnRegisterSubmit in your XML

        // --- Initialize Database Helper ---
        dbHelper = new DBHelper(this);

        // --- Set up Click Listener for "Already have an account? Login" Text ---
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Finish RegisterActivity to remove it from the back stack
            }
        });

        // --- Set up Click Listener for Register Button ---
        registerBtn.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String pass = passwordEditText.getText().toString().trim();
            String confirmPass = confirmPasswordEditText.getText().toString().trim(); // Get confirm password

            if (email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) { // Check all fields
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirmPass)) { // Check if passwords match
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Special case for ADMIN (hardcoded)
            if (email.equalsIgnoreCase("ADMIN") && pass.equals("ADMIN")) {
                if (!dbHelper.checkUserExists(email)) {
                    dbHelper.insertUser(email, pass, "admin");
                }
                Toast.makeText(this, "Bienvenue Admin", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ListUsersActivity.class)); // Assuming ListUsersActivity for Admin
                finish();
                return; // Exit after handling admin login
            }

            // Check if user already exists for non-admin registration
            if (dbHelper.checkUserExists(email)) {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            } else {
                // Register with default 'user' role
                boolean registered = dbHelper.insertUser(email, pass, "user");
                if (registered) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    // Navigate to appropriate activity for regular user after registration
                    startActivity(new Intent(this, BooksAdminActivity.class)); // Example: navigate to BooksAdminActivity
                    finish();
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}