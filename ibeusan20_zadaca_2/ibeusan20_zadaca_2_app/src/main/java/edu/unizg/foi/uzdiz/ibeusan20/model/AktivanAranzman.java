package edu.unizg.foi.uzdiz.ibeusan20.model;

/**
 * Stanje aranžmana "aktivan".
 */
public class AktivanAranzman implements StanjeAranzmana {

  @Override
  public String naziv() {
    return "aktivan";
  }

  @Override
  public boolean jeAktivan() {
    return true;
  }

  @Override
  public boolean jeOtkazan() {
    return false;
  }
}
