package edu.unizg.foi.uzdiz.ibeusan20.model;

/**
 * Stanje "otkazana" rezervacija.
 */
public class OtkazanaRezervacija implements StanjeRezervacije {

  @Override
  public String naziv() {
    return "otkazana";
  }

  @Override
  public String oznaka() {
    return "O";
  }

  @Override
  public boolean jeAktivna() {
    return false;
  }

  @Override
  public boolean jeOtkazana() {
    return true;
  }
}
