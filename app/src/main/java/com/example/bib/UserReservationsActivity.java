package com.example.bib;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bib.database.DBHelper; // Ensure this import is correct

import java.util.List;

public class UserReservationsActivity extends AppCompatActivity
        implements UserAdapter.OnUserActionListener {

    private TextView tvReservationsHeader;
    private ListView lvReservations;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reservations); // You'll create this XML next

        tvReservationsHeader = findViewById(R.id.tvReservationsHeader);
        lvReservations = findViewById(R.id.lvReservations);
        dbHelper = new DBHelper(this);

        // Get the user's email passed from the previous activity
        String userEmail = getIntent().getStringExtra("USER_EMAIL");

        if (userEmail != null) {
            tvReservationsHeader.setText("Réservations pour: " + userEmail);
            loadUserReservations(userEmail);
        } else {
            tvReservationsHeader.setText("Erreur: Email d'utilisateur non trouvé.");
        }
    }

    private void loadUserReservations(String userEmail) {
        List<String> reservations = dbHelper.getReservationDetailsByUser(userEmail);

        if (reservations.isEmpty()) {
            // Display a message if no reservations are found
            lvReservations.setEmptyView(findViewById(R.id.tvEmptyReservations)); // Set an empty view
            // Make sure you have a TextView with id tvEmptyReservations in your layout
        }

        // Create an ArrayAdapter to display the reservations in the ListView
        // android.R.layout.simple_list_item_1 is a simple built-in layout for text items
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                reservations
        );
        lvReservations.setAdapter(adapter);
    }

    @Override
    public void onDeleteUser(String email) {

    }

    @Override
    public void onResetPassword(String email) {

    }

    @Override
     public void onViewReservations(String email) {
        // This is where you launch the new activity to show reservations
        Intent intent = new Intent(this, UserReservationsActivity.class);
        intent.putExtra("USER_EMAIL", email); // Pass the user's email to the new activity
        startActivity(intent);
    }
}