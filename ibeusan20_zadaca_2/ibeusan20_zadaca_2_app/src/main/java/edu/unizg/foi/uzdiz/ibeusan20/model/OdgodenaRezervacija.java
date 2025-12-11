package edu.unizg.foi.uzdiz.ibeusan20.model;

/**
 * Stanje "odgođena" rezervacija.
 */
public class OdgodenaRezervacija implements StanjeRezervacije {

  @Override
  public String naziv() {
    return "odgođena";
  }

  @Override
  public String oznaka() {
    return "D";
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
