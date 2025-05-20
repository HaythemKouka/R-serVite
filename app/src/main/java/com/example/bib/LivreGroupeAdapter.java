package com.example.bib;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LivreGroupeAdapter extends RecyclerView.Adapter<LivreGroupeAdapter.TypeViewHolder> {

    private final Map<String, List<Livres>> livresParType;
    private final List<String> types;
    private final Context context;
    private final LivreAdapter.OnLivreClickListener listener;

    private final Map<String, Boolean> expandedStates = new HashMap<>();

    public LivreGroupeAdapter(Context context, Map<String, List<Livres>> livresParType, List<String> types, LivreAdapter.OnLivreClickListener listener) {
        this.context = context;
        this.livresParType = livresParType;
        this.types = types;
        this.listener = listener;

        for (String type : types) {
            expandedStates.put(type, false); // Par défaut, tout est replié
        }
    }

    @NonNull
    @Override
    public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_type_card, parent, false);
        return new TypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TypeViewHolder holder, int position) {
        String type = types.get(position);
        holder.textType.setText(type);

        List<Livres> livres = livresParType.get(type);
        boolean isExpanded = expandedStates.get(type);

        holder.recyclerViewLivres.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        LivreAdapter adapter = new LivreAdapter(livres, listener);
        holder.recyclerViewLivres.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerViewLivres.setAdapter(adapter);

        holder.cardType.setOnClickListener(v -> {
            boolean currentState = expandedStates.get(type);
            expandedStates.put(type, !currentState);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    static class TypeViewHolder extends RecyclerView.ViewHolder {
        TextView textType;
        RecyclerView recyclerViewLivres;
        MaterialCardView cardType;

        public TypeViewHolder(@NonNull View itemView) {
            super(itemView);
            textType = itemView.findViewById(R.id.textTypeGroup);
            recyclerViewLivres = itemView.findViewById(R.id.recyclerViewTypeLivres);
            cardType = itemView.findViewById(R.id.cardTypeGroup);
        }
    }
}
