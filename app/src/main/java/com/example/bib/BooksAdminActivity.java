package com.example.bib;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bib.database.DBHelper;
import com.google.android.material.textfield.TextInputEditText;

public class BooksAdminActivity extends AppCompatActivity {
    TextInputEditText titleInput = findViewById(R.id.titleInput);
    TextInputEditText authorInput = findViewById(R.id.authorInput);
    Button addBookBtn = findViewById(R.id.addBookBtn);
    RecyclerView booksRecyclerView = findViewById(R.id.booksRecyclerView);

    DBHelper dbHelper;

    BooksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_admin);

        dbHelper = new DBHelper(this);
        titleInput = findViewById(R.id.titleInput);
        authorInput = findViewById(R.id.authorInput);
        addBookBtn = findViewById(R.id.addBookBtn);
        booksRecyclerView = findViewById(R.id.booksRecyclerView);

        adapter = new BooksAdapter(dbHelper.getAllLivres(), dbHelper);
        booksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        booksRecyclerView.setAdapter(adapter);

        addBookBtn.setOnClickListener(v -> {
            String title = titleInput.getText().toString();
            String author = authorInput.getText().toString();
            if (!title.isEmpty() && !author.isEmpty()) {
                dbHelper.insertLivre(title, author);
                adapter.setBooks(dbHelper.getAllLivres());
                titleInput.setText("");
                authorInput.setText("");
            }
        });
    }
}
