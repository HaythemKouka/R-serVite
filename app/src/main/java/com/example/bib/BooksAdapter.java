package com.example.bib;


import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bib.database.DBHelper;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.BookViewHolder> {

    private List<Livres> books;
    private DBHelper dbHelper;

    public BooksAdapter(List<Livres> books, DBHelper dbHelper) {
        this.books = books;
        this.dbHelper = dbHelper;
    }

    public void setBooks(List<Livres> updatedBooks) {
        this.books = updatedBooks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Livres book = books.get(position);
        holder.title.setText("Titre: " + book.getTitre());
        holder.author.setText("Auteur: " + book.getAuteur());

        holder.deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Confirmation")
                    .setMessage("Supprimer ce livre ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        dbHelper.deleteLivre(book.getId());
                        setBooks(dbHelper.getAllLivres());
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView title, author;
        Button deleteBtn;

        BookViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            author = itemView.findViewById(R.id.bookAuthor);
            deleteBtn = itemView.findViewById(R.id.deleteBookBtn);
        }
    }
}
