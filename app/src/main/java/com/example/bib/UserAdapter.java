package com.example.bib;// package com.example.bib;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bib.User; // Ensure this import is correct
import com.example.bib.R; // Make sure your R file is imported correctly

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    // Constructor to receive the list of users
    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_user.xml layout for each list item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        // Get the User object for the current position
        User user = userList.get(position);

        // Bind the User data to the TextViews in the ViewHolder
        // Note: Your User class needs 'getEmail()' and 'getRole()' methods.
        // Assuming your User class has a username property (or you can use email as display name)
        holder.tvUserName.setText(user.getEmail()); // Or user.getUsername() if you store it
        holder.tvUserEmail.setText("Email: " + user.getEmail());
        holder.tvUserRole.setText("Role: " + user.getRole());
    }

    @Override
    public int getItemCount() {
        // Return the total number of users in the list
        return userList.size();
    }

    // ViewHolder class: Holds references to the views for each item
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        TextView tvUserEmail;
        TextView tvUserRole;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the TextViews from item_user.xml
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
        }
    }
}