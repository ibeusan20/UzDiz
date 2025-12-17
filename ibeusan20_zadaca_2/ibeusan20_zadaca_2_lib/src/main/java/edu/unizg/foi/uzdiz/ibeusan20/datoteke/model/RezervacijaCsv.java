package edu.unizg.foi.uzdiz.ibeusan20.datoteke.model;

import java.time.LocalDateTime;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.RezervacijaPodaci;

public class RezervacijaCsv implements RezervacijaPodaci {
  public String ime;
  public String prezime;
  public String oznakaAranzmana;
  public LocalDateTime datumVrijeme;

  @Override
  public String getIme() {
    return ime;
  }

  @Override
  public String getPrezime() {
    return prezime;
  }

  @Override
  public String getOznakaAranzmana() {
    return oznakaAranzmana;
  }

  @Override
  public LocalDateTime getDatumVrijeme() {
    return datumVrijeme;
  }
}
