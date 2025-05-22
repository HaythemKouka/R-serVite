package com.example.bib;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View; // Needed for View.GONE / View.VISIBLE for empty state
import androidx.appcompat.app.AppCompatActivity;

import com.example.bib.database.DBHelper;
import com.example.bib.Reservation; // Crucial: Import your Reservation class
import com.example.bib.ReservationAdapter; // Crucial: Import your custom ReservationAdapter
import com.example.bib.UserAdapter; // Assuming UserAdapter is in 'adapters' package

import java.util.ArrayList;
import java.util.List;

public class UserReservationsActivity extends AppCompatActivity
        implements UserAdapter.OnUserActionListener { // Keep this if this activity is ever a listener for user actions

    private TextView tvReservationsHeader;
    private ListView lvReservations;
    private TextView tvEmptyReservations; // CRITICAL: This was missing and caused a previous error
    private DBHelper dbHelper;
    private ReservationAdapter adapter; // CRITICAL: This was missing and caused an error

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reservations);

        // Initialize UI elements
        tvReservationsHeader = findViewById(R.id.tvReservationsHeader);
        lvReservations = findViewById(R.id.lvReservations);
        tvEmptyReservations = findViewById(R.id.tvEmptyReservations); // Initialize the empty view TextView

        dbHelper = new DBHelper(this);

        // Set the empty view for the ListView. This makes the ListView automatically
        // show tvEmptyReservations when its adapter is empty.
        lvReservations.setEmptyView(tvEmptyReservations);

        // Determine if we're viewing reservations for a specific user or all reservations (admin view)
        String userEmail = getIntent().getStringExtra("USER_EMAIL");

        if (userEmail != null && !userEmail.isEmpty()) {
            tvReservationsHeader.setText("Réservations pour: " + userEmail);
            loadReservations(userEmail); // Call the generic loadReservations
        } else {
            // This path would typically be for an admin viewing ALL reservations
            tvReservationsHeader.setText("Toutes les Réservations");
            loadReservations(null); // Pass null to signify loading all reservations
        }
    }

    /**
     * Loads reservations into the ListView.
     * If userEmail is provided, loads reservations for that user.
     * If userEmail is null, attempts to load all reservations (for admin view).
     */
    private void loadReservations(String userEmail) {
        List<Reservation> reservations;

        if (userEmail != null && !userEmail.isEmpty()) {
            // Get reservations for a specific user
            reservations = dbHelper.getReservationsByUserObject(userEmail);
        } else {
            // Get all reservations (for admin view)
            // You need to implement this method in your DBHelper.
            // Example:
            reservations = dbHelper.getAllReservations(); // Assumes you've added this method
            // If you haven't added getAllReservations(), uncomment this for a temporary fix:
            // reservations = new ArrayList<>(); // Or throw an error / show a message
        }

        // Initialize the adapter if it's null (first time) or update its data
        if (adapter == null) {
            adapter = new ReservationAdapter(this, reservations, dbHelper);
            lvReservations.setAdapter(adapter);
        } else {
            // If adapter already exists, update its underlying data and notify
            adapter.clear(); // Clear existing data from the adapter
            adapter.addAll(reservations); // Add new data to the adapter
            adapter.notifyDataSetChanged(); // Tell the adapter its data has changed
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list whenever the activity resumes. This is crucial if
        // a validation or deletion happens in another activity and you return here.
        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        loadReservations(userEmail);
    }

    // --- Implementation of UserAdapter.OnUserActionListener (if this activity acts as one) ---
    // These methods would be called if this activity is registered as a listener
    // for a UserAdapter (e.g., in an AdminUsersActivity that lists users).

    @Override
    public void onDeleteUser(String email) {
        // Implement deletion logic here if needed
    }

    @Override
    public void onResetPassword(String email) {
        // Implement password reset logic here if needed
    }

    @Override
    public void onViewReservations(String email) {
        // This method is correctly designed to launch this same activity
        // to view reservations for a specific user.
        Intent intent = new Intent(this, UserReservationsActivity.class);
        intent.putExtra("USER_EMAIL", email); // Pass the user's email
        startActivity(intent);
    }
}