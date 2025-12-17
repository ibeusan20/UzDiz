package edu.unizg.foi.uzdiz.ibeusan20.datoteke.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Interface AranzmanPodaci.
 */
public interface AranzmanPodaci {
  
  /**
   * Gets the oznaka.
   *
   * @return the oznaka
   */
  String getOznaka();
  
  /**
   * Gets the naziv.
   *
   * @return the naziv
   */
  String getNaziv();
  
  /**
   * Gets the program.
   *
   * @return the program
   */
  String getProgram();

  /**
   * Gets the pocetni datum.
   *
   * @return the pocetni datum
   */
  LocalDate getPocetniDatum();
  
  /**
   * Gets the zavrsni datum.
   *
   * @return the zavrsni datum
   */
  LocalDate getZavrsniDatum();
  
  /**
   * Gets the vrijeme kretanja.
   *
   * @return the vrijeme kretanja
   */
  LocalTime getVrijemeKretanja();
  
  /**
   * Gets the vrijeme povratka.
   *
   * @return the vrijeme povratka
   */
  LocalTime getVrijemePovratka();

  /**
   * Gets the cijena.
   *
   * @return the cijena
   */
  float getCijena();
  
  /**
   * Gets the min putnika.
   *
   * @return the min putnika
   */
  int getMinPutnika();
  
  /**
   * Gets the max putnika.
   *
   * @return the max putnika
   */
  int getMaxPutnika();

  /**
   * Gets the broj nocenja.
   *
   * @return the broj nocenja
   */
  int getBrojNocenja();
  
  /**
   * Gets the doplata jednokrevetna.
   *
   * @return the doplata jednokrevetna
   */
  float getDoplataJednokrevetna();

  /**
   * Gets the prijevoz.
   *
   * @return the prijevoz
   */
  List<String> getPrijevoz();

  /**
   * Gets the broj dorucaka.
   *
   * @return the broj dorucaka
   */
  int getBrojDorucaka();
  
  /**
   * Gets the broj ruckova.
   *
   * @return the broj ruckova
   */
  int getBrojRuckova();
  
  /**
   * Gets the broj vecera.
   *
   * @return the broj vecera
   */
  int getBrojVecera();
}
