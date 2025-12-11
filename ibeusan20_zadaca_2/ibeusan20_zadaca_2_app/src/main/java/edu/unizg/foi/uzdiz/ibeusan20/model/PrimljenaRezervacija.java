package edu.unizg.foi.uzdiz.ibeusan20.model;

/**
 * Stanje "primljena" rezervacija.
 */
public class PrimljenaRezervacija implements StanjeRezervacije {

  @Override
  public String naziv() {
    return "primljena";
  }

  @Override
  public String oznaka() {
    return "P";
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

