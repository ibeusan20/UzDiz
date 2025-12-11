package edu.unizg.foi.uzdiz.ibeusan20.model;

/**
 * Stanje "na čekanju" rezervacija.
 */
public class NaCekanjuRezervacija implements StanjeRezervacije {

  @Override
  public String naziv() {
    return "na čekanju";
  }

  @Override
  public String oznaka() {
    return "Č";
  }

  @Override
  public boolean jeAktivna() {
    return false;
  }

  @Override
  public boolean jeOtkazana() {
    return false;
  }
}
