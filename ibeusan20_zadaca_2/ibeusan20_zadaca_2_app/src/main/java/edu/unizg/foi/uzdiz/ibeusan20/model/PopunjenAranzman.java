package edu.unizg.foi.uzdiz.ibeusan20.model;

/**
 * Stanje aranžmana "popunjen".
 */
public class PopunjenAranzman implements StanjeAranzmana {

  @Override
  public String naziv() {
    return "popunjen";
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
