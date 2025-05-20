package com.example.bib;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bib.database.DBHelper;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText emailEditText, passwordEditText;
    Button registerBtn;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.etEmail);
        passwordEditText = findViewById(R.id.etPassword);
        registerBtn = findViewById(R.id.btnLoginSubmit);

        dbHelper = new DBHelper(this);

        registerBtn.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String pass = passwordEditText.getText().toString().trim();

            if(email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Si admin, redirige vers ListUsersActivity
            if(email.equals("ADMIN") && pass.equals("ADMIN")) {
                Intent intent = new Intent(RegisterActivity.this, ListUsersActivity.class);
                startActivity(intent);
                finish(); // pour ne pas revenir à cette activité avec le bouton back
                return;
            }

            // Sinon, inscription normale
            if(dbHelper.checkUserExists(email)) {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            } else {
                boolean registered = dbHelper.insertUser(email, pass);
                if(registered) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                    // Optionnel: redirection vers login
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
