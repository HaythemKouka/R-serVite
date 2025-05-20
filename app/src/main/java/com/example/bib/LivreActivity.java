package com.example.bib;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bib.database.DBHelper;
import com.example.bib.database.LivreDao;

import java.util.List;

public class LivreActivity extends AppCompatActivity {

    private EditText editTitre, editAuteur, editAnnee, editIsbn;
    private Spinner spinnerType;
    private Button btnAdd, btnUpdate, btnDelete;
    private ListView listViewLivres;

    private LivreDao livreDao;
    private List<Livres> livresList;
    private ArrayAdapter<String> listAdapter;

    private int selectedLivreId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livre);

        editTitre = findViewById(R.id.editTitre);
        editAuteur = findViewById(R.id.editAuteur);
        editAnnee = findViewById(R.id.editAnnee);
        editIsbn = findViewById(R.id.editIsbn);
        spinnerType = findViewById(R.id.spinnerType);
        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        listViewLivres = findViewById(R.id.listViewLivres);

        // Initialiser la DAO
        DBHelper dbHelper = new DBHelper(this);
        livreDao = new LivreDao(dbHelper);

        // Remplir spinner avec des types
        String[] types = {"Roman", "Essai", "Science", "Histoire", "Art"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(spinnerAdapter);

        // Charger la liste des livres
        loadLivres();

        // Bouton ajouter
        btnAdd.setOnClickListener(v -> {
            if (addLivre()) {
                clearFields();
                loadLivres();
            }
        });

        // Bouton modifier
        btnUpdate.setOnClickListener(v -> {
            if (updateLivre()) {
                clearFields();
                loadLivres();
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
                btnAdd.setEnabled(true);
            }
        });

        // Bouton supprimer
        btnDelete.setOnClickListener(v -> {
            if (deleteLivre()) {
                clearFields();
                loadLivres();
                btnUpdate.setEnabled(false);
                btnDelete.setEnabled(false);
                btnAdd.setEnabled(true);
            }
        });

        // Gestion sélection liste
        listViewLivres.setOnItemClickListener((parent, view, position, id) -> {
            Livres selectedLivre = livresList.get(position);
            selectedLivreId = selectedLivre.getId();

            editTitre.setText(selectedLivre.getTitre());
            editAuteur.setText(selectedLivre.getAuteur());
            editAnnee.setText(String.valueOf(selectedLivre.getAnneePublication()));
            editIsbn.setText(selectedLivre.getIsbn());

            // Set spinner selection
            int spinnerPosition = spinnerAdapter.getPosition(selectedLivre.getType());
            spinnerType.setSelection(spinnerPosition);

            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        });
    }

    private boolean addLivre() {
        String titre = editTitre.getText().toString().trim();
        String auteur = editAuteur.getText().toString().trim();
        String anneeStr = editAnnee.getText().toString().trim();
        String isbn = editIsbn.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();

        if (titre.isEmpty() || auteur.isEmpty() || anneeStr.isEmpty() || isbn.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return false;
        }

        int annee;
        try {
            annee = Integer.parseInt(anneeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Année invalide", Toast.LENGTH_SHORT).show();
            return false;
        }

        Livres livre = new Livres(titre, auteur, annee, isbn, type);
        boolean inserted = livreDao.insertLivre(livre);
        if (inserted) {
            Toast.makeText(this, "Livre ajouté", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Erreur lors de l'ajout", Toast.LENGTH_SHORT).show();
        }
        return inserted;
    }

    private boolean updateLivre() {
        if (selectedLivreId == -1) {
            Toast.makeText(this, "Aucun livre sélectionné", Toast.LENGTH_SHORT).show();
            return false;
        }

        String titre = editTitre.getText().toString().trim();
        String auteur = editAuteur.getText().toString().trim();
        String anneeStr = editAnnee.getText().toString().trim();
        String isbn = editIsbn.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();

        if (titre.isEmpty() || auteur.isEmpty() || anneeStr.isEmpty() || isbn.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return false;
        }

        int annee;
        try {
            annee = Integer.parseInt(anneeStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Année invalide", Toast.LENGTH_SHORT).show();
            return false;
        }

        Livres livre = new Livres(selectedLivreId, titre, auteur, annee, isbn, type);
        boolean updated = livreDao.updateLivre(livre);
        if (updated) {
            Toast.makeText(this, "Livre modifié", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Erreur lors de la modification", Toast.LENGTH_SHORT).show();
        }
        return updated;
    }

    private boolean deleteLivre() {
        if (selectedLivreId == -1) {
            Toast.makeText(this, "Aucun livre sélectionné", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean deleted = livreDao.deleteLivre(selectedLivreId);
        if (deleted) {
            Toast.makeText(this, "Livre supprimé", Toast.LENGTH_SHORT).show();
            selectedLivreId = -1;
        } else {
            Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
        }
        return deleted;
    }

    private void loadLivres() {
        livresList = livreDao.getAllLivres();
        List<String> titres = new java.util.ArrayList<>();
        for (Livres l : livresList) {
            titres.add(l.getTitre() + " (" + l.getAuteur() + ")");
        }
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titres);
        listViewLivres.setAdapter(listAdapter);
    }

    private void clearFields() {
        editTitre.setText("");
        editAuteur.setText("");
        editAnnee.setText("");
        editIsbn.setText("");
        spinnerType.setSelection(0);
        selectedLivreId = -1;
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
}
