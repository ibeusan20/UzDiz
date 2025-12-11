package edu.unizg.foi.uzdiz.ibeusan20.model;

/**
 * Stanje "aktivna" rezervacija.
 */
public class AktivnaRezervacija implements StanjeRezervacije {

  @Override
  public String naziv() {
    return "aktivna";
  }

  @Override
  public String oznaka() {
    return "A";
  }

  @Override
  public boolean jeAktivna() {
    return true;
  }

  @Override
  public boolean jeOtkazana() {
    return false;
  }
}
