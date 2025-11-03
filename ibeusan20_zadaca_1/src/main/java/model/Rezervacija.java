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
  private String vrsta; // "PA", "Č", "O"
  private LocalDateTime datumVrijemeOtkaza;
  private boolean aktivna;

  // osnovni konstruktor
  public Rezervacija(String ime, String prezime, String oznakaAranzmana,
      LocalDateTime datumVrijeme) {
    this(ime, prezime, oznakaAranzmana, datumVrijeme, "PA", null, false);
  }

  // puni konstruktor
  public Rezervacija(String ime, String prezime, String oznakaAranzmana, LocalDateTime datumVrijeme,
      String vrsta, LocalDateTime datumVrijemeOtkaza, boolean aktivna) {
    this.ime = ime;
    this.prezime = prezime;
    this.oznakaAranzmana = oznakaAranzmana;
    this.datumVrijeme = datumVrijeme;
    this.vrsta = vrsta;
    this.datumVrijemeOtkaza = datumVrijemeOtkaza;
    this.aktivna = aktivna;
  }

  public String getIme() {
    return ime;
  }

  public String getPrezime() {
    return prezime;
  }

  public String getOznakaAranzmana() {
    return oznakaAranzmana;
  }

  public LocalDateTime getDatumVrijeme() {
    return datumVrijeme;
  }

  public String getVrsta() {
    return vrsta;
  }

  public LocalDateTime getDatumVrijemeOtkaza() {
    return datumVrijemeOtkaza;
  }

  public boolean isAktivna() {
    return aktivna;
  }

  public void setVrsta(String vrsta) {
    this.vrsta = vrsta;
  }

  public void setDatumVrijemeOtkaza(LocalDateTime dt) {
    this.datumVrijemeOtkaza = dt;
  }

  public void setAktivna(boolean aktivna) {
    this.aktivna = aktivna;
  }

  public void otkazi(LocalDateTime vrijemeOtkaza) {
    this.vrsta = "O";
    this.datumVrijemeOtkaza = vrijemeOtkaza;
  }
}
