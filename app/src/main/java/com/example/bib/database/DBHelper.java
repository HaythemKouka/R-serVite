package com.example.bib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.bib.Book;
import com.example.bib.User; // Ensure this import is correct

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LibraryApp.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_BOOKS = "books";
    private static final String TABLE_RESERVATIONS = "reservations";

    // Common Columns
    private static final String COLUMN_ID = "id";

    // Users Table Columns
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_USER_ROLE = "role";
    // *** Added for consistency: if you use a 'username' field separately from
    // email
    private static final String COLUMN_USER_USERNAME = "username";

    // Books Table Columns
    private static final String COLUMN_BOOK_TITLE = "title";
    private static final String COLUMN_BOOK_AUTHOR = "author";

    // Reservations Table Columns
    // IMPORTANT: If 'email' is the unique user identifier, this FK should reference
    // 'users(email)'
    // I'm assuming for now your 'users' table will have both 'email' (unique) and
    // 'username'
    private static final String COLUMN_RESERVATION_USERNAME = "username"; // Used as FK, needs to match users table
                                                                          // column
    private static final String COLUMN_RESERVATION_BOOK_ID = "book_id";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE users(username TEXT PRIMARY KEY, password TEXT)");
        // Table des livres
        db.execSQL("CREATE TABLE IF NOT EXISTS liv&res(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "titre TEXT, " +
                "auteur TEXT, " +
                "anneePublication INTEGER, " +
                "isbn TEXT, " +
                "type TEXT)");
        // Create Users Table
        // Added 'username' column here
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_EMAIL + " TEXT UNIQUE, " + // Email is unique
                COLUMN_USER_PASSWORD + " TEXT, " +
                COLUMN_USER_ROLE + " TEXT, " +
                COLUMN_USER_USERNAME + " TEXT)"; // Added username column
        db.execSQL(CREATE_USERS_TABLE);

        // Create Books Table
        String CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_BOOKS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BOOK_TITLE + " TEXT, " +
                COLUMN_BOOK_AUTHOR + " TEXT)";
        db.execSQL(CREATE_BOOKS_TABLE);

        // Create Reservations Table
        String CREATE_RESERVATIONS_TABLE = "CREATE TABLE " + TABLE_RESERVATIONS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_RESERVATION_USERNAME + " TEXT, " +
                COLUMN_RESERVATION_BOOK_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_RESERVATION_USERNAME + ") REFERENCES " + TABLE_USERS + "("
                + COLUMN_USER_USERNAME + "), " + // Changed to reference username
                "FOREIGN KEY(" + COLUMN_RESERVATION_BOOK_ID + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_RESERVATIONS_TABLE);

        // --- INSERT DEFAULT ADMIN USER ---
        // This code runs ONLY when the database is created for the very first time.
        insertDefaultAdmin(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This is a destructive upgrade. For production, implement proper schema
        // migration.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
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
        values.put(COLUMN_USER_USERNAME, "ADMIN"); // Default username
        db.insert(TABLE_USERS, null, values);
    }

    // --- User Operations ---

    public boolean insertUser(String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);
        values.put(COLUMN_USER_ROLE, role);
        // If you always want to set username same as email, do this:
        values.put(COLUMN_USER_USERNAME, email);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Check if a user exists by email (consistent with unique email in schema)
    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;
        try {
            cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + "=?",
                    new String[] { email });
            exists = cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return exists;
    }

    // Check user credentials using email and password
    public boolean checkUserCredentials(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean isValid = false;
        try {
            cursor = db.rawQuery(
                    "SELECT " + COLUMN_ID + " FROM " + TABLE_USERS +
                            " WHERE " + COLUMN_USER_EMAIL + "=? AND " + COLUMN_USER_PASSWORD + "=?",
                    new String[] { email, password });
            isValid = cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return isValid;
    }

    // Get user role by email
    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        String role = null;
        try {
            cursor = db.rawQuery(
                    "SELECT " + COLUMN_USER_ROLE + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + "=?",
                    new String[] { email });
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

    // Retrieve all users (assuming User class has id, email, role fields)
    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            // Select all necessary columns to build a User object
            cursor = db.rawQuery("SELECT " + COLUMN_ID + ", " + COLUMN_USER_EMAIL + ", " + COLUMN_USER_USERNAME + ", "
                    + COLUMN_USER_ROLE + " FROM " + TABLE_USERS, null);

            if (cursor.moveToFirst()) {
                int idColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int emailColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL);
                int usernameColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_USERNAME); // Get username
                int roleColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE);

                do {
                    User user = new User();
                    user.setId(cursor.getInt(idColumnIndex));
                    user.setEmail(cursor.getString(emailColumnIndex));
                    user.setUsername(cursor.getString(usernameColumnIndex)); // Set username
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

    // --- Book Operations ---

    public boolean insertBook(String title, String author) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BOOK_TITLE, title);
        cv.put(COLUMN_BOOK_AUTHOR, author);
        long result = db.insert(TABLE_BOOKS, null, cv);
        db.close();
        return result != -1;
    }

 

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT " + COLUMN_ID + ", " + COLUMN_BOOK_TITLE + ", " + COLUMN_BOOK_AUTHOR + " FROM "
                    + TABLE_BOOKS, null);
            if (cursor.moveToFirst()) {
                int idColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
                int titleColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_BOOK_TITLE);
                int authorColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_BOOK_AUTHOR);

                do {
                    books.add(new Book(
                            cursor.getInt(idColumnIndex),
                            cursor.getString(titleColumnIndex),
                            cursor.getString(authorColumnIndex)));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return books;
    }

    public boolean updateBook(int id, String title, String author) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BOOK_TITLE, title);
        cv.put(COLUMN_BOOK_AUTHOR, author);
        int result = db.update(TABLE_BOOKS, cv, COLUMN_ID + "=?", new String[] { String.valueOf(id) });
        db.close();
        return result > 0;
    }

    public boolean deleteBook(int bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_BOOKS, COLUMN_ID + " = ?", new String[] { String.valueOf(bookId) });
        db.close();
        return result > 0;
    }

    // --- Reservation Operations ---

    public boolean reserveBook(String userEmail, int bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_RESERVATION_USERNAME, userEmail); // Assuming this column maps to email used in users table
        cv.put(COLUMN_RESERVATION_BOOK_ID, bookId);
        long result = db.insert(TABLE_RESERVATIONS, null, cv);
        db.close();
        return result != -1;
    }

    public List<String> getReservationsByUser(String userEmail) {
        List<String> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT b." + COLUMN_BOOK_TITLE + ", b." + COLUMN_BOOK_AUTHOR +
                            " FROM " + TABLE_RESERVATIONS + " r JOIN " + TABLE_BOOKS + " b ON r." + COLUMN_RESERVATION_BOOK_ID + " = b." + COLUMN_ID +
                            " WHERE r." + COLUMN_RESERVATION_USERNAME + " = ?",
                    new String[]{userEmail}
            );
            if (cursor.moveToFirst()) {
                int titleColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_BOOK_TITLE);
                int authorColumnIndex = cursor.getColumnIndexOrThrow(COLUMN_BOOK_AUTHOR);
                do {
                    reservations.add(cursor.getString(titleColumnIndex) + " by " + cursor.getString(authorColumnIndex));
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
}