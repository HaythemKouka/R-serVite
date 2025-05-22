package com.example.bib;

public class Reservation {
    private int id;
    private String userEmail;
    private String cin;
    private byte[] photoCin;
    private int livreId;
    private String dateReservation;
    private String statut;

    public Reservation(int id, String userEmail, String cin, byte[] photoCin, int livreId, String dateReservation, String statut) {
        this.id = id;
        this.userEmail = userEmail;
        this.cin = cin;
        this.photoCin = photoCin;
        this.livreId = livreId;
        this.dateReservation = dateReservation;
        this.statut = statut;
    }

    // Getters et setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }

    public byte[] getPhotoCin() { return photoCin; }
    public void setPhotoCin(byte[] photoCin) { this.photoCin = photoCin; }

    public int getLivreId() { return livreId; }
    public void setLivreId(int livreId) { this.livreId = livreId; }

    public String getDateReservation() { return dateReservation; }
    public void setDateReservation(String dateReservation) { this.dateReservation = dateReservation; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
