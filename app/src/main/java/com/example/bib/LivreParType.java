package com.example.bib;

import java.util.List;

public class LivreParType {
    private String type;
    private List<Livres> livres;
    private boolean expanded = false;

    public LivreParType(String type, List<Livres> livres) {
        this.type = type;
        this.livres = livres;
    }

    public String getType() { return type; }
    public List<Livres> getLivres() { return livres; }
    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }
}

