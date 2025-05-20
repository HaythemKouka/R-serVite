package com.example.bib;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LivreParTypeActivity extends AppCompatActivity implements LivreAdapter.OnLivreClickListener {

    private RecyclerView recyclerView;
    private LivreGroupeAdapter groupeAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livre);

        recyclerView = findViewById(R.id.recyclerViewLivres);

        // Simuler données de livres
        List<Livres> livres = creerLivresSample();

        // Regrouper par type
        Map<String, List<Livres>> livresParType = new HashMap<>();
        for (Livres livre : livres) {
            String type = livre.getType();
            if (!livresParType.containsKey(type)) {
                livresParType.put(type, new ArrayList<>());
            }
            livresParType.get(type).add(livre);
        }

        // Liste des types (pour l’ordre d’affichage)
        List<String> types = new ArrayList<>(livresParType.keySet());

        groupeAdapter = new LivreGroupeAdapter(this, livresParType, types, this);
        recyclerView.setAdapter(groupeAdapter);
    }

    @Override
    public void onLivreClick(Livres livre) {
        Toast.makeText(this, "Livre sélectionné : " + livre.getTitre(), Toast.LENGTH_SHORT).show();
    }

    private List<Livres> creerLivresSample() {
        List<Livres> livres = new ArrayList<>();
        return livres;
    }
}
