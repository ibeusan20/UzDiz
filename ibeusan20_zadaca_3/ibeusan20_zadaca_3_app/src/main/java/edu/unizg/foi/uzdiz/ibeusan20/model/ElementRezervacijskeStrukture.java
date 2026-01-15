package edu.unizg.foi.uzdiz.ibeusan20.model;

import java.util.List;

/**
 * Zajedničko sučelje za elemente rezervacijske strukture.
 * <p>
 * Predviđeno za primjenu uzorka <b>Composite</b>:
 * </p>
 * <ul>
 * <li>kompozit (npr. aranžman) može sadržavati podređene elemente</li>
 * <li>list (npr. rezervacija) predstavlja krajnji element</li>
 * </ul>
 *
 * <p>
 * Sučelje definira operacije dodavanja i uklanjanja podređenih elemenata i dohvat svih rezervacija
 * iz podstabla.
 * </p>
 */
public interface ElementRezervacijskeStrukture {

  /**
   * Dodaje podređeni element u strukturu.
   *
   * @param element element koji se dodaje
   */
  void dodaj(ElementRezervacijskeStrukture element);

  /**
   * Uklanja podređeni element iz strukture.
   *
   * @param element element koji se uklanja
   */
  void ukloni(ElementRezervacijskeStrukture element);

  /**
   * Dohvaća sve rezervacije koje se nalaze u podstablu trenutnog elementa.
   *
   * @return lista rezervacija
   */
  List<Rezervacija> dohvatiSveRezervacije();
}
