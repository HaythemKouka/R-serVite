// package com.example.bib; (or wherever you put your adapters)
package com.example.bib; // Recommended to put adapters in a separate package

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bib.database.DBHelper; // Import DBHelper

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

        TextView tvReservationDetails = convertView.findViewById(R.id.tvReservationDetails);
        TextView tvReservationCIN = convertView.findViewById(R.id.tvReservationCIN);
        TextView tvReservationUserEmail = convertView.findViewById(R.id.tvReservationUserEmail);
        Button btnValidateReservation = convertView.findViewById(R.id.btnValidateReservation);

        // --- Fetch Book Details for display (assuming you add getLivreById to DBHelper) ---
        Livres associatedLivre = dbHelper.getLivreById(currentReservation.getLivreId());
        String livreInfo = "Livre non trouvé";
        if (associatedLivre != null) {
            livreInfo = associatedLivre.getTitre() + " par " + associatedLivre.getAuteur();
        }

        // Set the text for the TextViews
        tvReservationDetails.setText("Livre: " + livreInfo +
                " | Date: " + currentReservation.getDateReservation() +
                " | Statut: " + currentReservation.getStatut());
        tvReservationCIN.setText("CIN: " + currentReservation.getCin());
        tvReservationUserEmail.setText("Email: " + currentReservation.getUserEmail());


        // --- Button Visibility and Click Listener ---
        if ("en attente".equalsIgnoreCase(currentReservation.getStatut())) {
            btnValidateReservation.setVisibility(View.VISIBLE);
            btnValidateReservation.setOnClickListener(v -> {
                // Logic to update reservation status in DB
                boolean success = dbHelper.updateReservationStatus(currentReservation.getId(), "validée");
                if (success) {
                    currentReservation.setStatut("validée"); // Update local object
                    notifyDataSetChanged(); // Refresh the ListView to show updated status
                    Toast.makeText(context, "Réservation validée!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Échec de la validation.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            btnValidateReservation.setVisibility(View.GONE); // Hide button if not "en attente"
            btnValidateReservation.setOnClickListener(null); // Clear listener for recycled views
        }

        return convertView;
    }
}