package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

// TODO: Auto-generated Javadoc
/**
 * Bridge sučelje za različite formate ispisa (tablični, csv, ...).
 */
public interface FormatIspisaBridge {

  /**
   * Ispisuje jedan red podataka u određenom formatu.
   *
   * @param tekst the tekst
   */
  void ispisi(String tekst);
}
