package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class AranzmanBuilder {

    String oznaka;
    String naziv;
    String program;
    LocalDate pocetniDatum;
    LocalDate zavrsniDatum;
    LocalTime vrijemeKretanja;
    LocalTime vrijemePovratka;
    float cijena;
    int minPutnika;
    int maxPutnika;

    public AranzmanBuilder postaviOznaku(String oznaka) {
        this.oznaka = oznaka;
        return this;
    }

    public AranzmanBuilder postaviNaziv(String naziv) {
        this.naziv = naziv;
        return this;
    }

    public AranzmanBuilder postaviProgram(String program) {
        this.program = program;
        return this;
    }

    public AranzmanBuilder postaviPocetniDatum(LocalDate datum) {
        this.pocetniDatum = datum;
        return this;
    }

    public AranzmanBuilder postaviZavrsniDatum(LocalDate datum) {
        this.zavrsniDatum = datum;
        return this;
    }

    public AranzmanBuilder postaviVrijemeKretanja(LocalTime vrijeme) {
        this.vrijemeKretanja = vrijeme;
        return this;
    }

    public AranzmanBuilder postaviVrijemePovratka(LocalTime vrijeme) {
        this.vrijemePovratka = vrijeme;
        return this;
    }

    public AranzmanBuilder postaviCijenu(float cijena) {
        this.cijena = cijena;
        return this;
    }

    public AranzmanBuilder postaviMinPutnika(int min) {
        this.minPutnika = min;
        return this;
    }

    public AranzmanBuilder postaviMaxPutnika(int max) {
        this.maxPutnika = max;
        return this;
    }

    public Aranzman izgradi() {
        return new Aranzman(this);
    }
}
