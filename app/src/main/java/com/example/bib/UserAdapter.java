package com.example.bib;

import android.content.Context;
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

    public UserAdapter(Context context, List<User> users, DBHelper dbHelper) {
        this.context = context;
        this.userList = users;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvUserName.setText("Email: " + user.getEmail());
        holder.tvUserEmail.setText(user.getEmail());
        holder.tvUserRole.setText("Role: " + user.getRole());

        holder.btnDeleteUser.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmation")
                    .setMessage("Supprimer cet utilisateur ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        dbHelper.deleteUser(user.getEmail()); // méthode à créer
                        userList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, userList.size());
                        Toast.makeText(context, "Utilisateur supprimé", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });


        holder.btnResetPassword.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Reset Password")
                    .setMessage("Voulez-vous réinitialiser le mot de passe de " + user.getEmail() + " ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        boolean updated = dbHelper.resetPassword(user.getEmail(), user.getEmail());
                        if (updated) {
                            Toast.makeText(context, "Mot de passe réinitialisé à : " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Échec de la réinitialisation", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail, tvUserRole;
        Button btnResetPassword;
        Button btnDeleteUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser); // <- ici

            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            btnResetPassword = itemView.findViewById(R.id.btnResetPassword);
        }
    }
}
