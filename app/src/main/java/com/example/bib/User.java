package com.example.bib;



public class User {
    private int id;
    private String email;

    private String username;
    private String password;

    public User( ) {
       super();
    }
    public User(int id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public int getId() { return id; }

    public String getEmail() { return email; }

    public String getPassword() { return password; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    // Tu peux ajouter setters si nécessaire
}
