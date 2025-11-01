package model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Klasa Aranzman predstavlja turistički aranžman.
 * 
 * Kreira se pomoću kreacijskog uzorka Builder (klasa AranzmanBuilder).
 * 
 * Objekt je nepromjenjiv (immutable).
 */
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

    /**
     * Konstruktor dostupan samo Builderu.
     */
    protected Aranzman(AranzmanBuilder builder) {
        this.oznaka = builder.getOznaka();
        this.naziv = builder.getNaziv();
        this.program = builder.getProgram();
        this.pocetniDatum = builder.getPocetniDatum();
        this.zavrsniDatum = builder.getZavrsniDatum();
        this.vrijemeKretanja = builder.getVrijemeKretanja();
        this.vrijemePovratka = builder.getVrijemePovratka();
        this.cijena = builder.getCijena();
        this.minPutnika = builder.getMinPutnika();
        this.maxPutnika = builder.getMaxPutnika();
    }

    // ------------------------------------------------------------
    // GETTERI
    // ------------------------------------------------------------

    public String getOznaka() {
        return oznaka;
    }

    public String getNaziv() {
        return naziv;
    }

    public String getProgram() {
        return program;
    }

    public LocalDate getPocetniDatum() {
        return pocetniDatum;
    }

    public LocalDate getZavrsniDatum() {
        return zavrsniDatum;
    }

    public LocalTime getVrijemeKretanja() {
        return vrijemeKretanja;
    }

    public LocalTime getVrijemePovratka() {
        return vrijemePovratka;
    }

    public float getCijena() {
        return cijena;
    }

    public int getMinPutnika() {
        return minPutnika;
    }

    public int getMaxPutnika() {
        return maxPutnika;
    }

    // ------------------------------------------------------------
    // ToString - za eventualne debug ispise
    // ------------------------------------------------------------
    @Override
    public String toString() {
        return "Aranzman{" +
                "oznaka='" + oznaka + '\'' +
                ", naziv='" + naziv + '\'' +
                ", pocetniDatum=" + pocetniDatum +
                ", zavrsniDatum=" + zavrsniDatum +
                ", cijena=" + cijena +
                ", minPutnika=" + minPutnika +
                ", maxPutnika=" + maxPutnika +
                '}';
    }
}
