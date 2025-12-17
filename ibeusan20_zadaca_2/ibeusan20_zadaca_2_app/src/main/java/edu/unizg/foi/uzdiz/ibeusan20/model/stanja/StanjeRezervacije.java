package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

// TODO: Auto-generated Javadoc
/**
 * The Interface StanjeRezervacije.
 */
public interface StanjeRezervacije {

  /**
   * Tekstualni naziv stanja (npr. "nova", "primljena", ...).
   *
   * @return the string
   */
  String naziv();

  /**
   * Označava broji li se rezervacija u kvotu prijava
   * (za min / max broj putnika).
   *
   * @return true, if successful
   */
  boolean brojiSeUKvotu();

  /**
   * Označava je li rezervacija aktivna
   * (osoba stvarno putuje na aranžman).
   *
   * @return true, if successful
   */
  boolean jeAktivna();

  /**
   * Smije li se rezervacija otkazati u ovom stanju.
   *
   * @return true, if successful
   */
  default boolean mozeOtkazati() {
    return !jeAktivna() || brojiSeUKvotu();
  }
}
