package com.example.bib;

public class Livres {
    private int id;
    private String titre;
    private String auteur;
    private int anneePublication;
    private String isbn;
    private String type;

    // Constructeurs, getters, setters
    public Livres(int id, String titre, String auteur, int anneePublication, String isbn, String type) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.anneePublication = anneePublication;
        this.isbn = isbn;
        this.type = type;
    }

    public Livres(String titre, String auteur, int anneePublication, String isbn, String type) {
        this(-1, titre, auteur, anneePublication, isbn, type);
    }

    public int getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getType() {
        return type;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getAnneePublication() {
        return anneePublication;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public void setAnneePublication(int anneePublication) {
        this.anneePublication = anneePublication;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }
}
