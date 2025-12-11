package edu.unizg.foi.uzdiz.ibeusan20.model;

/**
 * Stanje aranžmana "otkazan".
 */
public class OtkazanAranzman implements StanjeAranzmana {

  @Override
  public String naziv() {
    return "otkazan";
  }

  @Override
  public boolean jeAktivan() {
    return false;
  }

  @Override
  public boolean jeOtkazan() {
    return true;
  }
}
