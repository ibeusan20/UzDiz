package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

/**
 * Bridge sučelje za različite formate ispisa (tablični, csv, ...).
 */
public interface FormatIspisaBridge {

  /**
   * Ispisuje jedan red podataka u određenom formatu.
   */
  void ispisi(IspisniRed red);
}
