package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

// TODO: Auto-generated Javadoc
/**
 * Predstavlja jedan red tabličnog ispisa.
 * Adapteri za aranžmane i rezervacije ga implementiraju.
 */
public interface IspisniRed {

  /**
   * Vraća naslove stupaca za tablicu.
   *
   * @return the string[]
   */
  String[] zaglavlje();

  /**
   * Vraća vrijednosti stupaca za jedan red.
   *
   * @return the string[]
   */
  String[] vrijednosti();
}