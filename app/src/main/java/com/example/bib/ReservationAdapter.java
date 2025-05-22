package com.example.bib;// package com.example.bib.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.bib.Livres;
import com.example.bib.R;
import com.example.bib.Reservation;
import com.example.bib.database.DBHelper;

import java.util.List;

public class ReservationAdapter extends ArrayAdapter<Reservation> {

    private Context context;
    private List<Reservation> reservationsList;
    private DBHelper dbHelper;

    public ReservationAdapter(@NonNull Context context, List<Reservation> reservations, DBHelper dbHelper) {
        super(context, 0, reservations);
        this.context = context;
        this.reservationsList = reservations;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_reservation, parent, false);
        }

        Reservation currentReservation = reservationsList.get(position);
        // Create an effectively final copy of currentReservation
        // This is the key fix for the "Variable used in lambda..." error
        final Reservation reservationToValidate = currentReservation;


        TextView tvReservationDetails = convertView.findViewById(R.id.tvReservationDetails);
        TextView tvReservationCIN = convertView.findViewById(R.id.tvReservationCIN);
        TextView tvReservationUserEmail = convertView.findViewById(R.id.tvReservationUserEmail);
        Button btnValidateReservation = convertView.findViewById(R.id.btnValidateReservation);

        // --- Fetch Book Details for display ---
        Livres associatedLivre = dbHelper.getLivreById(reservationToValidate.getLivreId()); // Use the effectively final variable
        String livreInfo = "Livre non trouvé";
        if (associatedLivre != null) {
            livreInfo = associatedLivre.getTitre() + " par " + associatedLivre.getAuteur();
        }

        // Set the text for the TextViews
        tvReservationDetails.setText("Livre: " + livreInfo +
                " | Date: " + reservationToValidate.getDateReservation() + // Use the effectively final variable
                " | Statut: " + reservationToValidate.getStatut()); // Use the effectively final variable
        tvReservationCIN.setText("CIN: " + reservationToValidate.getCin()); // Use the effectively final variable
        tvReservationUserEmail.setText("Email: " + reservationToValidate.getUserEmail()); // Use the effectively final variable


        // --- Button Visibility and Click Listener with Confirmation ---
        if ("en attente".equalsIgnoreCase(reservationToValidate.getStatut())) { // Use the effectively final variable
            btnValidateReservation.setVisibility(View.VISIBLE);
            final String finalLivreInfo = livreInfo; // Also make livreInfo effectively final if used in lambda
            btnValidateReservation.setOnClickListener(v -> {
                // --- Show Confirmation Dialog ---
                new AlertDialog.Builder(context)
                        .setTitle("Confirmer la validation")
                        .setMessage("Êtes-vous sûr de vouloir valider cette réservation pour \"" + finalLivreInfo + "\" ?") // Use finalLivreInfo
                        .setPositiveButton("Oui, valider", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User confirmed, proceed with validation
                                // Use the effectively final variable inside the lambda
                                boolean success = dbHelper.updateReservationStatus(reservationToValidate.getId(), "validée");
                                if (success) {
                                    reservationToValidate.setStatut("validée"); // Update local object
                                    notifyDataSetChanged(); // Refresh the ListView to show updated status
                                    Toast.makeText(context, "Réservation validée!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Échec de la validation.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User cancelled, do nothing
                                dialog.dismiss();
                            }
                        })
                        .show();
            });
        } else {
            btnValidateReservation.setVisibility(View.GONE);
            btnValidateReservation.setOnClickListener(null);
        }

        return convertView;
    }
}