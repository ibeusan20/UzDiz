package edu.unizg.foi.uzdiz.ibeusan20.datoteke.api;

import java.time.LocalDateTime;

// TODO: Auto-generated Javadoc
/**
 * The Interface RezervacijaPodaci.
 */
public interface RezervacijaPodaci {
  
  /**
   * Gets the ime.
   *
   * @return the ime
   */
  String getIme();
  
  /**
   * Gets the prezime.
   *
   * @return the prezime
   */
  String getPrezime();
  
  /**
   * Gets the oznaka aranzmana.
   *
   * @return the oznaka aranzmana
   */
  String getOznakaAranzmana();
  
  /**
   * Gets the datum vrijeme.
   *
   * @return the datum vrijeme
   */
  LocalDateTime getDatumVrijeme();
}
