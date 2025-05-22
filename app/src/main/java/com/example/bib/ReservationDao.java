package com.example.bib;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.bib.database.DBHelper;

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
        values.put("cin", reservation.getCin());
        values.put("photo_cin", reservation.getPhotoCin());
        values.put("livre_id", reservation.getLivreId());
        values.put("date_reservation", reservation.getDateReservation());
        values.put("statut", reservation.getStatut());

        long result = db.insert("reservations", null, values);
        return result != -1;
    }

    public List<Reservation> getReservationsByUser(String email) {
        List<Reservation> reservations = new ArrayList<>();
        Cursor cursor = db.query("reservations", null, "user_email = ?", new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                reservations.add(new Reservation(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("user_email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("cin")),
                        cursor.getBlob(cursor.getColumnIndexOrThrow("photo_cin")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("livre_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date_reservation")),
                        cursor.getString(cursor.getColumnIndexOrThrow("statut"))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reservations;
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
