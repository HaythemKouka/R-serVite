package com.example.bib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.bib.Livres; // Corrected: Use Livres class
import com.example.bib.User;
import com.example.bib.Reservation; // Corrected: Use Reservation class

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LibraryApp.db";
    private static final int DATABASE_VERSION = 3; // Increment version to trigger onUpgrade

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_LIVRES = "livres"; // Consistent with Livres class
    private static final String TABLE_RESERVATIONS = "reservations"; // Consistent with Reservation class

    // Common Columns
    private static final String COLUMN_ID = "id";

    // Users Table Columns
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_USER_ROLE = "role";
    private static final String COLUMN_USER_USERNAME = "username";

    // Livres Table Columns (from your Livres class)
    private static final String COLUMN_LIVRE_TITRE = "titre";
    private static final String COLUMN_LIVRE_AUTEUR = "auteur";
    private static final String COLUMN_LIVRE_ANNEE_PUBLICATION = "anneePublication";
    private static final String COLUMN_LIVRE_ISBN = "isbn";
    private static final String COLUMN_LIVRE_TYPE = "type";

    // Reservations Table Columns (from your Reservation class)
    private static final String COLUMN_RESERVATION_USER_EMAIL = "user_email";
    private static final String COLUMN_RESERVATION_CIN = "cin";
    private static final String COLUMN_RESERVATION_PHOTO_CIN = "photo_cin";
    private static final String COLUMN_RESERVATION_LIVRE_ID = "livre_id";
    private static final String COLUMN_RESERVATION_DATE = "date_reservation";
    private static final String COLUMN_RESERVATION_STATUT = "statut";


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public boolean deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_USERS, COLUMN_USER_EMAIL + " = ?", new String[]{email});
        return deletedRows > 0;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Livres Table (Main book table)
        String CREATE_LIVRES_TABLE = "CREATE TABLE " + TABLE_LIVRES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LIVRE_TITRE + " TEXT, " +
                COLUMN_LIVRE_AUTEUR + " TEXT, " +
                COLUMN_LIVRE_ANNEE_PUBLICATION + " INTEGER, " +
                COLUMN_LIVRE_ISBN + " TEXT, " +
                COLUMN_LIVRE_TYPE + " TEXT)";
        db.execSQL(CREATE_LIVRES_TABLE);

        // Create Users Table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_EMAIL + " TEXT UNIQUE, " +
                COLUMN_USER_PASSWORD + " TEXT, " +
                COLUMN_USER_ROLE + " TEXT, " +
                COLUMN_USER_USERNAME + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);

        // Create Reservations Table (Using the detailed schema from your Reservation class)
        String CREATE_RESERVATIONS_TABLE_SQL = "CREATE TABLE " + TABLE_RESERVATIONS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_RESERVATION_USER_EMAIL + " TEXT NOT NULL," +
                COLUMN_RESERVATION_CIN + " TEXT NOT NULL," +
                COLUMN_RESERVATION_PHOTO_CIN + " BLOB," +
                COLUMN_RESERVATION_LIVRE_ID + " INTEGER NOT NULL," +
                COLUMN_RESERVATION_DATE + " TEXT NOT NULL," +
                COLUMN_RESERVATION_STATUT + " TEXT NOT NULL DEFAULT 'en attente'," +
                "FOREIGN KEY(" + COLUMN_RESERVATION_USER_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_EMAIL + ")," +
                "FOREIGN KEY(" + COLUMN_RESERVATION_LIVRE_ID + ") REFERENCES " + TABLE_LIVRES + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_RESERVATIONS_TABLE_SQL);

        // --- INSERT DEFAULT USERS, BOOK, AND RESERVATION ---
        insertDefaultAdmin(db);
        insertDefaultUserAndReservation(db);
    }
// Inside com.example.bib.database.DBHelper.java

// ... (existing code)

    // --- NEW METHOD: Get Livres by ID ---
    @Nullable // Use Nullable as it might return null if not found
    public Livres getLivreById(int livreId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Livres livre = null;
        try {
            cursor = db.rawQuery(
                    "SELECT " + COLUMN_ID + ", " + COLUMN_LIVRE_TITRE + ", " + COLUMN_LIVRE_AUTEUR + ", " +
                            COLUMN_LIVRE_ANNEE_PUBLICATION + ", " + COLUMN_LIVRE_ISBN + ", " + COLUMN_LIVRE_TYPE +
                            " FROM " + TABLE_LIVRES +
                            " WHERE " + COLUMN_ID + " = ?",
                    new String[]{String.valueOf(livreId)}
            );
            if (cursor.moveToFirst()) {
                int idColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int titreColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_LIVRE_TITRE);
                int auteurColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_LIVRE_AUTEUR);
                int anneePublicationColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_LIVRE_ANNEE_PUBLICATION);
                int isbnColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_LIVRE_ISBN);
                int typeColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_LIVRE_TYPE);

                livre = new Livres(
                        cursor.getInt(idColumnIndex),
                        cursor.getString(titreColumnIndex),
                        cursor.getString(auteurColumnIndex),
                        cursor.getInt(anneePublicationColumnIndex),
                        cursor.getString(isbnColumnIndex),
                        cursor.getString(typeColumnIndex)
                );
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return livre;
    }


    // --- NEW METHOD: Update Reservation Status ---
    public boolean updateReservationStatus(int reservationId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RESERVATION_STATUT, newStatus);
        int rowsAffected = db.update(
                TABLE_RESERVATIONS,
                values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(reservationId)}
        );
        db.close();
        return rowsAffected > 0;
    }

    // ... (rest of your DBHelper code)
    public boolean resetPassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_PASSWORD, newPassword);
        int rows = db.update(TABLE_USERS, values, COLUMN_USER_EMAIL + "=?", new String[]{email});
        return rows > 0;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables in reverse order of dependency
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIVRES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db); // Recreate tables
    }

    /**
     * Helper method to insert the default admin user.
     * Called only once when the database is first created.
     */
    private void insertDefaultAdmin(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, "haythemkk56@gmail.com");
        values.put(COLUMN_USER_PASSWORD, "ADMIN");
        values.put(COLUMN_USER_ROLE, "admin");
        values.put(COLUMN_USER_USERNAME, "ADMIN");
        db.insert(TABLE_USERS, null, values);
    }

    /**
     * Helper method to insert a default user, a default book,
     * and a default reservation linking them.
     * Called only once when the database is first created.
     */
    private void insertDefaultUserAndReservation(SQLiteDatabase db) {
        // 1. Insert Default User: user@user.user
        String defaultUserEmail = "user@user.user";
        // Check if user already exists
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT 1 FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + " = ?", new String[]{defaultUserEmail});
            if (cursor.getCount() == 0) { // User does not exist, so insert
                ContentValues userValues = new ContentValues();
                userValues.put(COLUMN_USER_EMAIL, defaultUserEmail);
                userValues.put(COLUMN_USER_PASSWORD, "user123");
                userValues.put(COLUMN_USER_ROLE, "user");
                userValues.put(COLUMN_USER_USERNAME, "Default User");
                db.insert(TABLE_USERS, null, userValues);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // 2. Insert a Default Livres (Book)
        ContentValues livreValues = new ContentValues();
        livreValues.put(COLUMN_LIVRE_TITRE, "Le Petit Prince");
        livreValues.put(COLUMN_LIVRE_AUTEUR, "Antoine de Saint-Exupéry");
        livreValues.put(COLUMN_LIVRE_ANNEE_PUBLICATION, 1943);
        livreValues.put(COLUMN_LIVRE_ISBN, "978-2070417937");
        livreValues.put(COLUMN_LIVRE_TYPE, "Roman");
        long livreId = db.insert(TABLE_LIVRES, null, livreValues);

        // 3. Insert a Default Reservation (only if book was inserted successfully)
        if (livreId != -1) {
            ContentValues reservationValues = new ContentValues();
            reservationValues.put(COLUMN_RESERVATION_USER_EMAIL, defaultUserEmail);
            reservationValues.put(COLUMN_RESERVATION_CIN, "12345678"); // Example CIN
            // reservationValues.put(COLUMN_RESERVATION_PHOTO_CIN, new byte[0]); // Optional: empty byte array for photo
            reservationValues.put(COLUMN_RESERVATION_LIVRE_ID, livreId);
            reservationValues.put(COLUMN_RESERVATION_DATE, "2025-05-22"); // Current date
            reservationValues.put(COLUMN_RESERVATION_STATUT, "en attente");
            db.insert(TABLE_RESERVATIONS, null, reservationValues);
        }
    }

    // --- User Operations ---

    public boolean insertUser(String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);
        values.put(COLUMN_USER_ROLE, role);
        values.put(COLUMN_USER_USERNAME, email);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;
        try {
            cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + "=?",
                    new String[]{email});
            exists = cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return exists;
    }

    public boolean checkUserCredentials(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean isValid = false;
        try {
            cursor = db.rawQuery(
                    "SELECT " + COLUMN_ID + " FROM " + TABLE_USERS +
                            " WHERE " + COLUMN_USER_EMAIL + "=? AND " + COLUMN_USER_PASSWORD + "=?",
                    new String[]{email, password});
            isValid = cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return isValid;
    }

    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String role = null;
        try {
            cursor = db.rawQuery(
                    "SELECT " + COLUMN_USER_ROLE + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + "=?",
                    new String[]{email});
            if (cursor.moveToFirst()) {
                role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return role;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT " + COLUMN_ID + ", " + COLUMN_USER_EMAIL + ", " + COLUMN_USER_USERNAME + ", "
                    + COLUMN_USER_ROLE + " FROM " + TABLE_USERS, null);

            if (cursor.moveToFirst()) {
                int idColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int emailColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL);
                int usernameColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_USERNAME);
                int roleColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE);

                do {
                    User user = new User();
                    user.setId(cursor.getInt(idColumnIndex));
                    user.setEmail(cursor.getString(emailColumnIndex));
                    user.setUsername(cursor.getString(usernameColumnIndex));
                    user.setRole(cursor.getString(roleColumnIndex));
                    userList.add(user);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return userList;
    }


    // --- Livres Operations (Corrected from Book) ---

    // Renamed insertBook to insertLivre for consistency
    public boolean insertLivre(String titre, String auteur, int anneePublication, String isbn, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LIVRE_TITRE, titre);
        cv.put(COLUMN_LIVRE_AUTEUR, auteur);
        cv.put(COLUMN_LIVRE_ANNEE_PUBLICATION, anneePublication);
        cv.put(COLUMN_LIVRE_ISBN, isbn);
        cv.put(COLUMN_LIVRE_TYPE, type);
        long result = db.insert(TABLE_LIVRES, null, cv);
        db.close();
        return result != -1;
    }

    // Corrected: Return type changed from List<Book> to List<Livres>
    public List<Livres> getAllLivres() {
        List<Livres> livres = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT " + COLUMN_ID + ", " + COLUMN_LIVRE_TITRE + ", " + COLUMN_LIVRE_AUTEUR + ", " +
                    COLUMN_LIVRE_ANNEE_PUBLICATION + ", " + COLUMN_LIVRE_ISBN + ", " + COLUMN_LIVRE_TYPE +
                    " FROM " + TABLE_LIVRES, null);
            if (cursor.moveToFirst()) {
                int idColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int titreColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_LIVRE_TITRE);
                int auteurColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_LIVRE_AUTEUR);
                int anneePublicationColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_LIVRE_ANNEE_PUBLICATION);
                int isbnColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_LIVRE_ISBN);
                int typeColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_LIVRE_TYPE);

                do {
                    livres.add(new Livres(
                            cursor.getInt(idColumnIndex),
                            cursor.getString(titreColumnIndex),
                            cursor.getString(auteurColumnIndex),
                            cursor.getInt(anneePublicationColumnIndex),
                            cursor.getString(isbnColumnIndex),
                            cursor.getString(typeColumnIndex)));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return livres;
    }

    // Renamed updateBook to updateLivre
    public boolean updateLivre(int id, String titre, String auteur, int anneePublication, String isbn, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LIVRE_TITRE, titre);
        cv.put(COLUMN_LIVRE_AUTEUR, auteur);
        cv.put(COLUMN_LIVRE_ANNEE_PUBLICATION, anneePublication);
        cv.put(COLUMN_LIVRE_ISBN, isbn);
        cv.put(COLUMN_LIVRE_TYPE, type);
        int result = db.update(TABLE_LIVRES, cv, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // Renamed deleteBook to deleteLivre
    public boolean deleteLivre(int livreId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_LIVRES, COLUMN_ID + " = ?", new String[]{String.valueOf(livreId)});
        db.close();
        return result > 0;
    }

    // --- Reservation Operations (Corrected from old 'reserveBook' & 'getReservationsByUser') ---

    // New method to insert a full Reservation object
    public boolean insertReservation(Reservation reservation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RESERVATION_USER_EMAIL, reservation.getUserEmail());
        cv.put(COLUMN_RESERVATION_CIN, reservation.getCin());
        cv.put(COLUMN_RESERVATION_PHOTO_CIN, reservation.getPhotoCin());
        cv.put(COLUMN_RESERVATION_LIVRE_ID, reservation.getLivreId());
        cv.put(COLUMN_RESERVATION_DATE, reservation.getDateReservation());
        cv.put(COLUMN_RESERVATION_STATUT, reservation.getStatut());
        long result = db.insert(TABLE_RESERVATIONS, null, cv);
        db.close();
        return result != -1;
    }

    // Updated getReservationsByUser to return a List<Reservation> objects
    public List<Reservation> getReservationsByUserObject(String userEmail) {
        List<Reservation> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT " + COLUMN_ID + ", " + COLUMN_RESERVATION_USER_EMAIL + ", " + COLUMN_RESERVATION_CIN + ", " +
                            COLUMN_RESERVATION_PHOTO_CIN + ", " + COLUMN_RESERVATION_LIVRE_ID + ", " + COLUMN_RESERVATION_DATE + ", " +
                            COLUMN_RESERVATION_STATUT +
                            " FROM " + TABLE_RESERVATIONS +
                            " WHERE " + COLUMN_RESERVATION_USER_EMAIL + " = ?",
                    new String[]{userEmail}
            );
            if (cursor.moveToFirst()) {
                int idColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int userEmailColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_RESERVATION_USER_EMAIL);
                int cinColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_RESERVATION_CIN);
                int photoCinColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_RESERVATION_PHOTO_CIN);
                int livreIdColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_RESERVATION_LIVRE_ID);
                int dateReservationColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_RESERVATION_DATE);
                int statutColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_RESERVATION_STATUT);

                do {
                    reservations.add(new Reservation(
                            cursor.getInt(idColumnIndex),
                            cursor.getString(userEmailColumnIndex),
                            cursor.getString(cinColumnIndex),
                            cursor.getBlob(photoCinColumnIndex),
                            cursor.getInt(livreIdColumnIndex),
                            cursor.getString(dateReservationColumnIndex),
                            cursor.getString(statutColumnIndex)
                    ));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return reservations;
    }

    // New method to get full details of a reservation including book title/author
    public List<String> getReservationDetailsByUser(String userEmail) {
        List<String> reservationDetails = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            // Join the 'reservations' table with the 'livres' table
            cursor = db.rawQuery(
                    "SELECT r." + COLUMN_ID + ", r." + COLUMN_RESERVATION_DATE + ", r." + COLUMN_RESERVATION_STATUT + ", " +
                            "l." + COLUMN_LIVRE_TITRE + ", l." + COLUMN_LIVRE_AUTEUR +
                            " FROM " + TABLE_RESERVATIONS + " r JOIN " + TABLE_LIVRES + " l ON r." + COLUMN_RESERVATION_LIVRE_ID + " = l." + COLUMN_ID +
                            " WHERE r." + COLUMN_RESERVATION_USER_EMAIL + " = ?",
                    new String[]{userEmail}
            );
            if (cursor.moveToFirst()) {
                int dateIndex = cursor.getColumnIndexOrThrow(COLUMN_RESERVATION_DATE);
                int statutIndex = cursor.getColumnIndexOrThrow(COLUMN_RESERVATION_STATUT);
                int titreIndex = cursor.getColumnIndexOrThrow(COLUMN_LIVRE_TITRE);
                int auteurIndex = cursor.getColumnIndexOrThrow(COLUMN_LIVRE_AUTEUR);

                do {
                    String title = cursor.getString(titreIndex);
                    String author = cursor.getString(auteurIndex);
                    String date = cursor.getString(dateIndex);
                    String status = cursor.getString(statutIndex);
                    reservationDetails.add("Livre: " + title + " par " + author + " | Date: " + date + " | Statut: " + status);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return reservationDetails;
    }

    public void insertLivre(String title, String author) {
    }
}