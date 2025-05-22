package com.example.bib.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bib.Reservation;

import java.util.ArrayList;
import java.util.List;

public class ReservationDao {
    private SQLiteDatabase db;

    public ReservationDao(DBHelper dbHelper) {
        this.db = dbHelper.getWritableDatabase();
    }

    public boolean insertReservation(Reservation reservation) {
        ContentValues values = new ContentValues();
        values.put("user_email", reservation.getUserEmail());
        values.put("nom", reservation.getNom());
        values.put("tel", reservation.getTel());
        values.put("cin", reservation.getCin());
        values.put("photo_cin_uri", reservation.getPhotoCinUri());
        values.put("titres_livres", reservation.getTitresLivres());
        values.put("date_reservation", reservation.getDateReservation());
        values.put("statut", reservation.getStatut());

        long result = db.insert("reservations", null, values);
        return result != -1;
    }

    public List<Reservation> getReservationsParEmail(String email) {
        List<Reservation> reservations = new ArrayList<>();
        Cursor cursor = db.query("reservations", null, "user_email = ?", new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                reservations.add(new Reservation(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("user_email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nom")),
                        cursor.getString(cursor.getColumnIndexOrThrow("tel")),
                        cursor.getString(cursor.getColumnIndexOrThrow("cin")),
                        cursor.getString(cursor.getColumnIndexOrThrow("photo_cin_uri")),
                        cursor.getString(cursor.getColumnIndexOrThrow("titres_livres")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date_reservation")),
                        cursor.getString(cursor.getColumnIndexOrThrow("statut"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reservations;
    }
    public boolean updateReservation(Reservation reservation) {
        ContentValues values = new ContentValues();
        values.put("user_email", reservation.getUserEmail());
        values.put("nom", reservation.getNom());
        values.put("tel", reservation.getTel());
        values.put("cin", reservation.getCin());
        values.put("photo_cin_uri", reservation.getPhotoCinUri());
        values.put("titres_livres", reservation.getTitresLivres());
        values.put("date_reservation", reservation.getDateReservation());
        values.put("statut", reservation.getStatut());

        int rows = db.update("reservations", values, "id = ?", new String[]{String.valueOf(reservation.getId())});
        return rows > 0;
    }
    public boolean deleteReservation(int id) {
        int rows = db.delete("reservations", "id = ?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

}
