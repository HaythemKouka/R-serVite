package com.example.bib;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TypeLivreAdapter extends RecyclerView.Adapter<TypeLivreAdapter.TypeViewHolder> {

    private final List<String> types;
    private final Context context;

    public TypeLivreAdapter(Context context, List<String> types) {
        this.context = context;
        this.types = types;
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

        // Choisir une image en fonction du type
        int iconResId = getIconForType(type);
        holder.icon.setImageResource(iconResId);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LivreParType.class);
            intent.putExtra("TYPE_LIVRE", type);
            context.startActivity(intent);
        });
    }

    private int getIconForType(String type) {
        String iconName;
        switch (type.toLowerCase()) {
            case "alphabet":
                iconName = "ic_alphabet";
                break;
            case "nombres":
                iconName = "ic_numbers";
                break;
            case "formes":
                iconName = "ic_shapes";
                break;
            case "couleurs":
                iconName = "ic_colors";
                break;
            case "fruits":
                iconName = "ic_fruits";
                break;
            default:
                iconName = "ic_placeholder";
                break;
        }
        int resId = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
        if (resId == 0) {
            // Ressource non trouvée, on renvoie ic_placeholder par défaut
            resId = context.getResources().getIdentifier("ic_placeholder", "drawable", context.getPackageName());
        }
        return resId;
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    static class TypeViewHolder extends RecyclerView.ViewHolder {
        TextView textType;
        ImageView icon;

        public TypeViewHolder(@NonNull View itemView) {
            super(itemView);
            textType = itemView.findViewById(R.id.textTypeGroup);
            //icon = itemView.findViewById(R.id.iconTypeImage);
        }
    }
}
