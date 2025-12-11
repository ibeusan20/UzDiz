package edu.unizg.foi.uzdiz.ibeusan20.model;

/**
 * Sučelje za stanja aranžmana (State uzorak).
 */
public interface StanjeAranzmana {

  /**
   * Naziv stanja za ispis.
   *
   * @return naziv
   */
  String naziv();

  /**
   * Je li aranžman aktivan.
   *
   * @return true ako je aktivan
   */
  boolean jeAktivan();

  /**
   * Je li aranžman otkazan.
   *
   * @return true ako je otkazan
   */
  boolean jeOtkazan();
}
