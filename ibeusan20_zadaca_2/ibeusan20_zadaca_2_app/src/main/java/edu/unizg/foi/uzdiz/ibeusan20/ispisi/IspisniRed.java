package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

/**
 * Predstavlja jedan red tabličnog ispisa.
 * Adapteri za aranžmane i rezervacije ga implementiraju.
 */
public interface IspisniRed {

  /**
   * Vraća naslove stupaca za tablicu.
   */
  String[] zaglavlje();

  /**
   * Vraća vrijednosti stupaca za jedan red.
   */
  String[] vrijednosti();
}