package com.example.bib;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bib.database.DBHelper;
import com.example.bib.database.LivreDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LivreActivity extends AppCompatActivity {

    private RecyclerView recyclerViewLivres;
    private FloatingActionButton fabAddLivre;

    private LivreDao livreDao;
    private final String[] types = {"Roman", "Essai", "Science", "Histoire", "Art"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livre);

        recyclerViewLivres = findViewById(R.id.recyclerViewLivres);
        fabAddLivre = findViewById(R.id.fabAddLivre);

        DBHelper dbHelper = new DBHelper(this);
        livreDao = new LivreDao(dbHelper);

        recyclerViewLivres.setLayoutManager(new LinearLayoutManager(this));
        loadGroupedLivres();

        fabAddLivre.setOnClickListener(v -> showLivreDialog(null)); // null => ajout
    }

    private void loadGroupedLivres() {
        List<Livres> allLivres = livreDao.getAllLivres();

        // Regrouper les livres par type
        Map<String, List<Livres>> grouped = new HashMap<>();
        for (String type : types) {
            grouped.put(type, new ArrayList<>());
        }

        for (Livres l : allLivres) {
            if (!grouped.containsKey(l.getType())) {
                grouped.put(l.getType(), new ArrayList<>());
            }
            grouped.get(l.getType()).add(l);
        }

        LivreGroupeAdapter groupeAdapter = new LivreGroupeAdapter(
                this, grouped, new ArrayList<>(grouped.keySet()), livre -> showLivreDialog(livre)
        );
        recyclerViewLivres.setAdapter(groupeAdapter);
    }

    private void showLivreDialog(Livres livreToEdit) {
        boolean isEdit = (livreToEdit != null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isEdit ? "Modifier le livre" : "Ajouter un livre");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_livre_form, null);
        EditText editTitre = dialogView.findViewById(R.id.editTitre);
        EditText editAuteur = dialogView.findViewById(R.id.editAuteur);
        EditText editAnnee = dialogView.findViewById(R.id.editAnnee);
        EditText editIsbn = dialogView.findViewById(R.id.editIsbn);
        Spinner spinnerType = dialogView.findViewById(R.id.spinnerType);

        // Setup spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(spinnerAdapter);

        if (isEdit) {
            editTitre.setText(livreToEdit.getTitre());
            editAuteur.setText(livreToEdit.getAuteur());
            editAnnee.setText(String.valueOf(livreToEdit.getAnneePublication()));
            editIsbn.setText(livreToEdit.getIsbn());
            spinnerType.setSelection(spinnerAdapter.getPosition(livreToEdit.getType()));
        }

        builder.setView(dialogView);

        builder.setPositiveButton(isEdit ? "Modifier" : "Ajouter", null);
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        if (isEdit) {
            builder.setNeutralButton("Supprimer", (dialog, which) -> {
                new AlertDialog.Builder(this)
                        .setTitle("Confirmation")
                        .setMessage("Voulez-vous vraiment supprimer ce livre ?")
                        .setPositiveButton("Oui", (confirmDialog, confirmWhich) -> {
                            livreDao.deleteLivre(livreToEdit.getId());
                            Toast.makeText(this, "Livre supprimé", Toast.LENGTH_SHORT).show();
                            loadGroupedLivres();
                        })
                        .setNegativeButton("Non", (confirmDialog, confirmWhich) -> confirmDialog.dismiss())
                        .show();
            });
        }

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnPositive.setOnClickListener(v -> {
                // Validation
                String titre = editTitre.getText().toString().trim();
                String auteur = editAuteur.getText().toString().trim();
                String anneeStr = editAnnee.getText().toString().trim();
                String isbn = editIsbn.getText().toString().trim();
                String type = (spinnerType.getSelectedItem() != null) ? spinnerType.getSelectedItem().toString() : "";

                if (titre.isEmpty() || auteur.isEmpty() || anneeStr.isEmpty() || isbn.isEmpty() || type.isEmpty()) {
                    Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                int annee;
                try {
                    annee = Integer.parseInt(anneeStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Année invalide", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isbn.length() != 10 && isbn.length() != 13) {
                    Toast.makeText(this, "ISBN invalide", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isEdit) {
                    livreToEdit.setTitre(titre);
                    livreToEdit.setAuteur(auteur);
                    livreToEdit.setAnneePublication(annee);
                    livreToEdit.setIsbn(isbn);
                    livreToEdit.setType(type);

                    if (livreDao.updateLivre(livreToEdit)) {
                        Toast.makeText(this, "Livre modifié", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Erreur lors de la modification", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Livres newLivre = new Livres(titre, auteur, annee, isbn, type);
                    if (livreDao.insertLivre(newLivre)) {
                        Toast.makeText(this, "Livre ajouté", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
                    }
                }

                loadGroupedLivres();
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        livreDao.close(); // Fermer proprement la base
    }
}
