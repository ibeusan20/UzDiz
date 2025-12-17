package edu.unizg.foi.uzdiz.ibeusan20.model;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Zajedničko sučelje za Composite uzorak.
 * Aranžman je kompozit, Rezervacija je list.
 */
public interface ElementRezervacijskeStrukture {

  /**
   * Dodaje podređeni element.
   *
   * @param element element koji se dodaje
   */
  void dodaj(ElementRezervacijskeStrukture element);

  /**
   * Uklanja podređeni element.
   *
   * @param element element koji se uklanja
   */
  void ukloni(ElementRezervacijskeStrukture element);

  /**
   * Dohvaća sve rezervacije u podstablu.
   *
   * @return lista rezervacija
   */
  List<Rezervacija> dohvatiSveRezervacije();
}
