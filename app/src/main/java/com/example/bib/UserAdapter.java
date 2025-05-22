package com.example.bib;

import android.content.Context;
import android.content.Intent; // Needed for starting new Activity
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bib.database.DBHelper;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private Context context;
    private DBHelper dbHelper;
    private OnUserActionListener listener; // Declare the listener

    // Define the listener interface
    public interface OnUserActionListener {
        void onDeleteUser(String email);
        void onResetPassword(String email);
        void onViewReservations(String email); // New callback method for reservations
    }

    // Modify the constructor to accept the listener
    public UserAdapter(Context context, List<User> users, DBHelper dbHelper, OnUserActionListener listener) {
        this.context = context;
        this.userList = users;
        this.dbHelper = dbHelper;
        this.listener = listener; // Initialize the listener
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false); // Make sure this is user_item_layout not item_user
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Display user email for both username and email TextViews for clarity,
        // or ensure your User object correctly provides username/email.
        // Assuming tvUserName shows the main identifier, which is often email for login.
        holder.tvUserName.setText("Email: " + user.getEmail());
        // holder.tvUserName.setText(user.getUsername() != null ? user.getUsername() : "N/A"); // If you have a separate username
        holder.tvUserEmail.setText(user.getEmail()); // Keeping this as a duplicate for now, adjust as per your UI need
        holder.tvUserRole.setText("Role: " + user.getRole());


        holder.btnDeleteUser.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmation")
                    .setMessage("Supprimer cet utilisateur ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        if (listener != null) { // Check if listener is set
                            listener.onDeleteUser(user.getEmail());
                        }
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });


        holder.btnResetPassword.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Reset Password")
                    .setMessage("Voulez-vous réinitialiser le mot de passe de " + user.getEmail() + " ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        if (listener != null) { // Check if listener is set
                            listener.onResetPassword(user.getEmail());
                        }
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });

        // --- NEW: Set OnClickListener for the View Reservations button ---
        holder.btnViewReservations.setOnClickListener(v -> {
            if (listener != null) { // Crucially, check if the listener is set
                listener.onViewReservations(user.getEmail()); // Call the new callback method
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // --- Helper methods to update data and handle UI (if needed) ---
    public void deleteUserFromList(String email) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getEmail().equals(email)) {
                userList.remove(i);
                notifyItemRemoved(i);
                notifyItemRangeChanged(i, userList.size());
                break;
            }
        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail, tvUserRole;
        Button btnResetPassword, btnDeleteUser, btnViewReservations; // Declare the new button

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            btnResetPassword = itemView.findViewById(R.id.btnResetPassword);
            btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);
            btnViewReservations = itemView.findViewById(R.id.btnViewReservations); // Initialize the new button
        }
    }
}