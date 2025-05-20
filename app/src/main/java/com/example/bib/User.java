package com.example.bib;



public class User {
    private int id;
    private String email;

    private String username;
    private String password;

    private String role;

    public User( ) {
       super();
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public User(String password, String role, String username, String email, int id) {
        this.password = password;
        this.role = role;
        this.username = username;
        this.email = email;
        this.id = id;
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
