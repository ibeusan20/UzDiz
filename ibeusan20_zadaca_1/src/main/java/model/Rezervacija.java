package model;

import java.time.LocalDateTime;

/**
 * Klasa koja opisuje rezervaciju turističkog aranžmana.
 */
public class Rezervacija {

    private final String ime;
    private final String prezime;
    private final String oznakaAranzmana;
    private final LocalDateTime datumVrijeme;
    private final String vrsta; // "PA", "Č", "O"
    private final LocalDateTime datumVrijemeOtkaza;

    // osnovni konstruktor
    public Rezervacija(String ime, String prezime, String oznakaAranzmana, LocalDateTime datumVrijeme) {
        this(ime, prezime, oznakaAranzmana, datumVrijeme, "PA", null);
    }

    // puni konstruktor
    public Rezervacija(String ime, String prezime, String oznakaAranzmana,
                       LocalDateTime datumVrijeme, String vrsta, LocalDateTime datumVrijemeOtkaza) {
        this.ime = ime;
        this.prezime = prezime;
        this.oznakaAranzmana = oznakaAranzmana;
        this.datumVrijeme = datumVrijeme;
        this.vrsta = vrsta;
        this.datumVrijemeOtkaza = datumVrijemeOtkaza;
    }

    public String getIme() { return ime; }
    public String getPrezime() { return prezime; }
    public String getOznakaAranzmana() { return oznakaAranzmana; }
    public LocalDateTime getDatumVrijeme() { return datumVrijeme; }
    public String getVrsta() { return vrsta; }
    public LocalDateTime getDatumVrijemeOtkaza() { return datumVrijemeOtkaza; }
}
