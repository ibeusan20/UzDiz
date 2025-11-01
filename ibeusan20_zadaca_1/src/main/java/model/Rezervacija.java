package model;

import java.time.LocalDateTime;

public class Rezervacija {

  private final String ime;
  private final String prezime;
  private final String oznakaAranzmana;
  private final LocalDateTime datumVrijeme;
  private final String vrsta;

  public Rezervacija(String ime, String prezime, String oznakaAranzmana,
      LocalDateTime datumVrijeme) {
    this(ime, prezime, oznakaAranzmana, datumVrijeme, "PA");
  }

  public Rezervacija(String ime, String prezime, String oznakaAranzmana, LocalDateTime datumVrijeme,
      String vrsta) {
    this.ime = ime;
    this.prezime = prezime;
    this.oznakaAranzmana = oznakaAranzmana;
    this.datumVrijeme = datumVrijeme;
    this.vrsta = vrsta;
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
}
