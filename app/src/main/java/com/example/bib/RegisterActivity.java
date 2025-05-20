package com.example.bib;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bib.database.DBHelper;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText emailEditText, passwordEditText, confirmPasswordEditText;
    Button registerBtn;
    DBHelper dbHelper;
    TextView tvLoginLink;
    Button manageBooksBtn;  // déclaration au niveau classe

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // --- Initialize UI elements ---
        tvLoginLink = findViewById(R.id.tvLoginLink);
        emailEditText = findViewById(R.id.etEmail);
        passwordEditText = findViewById(R.id.etPassword);
        confirmPasswordEditText = findViewById(R.id.etConfirmPassword);
        registerBtn = findViewById(R.id.btnRegisterSubmit);

        // --- Initialize Database Helper ---
        dbHelper = new DBHelper(this);

        // --- Set up Click Listener for "Already have an account? Login" Text ---
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        // --- Set up Click Listener for Register Button ---
        registerBtn.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String pass = passwordEditText.getText().toString().trim();
            String confirmPass = confirmPasswordEditText.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Special case for ADMIN (hardcoded)
            if (email.equalsIgnoreCase("ADMIN") && pass.equals("ADMIN")) {
                // The checkUserExists here will use 'email' which is "ADMIN"
                if (!dbHelper.checkUserExists(email)) { // This assumes checkUserExists now checks by email
                    dbHelper.insertUser(email, pass, "admin");
                }
                Toast.makeText(this, "Bienvenue Admin", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ListUsersActivity.class));
                finish();
                return;
            }

            // Check if user already exists for non-admin registration (using email)
            if (dbHelper.checkUserExists(email)) {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            } else {
                boolean registered = dbHelper.insertUser(email, pass, "user");
                if (registered) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, BooksAdminActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });manageBooksBtn = findViewById(R.id.manageBooksBtn);
        manageBooksBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LivreActivity.class);
                startActivity(intent);
            }
        }); // 👈 Cette accolade fermante doit bien se trouver ici



    }

 }
 