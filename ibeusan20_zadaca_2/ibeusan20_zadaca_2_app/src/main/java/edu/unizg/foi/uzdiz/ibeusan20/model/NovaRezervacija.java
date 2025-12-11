package edu.unizg.foi.uzdiz.ibeusan20.model;

/**
 * Stanje "nova" rezervacija.
 */
public class NovaRezervacija implements StanjeRezervacije {

  @Override
  public String naziv() {
    return "nova";
  }

  @Override
  public String oznaka() {
    return "N";
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
