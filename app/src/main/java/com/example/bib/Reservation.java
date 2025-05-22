package com.example.bib;

public class Reservation {
    private int id;
    private String userEmail;
    private String nom;
    private String tel;
    private String cin;
    private String photoCinUri;
    private String titresLivres;
    private String dateReservation;
    private String statut;

    public Reservation(int id, String userEmail, String nom, String tel, String cin, String photoCinUri, String titresLivres, String dateReservation, String statut) {
        this.id = id;
        this.userEmail = userEmail;
        this.nom = nom;
        this.tel = tel;
        this.cin = cin;
        this.photoCinUri = photoCinUri;
        this.titresLivres = titresLivres;
        this.dateReservation = dateReservation;
        this.statut = statut;
    }

    // Getters et setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getTel() { return tel; }
    public void setTel(String tel) { this.tel = tel; }

    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public String getPhotoCinUri() { return photoCinUri; }
    public void setPhotoCinUri(String photoCinUri) { this.photoCinUri = photoCinUri; }

    public String getTitresLivres() { return titresLivres; }
    public void setTitresLivres(String titresLivres) { this.titresLivres = titresLivres; }

    public String getDateReservation() { return dateReservation; }
    public void setDateReservation(String dateReservation) { this.dateReservation = dateReservation; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
