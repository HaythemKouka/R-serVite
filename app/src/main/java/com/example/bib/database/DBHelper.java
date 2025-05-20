package com.example.bib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.bib.Book;
import com.example.bib.User;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
     public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT role FROM users WHERE email=?", new String[]{email});
        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            return role;
        }
        cursor.close();
        return null;
    }


    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM books", null);
        if (cursor.moveToFirst()) {
            do {
                books.add(new Book(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return books;
    }
    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE username=? AND password=?",
                new String[]{username, password}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean deleteBook(int bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("books", "id = ?", new String[]{String.valueOf(bookId)});
        return result > 0;
    }
    public DBHelper(Context context) {
        super(context, "Userdata.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT UNIQUE, " +
                "password TEXT, " +
                "role TEXT)");        db.execSQL("CREATE TABLE books(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, author TEXT)");
        db.execSQL("CREATE TABLE reservations(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, book_id INTEGER, " +
                "FOREIGN KEY(username) REFERENCES users(username), " +
                "FOREIGN KEY(book_id) REFERENCES books(id))");
    }
    // Ajouter un livre
    public boolean insertBook(String title, String author) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("author", author);
        return db.insert("books", null, cv) != -1;
    }

    // Récupérer tous les livres


    // Mettre à jour un livre
    public boolean updateBook(int id, String title, String author) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("author", author);
        return db.update("books", cv, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    // Supprimer un livre

    // Réserver un livre
    public boolean reserveBook(String username, int bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("book_id", bookId);
        return db.insert("reservations", null, cv) != -1;
    }
    public List<String> getReservationsByUser(String username) {
        List<String> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT b.title, b.author FROM reservations r JOIN books b ON r.book_id = b.id WHERE r.username = ?",
                new String[]{username}
        );
        while (cursor.moveToNext()) {
            reservations.add(cursor.getString(0) + " by " + cursor.getString(1));
        }
        cursor.close();
        return reservations;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

     public boolean insertUser(String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("password", password);
        values.put("role", role);
        long result = db.insert("users", null, values);
        return result != -1;
    }



    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // 1. Query the database to get raw data (rows/columns)
        // The result is stored in a Cursor object.
        Cursor cursor = db.rawQuery("SELECT id, username, email FROM users", null);

        if (cursor.moveToFirst()) {
            // 2. Get column indices for efficient data retrieval
            // This maps the column name to its position in the current cursor.
            int idColumnIndex = cursor.getColumnIndex("id");
            int usernameColumnIndex = cursor.getColumnIndex("username");
            int emailColumnIndex = cursor.getColumnIndex("email");

            do {
                // 3. Create a new Java object (User) for each database row
                User user = new User();

                // 4. Map database column values to properties of the Java object
                // We use the column indices to get the specific data type from the cursor
                // and then set it to the corresponding field in our User object.
                if (idColumnIndex != -1) {
                    user.setId(cursor.getInt(idColumnIndex));
                }
                if (usernameColumnIndex != -1) {
                    user.setUsername(cursor.getString(usernameColumnIndex));
                }
                if (emailColumnIndex != -1) {
                    user.setEmail(cursor.getString(emailColumnIndex));
                }

                // 5. Add the fully mapped User object to your list
                userList.add(user);
            } while (cursor.moveToNext()); // Move to the next row (next User)
        }

        cursor.close();
        db.close();
        return userList;
    }

    public boolean checkUserExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username=?", new String[]{username});
        return cursor.getCount() > 0;
    }
}
