package com.example.bib;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LivreAdapter extends RecyclerView.Adapter<LivreAdapter.LivreViewHolder> {

    public interface OnLivreClickListener {
        void onLivreClick(Livres livre);
    }

    private final List<Livres> livres;
    private final OnLivreClickListener listener;

    private final int[] cardColors = {
            Color.parseColor("#FFCDD2"),
            Color.parseColor("#C8E6C9"),
            Color.parseColor("#BBDEFB"),
            Color.parseColor("#FFF9C4"),
            Color.parseColor("#D1C4E9")
    };

    public LivreAdapter(List<Livres> livres, OnLivreClickListener listener) {
        this.livres = livres;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LivreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_livre_card, parent, false);
        return new LivreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LivreViewHolder holder, int position) {
        Livres livre = livres.get(position);
        holder.textTitre.setText(livre.getTitre());
        holder.textAuteur.setText("Auteur : " + livre.getAuteur());
        holder.textAnnee.setText("Année : " + livre.getAnneePublication());
        holder.textType.setText("Type : " + livre.getType());

        int color = cardColors[position % cardColors.length];
        holder.cardView.setCardBackgroundColor(color);

        holder.itemView.setOnClickListener(v -> listener.onLivreClick(livre));
    }

    @Override
    public int getItemCount() {
        return livres.size();
    }

    static class LivreViewHolder extends RecyclerView.ViewHolder {
        TextView textTitre, textAuteur, textAnnee, textType;
        com.google.android.material.card.MaterialCardView cardView;

        public LivreViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (com.google.android.material.card.MaterialCardView) itemView;
            textTitre = itemView.findViewById(R.id.textTitre);
            textAuteur = itemView.findViewById(R.id.textAuteur);
            textAnnee = itemView.findViewById(R.id.textAnnee);
            textType = itemView.findViewById(R.id.textType);
        }
    }
}
