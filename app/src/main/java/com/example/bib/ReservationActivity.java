package com.example.bib;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bib.database.DBHelper;
import com.example.bib.Reservation;
import com.example.bib.ReservationDao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity {

    private EditText editTextCin;
    private ImageView imageViewPhotoCin;
    private Button buttonChoosePhoto, buttonReserve;

    private byte[] photoCinBytes = null;

    private final int livreIdChoisi = 1; // exemple, tu peux remplacer par un vrai id sélectionné
    private final String userEmail = "user@example.com"; // simuler user connecté (à remplacer)

    private ActivityResultLauncher<Intent> pickImageLauncher;

    private DBHelper dbHelper;
    private ReservationDao reservationDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        editTextCin = findViewById(R.id.editTextCin);
        imageViewPhotoCin = findViewById(R.id.imageViewPhotoCin);
        buttonChoosePhoto = findViewById(R.id.buttonChoosePhoto);
        buttonReserve = findViewById(R.id.buttonReserve);

        dbHelper = new DBHelper(this);
        reservationDao = new ReservationDao(dbHelper);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            imageViewPhotoCin.setImageBitmap(bitmap);
                            photoCinBytes = bitmapToBytes(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Erreur chargement image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        buttonChoosePhoto.setOnClickListener(v -> openGallery());

        buttonReserve.setOnClickListener(v -> submitReservation());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void submitReservation() {
        String cin = editTextCin.getText().toString().trim();

        if (cin.isEmpty()) {
            editTextCin.setError("Veuillez saisir votre CIN");
            return;
        }
        if (photoCinBytes == null) {
            Toast.makeText(this, "Veuillez choisir une photo du CIN", Toast.LENGTH_SHORT).show();
            return;
        }

        String dateNow = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        Reservation reservation = new Reservation(0, userEmail, cin, photoCinBytes, livreIdChoisi, dateNow, "en attente");

        boolean success = reservationDao.insertReservation(reservation);
        if (success) {
            Toast.makeText(this, "Réservation enregistrée avec succès", Toast.LENGTH_LONG).show();
            // Réinitialiser formulaire si besoin
            editTextCin.setText("");
            imageViewPhotoCin.setImageResource(android.R.color.transparent);
            photoCinBytes = null;
        } else {
            Toast.makeText(this, "Erreur lors de la réservation", Toast.LENGTH_LONG).show();
        }
    }

    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reservationDao.close();
        dbHelper.close();
    }
}
