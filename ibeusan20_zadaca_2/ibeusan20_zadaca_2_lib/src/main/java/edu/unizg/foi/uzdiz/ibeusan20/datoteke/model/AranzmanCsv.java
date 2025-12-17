package edu.unizg.foi.uzdiz.ibeusan20.datoteke.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.AranzmanPodaci;

// TODO: Auto-generated Javadoc
/**
 * The Class AranzmanCsv.
 */
public class AranzmanCsv implements AranzmanPodaci {
  
  /** The oznaka. */
  public String oznaka;
  
  /** The naziv. */
  public String naziv;
  
  /** The program. */
  public String program;
  
  /** The pocetni datum. */
  public LocalDate pocetniDatum;
  
  /** The zavrsni datum. */
  public LocalDate zavrsniDatum;
  
  /** The vrijeme kretanja. */
  public LocalTime vrijemeKretanja;
  
  /** The vrijeme povratka. */
  public LocalTime vrijemePovratka;
  
  /** The cijena. */
  public float cijena;
  
  /** The min putnika. */
  public int minPutnika;
  
  /** The max putnika. */
  public int maxPutnika;
  
  /** The broj nocenja. */
  public int brojNocenja;
  
  /** The doplata jednokrevetna. */
  public float doplataJednokrevetna;
  
  /** The prijevoz. */
  public List<String> prijevoz;
  
  /** The broj dorucaka. */
  public int brojDorucaka;
  
  /** The broj ruckova. */
  public int brojRuckova;
  
  /** The broj vecera. */
  public int brojVecera;

  /**
   * Gets the oznaka.
   *
   * @return the oznaka
   */
  @Override public String getOznaka() { return oznaka; }
  
  /**
   * Gets the naziv.
   *
   * @return the naziv
   */
  @Override public String getNaziv() { return naziv; }
  
  /**
   * Gets the program.
   *
   * @return the program
   */
  @Override public String getProgram() { return program; }
  
  /**
   * Gets the pocetni datum.
   *
   * @return the pocetni datum
   */
  @Override public LocalDate getPocetniDatum() { return pocetniDatum; }
  
  /**
   * Gets the zavrsni datum.
   *
   * @return the zavrsni datum
   */
  @Override public LocalDate getZavrsniDatum() { return zavrsniDatum; }
  
  /**
   * Gets the vrijeme kretanja.
   *
   * @return the vrijeme kretanja
   */
  @Override public LocalTime getVrijemeKretanja() { return vrijemeKretanja; }
  
  /**
   * Gets the vrijeme povratka.
   *
   * @return the vrijeme povratka
   */
  @Override public LocalTime getVrijemePovratka() { return vrijemePovratka; }
  
  /**
   * Gets the cijena.
   *
   * @return the cijena
   */
  @Override public float getCijena() { return cijena; }
  
  /**
   * Gets the min putnika.
   *
   * @return the min putnika
   */
  @Override public int getMinPutnika() { return minPutnika; }
  
  /**
   * Gets the max putnika.
   *
   * @return the max putnika
   */
  @Override public int getMaxPutnika() { return maxPutnika; }
  
  /**
   * Gets the broj nocenja.
   *
   * @return the broj nocenja
   */
  @Override public int getBrojNocenja() { return brojNocenja; }
  
  /**
   * Gets the doplata jednokrevetna.
   *
   * @return the doplata jednokrevetna
   */
  @Override public float getDoplataJednokrevetna() { return doplataJednokrevetna; }
  
  /**
   * Gets the prijevoz.
   *
   * @return the prijevoz
   */
  @Override public List<String> getPrijevoz() { return prijevoz; }
  
  /**
   * Gets the broj dorucaka.
   *
   * @return the broj dorucaka
   */
  @Override public int getBrojDorucaka() { return brojDorucaka; }
  
  /**
   * Gets the broj ruckova.
   *
   * @return the broj ruckova
   */
  @Override public int getBrojRuckova() { return brojRuckova; }
  
  /**
   * Gets the broj vecera.
   *
   * @return the broj vecera
   */
  @Override public int getBrojVecera() { return brojVecera; }
}
