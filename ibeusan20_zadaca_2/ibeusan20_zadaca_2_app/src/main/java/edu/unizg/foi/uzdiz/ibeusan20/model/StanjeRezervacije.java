package edu.unizg.foi.uzdiz.ibeusan20.model;

/**
 * Sučelje za stanja rezervacije (State uzorak).
 */
public interface StanjeRezervacije {

  /**
   * Naziv stanja za ispis (npr. "nova", "aktivna").
   *
   * @return naziv stanja
   */
  String naziv();

  /**
   * Oznaka stanja za internu upotrebu (npr. N, P, A, Č, D, O).
   *
   * @return šifra stanja
   */
  String oznaka();

  /**
   * Je li rezervacija aktivna u smislu zadatka.
   *
   * @return true ako je aktivna
   */
  boolean jeAktivna();

  /**
   * Je li rezervacija otkazana.
   *
   * @return true ako je otkazana
   */
  boolean jeOtkazana();
}
