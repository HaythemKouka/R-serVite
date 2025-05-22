package com.example.bib;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bib.database.DBHelper;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText emailEditText, passwordEditText, confirmPasswordEditText;
    Button registerBtn, manageBooksBtn, buttonGoToReservation;
    TextView tvLoginLink;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.etEmail);
        passwordEditText = findViewById(R.id.etPassword);
        confirmPasswordEditText = findViewById(R.id.etConfirmPassword);
        registerBtn = findViewById(R.id.btnRegisterSubmit);
        tvLoginLink = findViewById(R.id.tvLoginLink);
        manageBooksBtn = findViewById(R.id.manageBooksBtn);
        buttonGoToReservation = findViewById(R.id.buttonGoToReservation); // Assure-toi qu’il existe dans le layout XML

        dbHelper = new DBHelper(this);

        tvLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

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

            if (email.equalsIgnoreCase("ADMIN") && pass.equals("ADMIN")) {
                if (!dbHelper.checkUserExists(email)) {
                    dbHelper.insertUser(email, pass, "admin");
                }
                Toast.makeText(this, "Bienvenue Admin", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, ListUsersActivity.class));
                finish();
                return;
            }

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
        });

        manageBooksBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LivreActivity.class);
            startActivity(intent);
        });

        buttonGoToReservation.setOnClickListener(v -> {
            String emailValue = emailEditText.getText().toString().trim();
            Intent intent = new Intent(RegisterActivity.this, ReservationActivity.class);
            intent.putExtra("email", emailValue);
            startActivity(intent);
        });
    }
}
