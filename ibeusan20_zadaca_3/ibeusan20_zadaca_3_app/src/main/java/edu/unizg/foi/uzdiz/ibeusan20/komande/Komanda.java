package edu.unizg.foi.uzdiz.ibeusan20.komande;

/**
 * Osnovno sučelje za sve konzolne komande aplikacije.
 * <p>
 * Svaka komanda mora implementirati izvršavanje i vratiti informaciju treba li aplikacija nastaviti
 * s radom ili završiti.
 * </p>
 */
public interface Komanda {

  /**
   * Izvršava komandu.
   *
   * @return {@code true} ako se program nastavlja, {@code false} ako treba završiti rad
   */
  boolean izvrsi();
}
