package com.example.bib.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bib.Livres;  // ta classe modèle

import java.util.ArrayList;
import java.util.List;

public class LivreDao {
    private SQLiteDatabase db;

    public LivreDao(DBHelper dbHelper) {
        this.db = dbHelper.getWritableDatabase();
    }

    public boolean insertLivre(Livres livre) {
        ContentValues values = new ContentValues();
        values.put("titre", livre.getTitre());
        values.put("auteur", livre.getAuteur());
        values.put("anneePublication", livre.getAnneePublication());
        values.put("isbn", livre.getIsbn());
        values.put("type", livre.getType());
        long result = db.insert("livres", null, values);
        return result != -1;
    }

    public List<Livres> getAllLivres() {
        List<Livres> livres = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM livres", null);
        if (cursor.moveToFirst()) {
            do {
                livres.add(new Livres(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return livres;
    }

    public boolean updateLivre(Livres livre) {
        ContentValues values = new ContentValues();
        values.put("titre", livre.getTitre());
        values.put("auteur", livre.getAuteur());
        values.put("anneePublication", livre.getAnneePublication());
        values.put("isbn", livre.getIsbn());
        values.put("type", livre.getType());
        int rows = db.update("livres", values, "id=?", new String[]{String.valueOf(livre.getId())});
        return rows > 0;
    }

    public boolean deleteLivre(int id) {
        int rows = db.delete("livres", "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }
}
