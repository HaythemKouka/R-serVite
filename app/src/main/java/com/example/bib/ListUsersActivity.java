package com.example.bib;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bib.database.DBHelper;

import java.util.List;

public class ListUsersActivity extends AppCompatActivity {

    RecyclerView usersRecyclerView;
    UserAdapter userAdapter;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        usersRecyclerView = findViewById(R.id.usersRecyclerView);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DBHelper(this);

        // Supposons que dbHelper ait une méthode getAllUsers() qui renvoie List<String> emails
         List<User> userList = dbHelper.getAllUsers();

        userAdapter = new UserAdapter(userList);
        usersRecyclerView.setAdapter(userAdapter);
    }
}
