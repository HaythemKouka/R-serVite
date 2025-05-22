package com.example.bib;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bib.database.DBHelper;

import java.util.List;

// 1. Make ListUsersActivity implement the interface
public class ListUsersActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users); // Make sure this matches your layout file name

        dbHelper = new DBHelper(this);
        recyclerView = findViewById(R.id.usersRecyclerView); // Ensure this ID matches your layout

        userList = dbHelper.getAllUsers();

        // 2. Pass 'this' as the fourth argument to the adapter constructor
        userAdapter = new UserAdapter(this, userList, dbHelper, this); // <-- Corrected line
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
    }

    // --- Implement the OnUserActionListener methods ---

    @Override
    public void onDeleteUser(String email) {
        // Handle delete logic here
        boolean deleted = dbHelper.deleteUser(email);
        if (deleted) {
            userAdapter.deleteUserFromList(email); // Use the adapter's helper method to update UI
            Toast.makeText(this, "Utilisateur " + email + " supprimé", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Échec de la suppression de l'utilisateur", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResetPassword(String email) {
        // Handle reset password logic here
        boolean updated = dbHelper.resetPassword(email, email); // Reset to email as password
        if (updated) {
            Toast.makeText(this, "Mot de passe de " + email + " réinitialisé à: " + email, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Échec de la réinitialisation du mot de passe", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewReservations(String email) {
        // This is where you launch the new activity to show reservations
        Intent intent = new Intent(this, UserReservationsActivity.class);
        intent.putExtra("USER_EMAIL", email); // Pass the user's email to the new activity
        startActivity(intent);
    }

    // Optional: Refresh the user list when the activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        // It's good practice to refresh the list if changes might have occurred
        // (e.g., if a user was deleted/modified from another screen, or if the list isn't updated by the listener)
        List<User> updatedUserList = dbHelper.getAllUsers();
        userList.clear(); // Clear existing data
        userList.addAll(updatedUserList); // Add new data
        userAdapter.notifyDataSetChanged(); // Notify adapter that data has changed
    }
}