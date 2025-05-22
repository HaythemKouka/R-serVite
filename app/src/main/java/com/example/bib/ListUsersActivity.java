package com.example.bib; // Adjust package name

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.bib.database.DBHelper;

import java.util.Objects; // For Objects.requireNonNull

public class ListUsersActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private DBHelper dbHelper; // You'll need this in the Activity too for DB operations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users); // Your main layout

        dbHelper = new DBHelper(this); // Initialize your database helper

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        // Link the TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(viewPagerAdapter.getTabTitle(position))
        ).attach();

        // Optional: Listen for tab changes if you need specific actions
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Refresh the current fragment's data when its tab is selected
                // This ensures up-to-date data if something changed while on another tab
                int position = tab.getPosition();
                UserListFragment fragment = (UserListFragment) viewPagerAdapter.createFragment(position);
                fragment.refreshUserList();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No specific action needed here
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No specific action needed here
            }
        });
    }

    // --- Implement the OnUserActionListener methods ---
    // These methods will be called by the UserAdapter in the fragments
    // and executed in this hosting Activity.

    @Override
    public void onDeleteUser(String email) {
        // Handle delete logic here
        boolean deleted = dbHelper.deleteUser(email);
        if (deleted) {
            Toast.makeText(this, "Utilisateur " + email + " supprimé", Toast.LENGTH_SHORT).show();
            // After deletion, refresh the data in both fragments
            // The simplest way is to iterate through known fragment types and refresh
            for (int i = 0; i < viewPagerAdapter.getItemCount(); i++) {
                UserListFragment fragment = (UserListFragment) viewPagerAdapter.createFragment(i);
                fragment.refreshUserList(); // This will reload data based on role
            }
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

    // Optional: Refresh data when returning to this Activity (e.g., from UserReservationsActivity)
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the currently visible fragment
        if (viewPager != null && viewPagerAdapter != null) {
            UserListFragment currentFragment = (UserListFragment) viewPagerAdapter.createFragment(viewPager.getCurrentItem());
            currentFragment.refreshUserList();
        }
    }
}