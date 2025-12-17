package edu.unizg.foi.uzdiz.ibeusan20.datoteke.model;

import java.time.LocalDateTime;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.RezervacijaPodaci;

// TODO: Auto-generated Javadoc
/**
 * The Class RezervacijaCsv.
 */
public class RezervacijaCsv implements RezervacijaPodaci {
  
  /** The ime. */
  public String ime;
  
  /** The prezime. */
  public String prezime;
  
  /** The oznaka aranzmana. */
  public String oznakaAranzmana;
  
  /** The datum vrijeme. */
  public LocalDateTime datumVrijeme;

  /**
   * Gets the ime.
   *
   * @return the ime
   */
  @Override public String getIme() { return ime; }
  
  /**
   * Gets the prezime.
   *
   * @return the prezime
   */
  @Override public String getPrezime() { return prezime; }
  
  /**
   * Gets the oznaka aranzmana.
   *
   * @return the oznaka aranzmana
   */
  @Override public String getOznakaAranzmana() { return oznakaAranzmana; }
  
  /**
   * Gets the datum vrijeme.
   *
   * @return the datum vrijeme
   */
  @Override public LocalDateTime getDatumVrijeme() { return datumVrijeme; }
}
