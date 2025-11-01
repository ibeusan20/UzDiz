package model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Aranzman {

    private final String oznaka;
    private final String naziv;
    private final String program;
    private final LocalDate pocetniDatum;
    private final LocalDate zavrsniDatum;
    private final LocalTime vrijemeKretanja;
    private final LocalTime vrijemePovratka;
    private final float cijena;
    private final int minPutnika;
    private final int maxPutnika;

    protected Aranzman(AranzmanBuilder builder) {
        this.oznaka = builder.oznaka;
        this.naziv = builder.naziv;
        this.program = builder.program;
        this.pocetniDatum = builder.pocetniDatum;
        this.zavrsniDatum = builder.zavrsniDatum;
        this.vrijemeKretanja = builder.vrijemeKretanja;
        this.vrijemePovratka = builder.vrijemePovratka;
        this.cijena = builder.cijena;
        this.minPutnika = builder.minPutnika;
        this.maxPutnika = builder.maxPutnika;
    }

    public String getOznaka() { return oznaka; }
    public String getNaziv() { return naziv; }
    public String getProgram() { return program; }
    public LocalDate getPocetniDatum() { return pocetniDatum; }
    public LocalDate getZavrsniDatum() { return zavrsniDatum; }
    public LocalTime getVrijemeKretanja() { return vrijemeKretanja; }
    public LocalTime getVrijemePovratka() { return vrijemePovratka; }
    public float getCijena() { return cijena; }
    public int getMinPutnika() { return minPutnika; }
    public int getMaxPutnika() { return maxPutnika; }
}
