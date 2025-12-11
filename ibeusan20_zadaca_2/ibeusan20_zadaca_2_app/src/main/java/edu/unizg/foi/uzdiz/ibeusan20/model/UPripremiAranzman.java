package edu.unizg.foi.uzdiz.ibeusan20.model;

/**
 * Stanje aranžmana "u pripremi".
 */
public class UPripremiAranzman implements StanjeAranzmana {

  @Override
  public String naziv() {
    return "u pripremi";
  }

  @Override
  public boolean jeAktivan() {
    return false;
  }

  @Override
  public boolean jeOtkazan() {
    return false;
  }
}
