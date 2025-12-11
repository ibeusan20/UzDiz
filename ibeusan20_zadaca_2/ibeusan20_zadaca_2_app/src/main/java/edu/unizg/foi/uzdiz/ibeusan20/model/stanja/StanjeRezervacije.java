package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

public interface StanjeRezervacije {

  /**
   * Tekstualni naziv stanja (npr. "nova", "primljena", ...).
   */
  String naziv();

  /**
   * Označava broji li se rezervacija u kvotu prijava
   * (za min / max broj putnika).
   */
  boolean brojiSeUKvotu();

  /**
   * Označava je li rezervacija aktivna
   * (osoba stvarno putuje na aranžman).
   */
  boolean jeAktivna();

  /**
   * Smije li se rezervacija otkazati u ovom stanju.
   */
  default boolean mozeOtkazati() {
    return !jeAktivna() || brojiSeUKvotu();
  }
}
