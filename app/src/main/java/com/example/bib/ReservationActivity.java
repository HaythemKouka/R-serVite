package com.example.bib;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.*;
// Ajoute ces imports supplémentaires :
import android.app.AlertDialog;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.example.bib.database.DBHelper;
import com.example.bib.database.LivreDao;
import com.example.bib.database.ReservationDao;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ReservationActivity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView textViewEmail;
    private EditText editTextNom, editTextTel, editTextCin;
    private MultiAutoCompleteTextView autoCompleteLivres;
    private ImageView imageViewPhotoCin;
    private Button buttonChoosePhoto, buttonReserve;
    private TableLayout tableReservations;

    private Uri imageCinUri;
    private Reservation reservationToUpdate = null;

    private DBHelper dbHelper;
    private LivreDao livreDao;
    private ReservationDao reservationDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        textViewEmail = findViewById(R.id.textViewEmail);
        editTextNom = findViewById(R.id.editTextNom);
        editTextTel = findViewById(R.id.editTextTel);
        editTextCin = findViewById(R.id.editTextCin);
        autoCompleteLivres = findViewById(R.id.autoCompleteLivres);
        imageViewPhotoCin = findViewById(R.id.imageViewPhotoCin);
        buttonChoosePhoto = findViewById(R.id.buttonChoosePhoto);
        buttonReserve = findViewById(R.id.buttonReserve);
        tableReservations = findViewById(R.id.tableReservations);

        dbHelper = new DBHelper(this);
        livreDao = new LivreDao(dbHelper);
        reservationDao = new ReservationDao(dbHelper);

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String userEmail = getIntent().getStringExtra("email");
        textViewEmail.setText("Email: " + userEmail);

        List<Livres> livresList = livreDao.getAllLivres();
        List<String> titres = new ArrayList<>();
        for (Livres l : livresList) {
            titres.add(l.getTitre());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, titres);
        autoCompleteLivres.setAdapter(adapter);
        autoCompleteLivres.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        buttonChoosePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        buttonReserve.setOnClickListener(v -> enregistrerReservation(userEmail));

        afficherReservations(userEmail);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageCinUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageCinUri);
                imageViewPhotoCin.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void enregistrerReservation(String userEmail) {
        String nom = editTextNom.getText().toString().trim();
        String tel = editTextTel.getText().toString().trim();
        String cin = editTextCin.getText().toString().trim();
        String titresLivres = autoCompleteLivres.getText().toString().trim();

        if (TextUtils.isEmpty(nom) || TextUtils.isEmpty(tel) || TextUtils.isEmpty(cin) || TextUtils.isEmpty(titresLivres) || imageCinUri == null) {
            Toast.makeText(this, "Veuillez remplir tous les champs et choisir une photo du CIN.", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateRes = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Reservation reservation = new Reservation(0, userEmail, nom, tel, cin, imageCinUri.toString(), titresLivres, dateRes, "en attente");

        boolean success = reservationDao.insertReservation(reservation);
        if (success) {
            Toast.makeText(this, "Réservation enregistrée avec succès.", Toast.LENGTH_SHORT).show();
            afficherReservations(userEmail);
            clearForm();
        } else {
            Toast.makeText(this, "Erreur lors de l'enregistrement.", Toast.LENGTH_SHORT).show();
        }
    }

    private void afficherReservations(String userEmail) {
        List<Reservation> reservations = reservationDao.getReservationsParEmail(userEmail);
        tableReservations.removeAllViews();

        TableRow header = new TableRow(this);
         header.addView(createTextView("Livres"));
        header.addView(createTextView("Date"));
        header.addView(createTextView("Statut"));
        header.addView(createTextView("Actions"));
        tableReservations.addView(header);

        for (Reservation r : reservations) {
            TableRow row = new TableRow(this);
            row.addView(createTextView(r.getNom()));
            row.addView(createTextView(r.getTitresLivres()));
            row.addView(createTextView(r.getDateReservation()));
            row.addView(createTextView(r.getStatut()));

            // Cellule actions (modifier / supprimer)
            LinearLayout actionsLayout = new LinearLayout(this);
            actionsLayout.setOrientation(LinearLayout.HORIZONTAL);

            // Bouton modifier
            ImageButton btnEdit = new ImageButton(this);
            btnEdit.setImageResource(android.R.drawable.ic_menu_edit);
            btnEdit.setBackgroundColor(Color.TRANSPARENT);
            btnEdit.setOnClickListener(v -> afficherDialogModification(r));
            actionsLayout.addView(btnEdit);

            // Bouton supprimer
            ImageButton btnDelete = new ImageButton(this);
            btnDelete.setImageResource(android.R.drawable.ic_menu_delete);
            btnDelete.setBackgroundColor(Color.TRANSPARENT);
            btnDelete.setOnClickListener(v -> confirmerSuppression(r));
            actionsLayout.addView(btnDelete);

            row.addView(actionsLayout);
            tableReservations.addView(row);
        }
    }
    private void confirmerSuppression(Reservation reservation) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation de suppression")
                .setMessage("Voulez-vous vraiment supprimer cette réservation ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    boolean success = reservationDao.deleteReservation(reservation.getId());
                    if (success) {
                        Toast.makeText(this, "Réservation supprimée.", Toast.LENGTH_SHORT).show();
                        afficherReservations(reservation.getUserEmail());
                    } else {
                        Toast.makeText(this, "Erreur lors de la suppression.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Non", null)
                .show();
    }

    private void afficherDialogModification(Reservation r) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifier Réservation");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 20, 20, 20);

        EditText editNom = new EditText(this);
        editNom.setHint("Nom");
        editNom.setText(r.getNom());
        layout.addView(editNom);

        EditText editTel = new EditText(this);
        editTel.setHint("Téléphone");
        editTel.setText(r.getTel());
        layout.addView(editTel);

        EditText editLivres = new EditText(this);
        editLivres.setHint("Livres");
        editLivres.setText(r.getTitresLivres());
        layout.addView(editLivres);

        builder.setView(layout);

        builder.setPositiveButton("Enregistrer", (dialog, which) -> {
            r.setNom(editNom.getText().toString().trim());
            r.setTel(editTel.getText().toString().trim());
            r.setTitresLivres(editLivres.getText().toString().trim());

            boolean success = reservationDao.updateReservation(r);
            if (success) {
                Toast.makeText(this, "Réservation mise à jour.", Toast.LENGTH_SHORT).show();
                afficherReservations(r.getUserEmail());
            } else {
                Toast.makeText(this, "Échec de la mise à jour.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", null);
        builder.show();
    }


    private TextView createTextView(String texte) {
        TextView tv = new TextView(this);
        tv.setText(texte);
        tv.setPadding(10, 10, 10, 10);
        return tv;
    }

    private void clearForm() {
        editTextNom.setText("");
        editTextTel.setText("");
        editTextCin.setText("");
        autoCompleteLivres.setText("");
        imageViewPhotoCin.setImageResource(android.R.color.transparent);
        imageCinUri = null;
    }
}
