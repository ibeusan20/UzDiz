package edu.unizg.foi.uzdiz.ibeusan20.model;

import java.time.LocalDateTime;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOdgodenaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOtkazanaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjePrimljenaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeRezervacije;

// TODO: Auto-generated Javadoc
/**
 * Predstavlja rezervaciju turističkog aranžmana.
 * Koristi uzorak State za upravljanje statusima.
 */
public class Rezervacija {

  /** The ime. */
  private final String ime;
  
  /** The prezime. */
  private final String prezime;
  
  /** The oznaka aranzmana. */
  private final String oznakaAranzmana;
  
  /** The datum vrijeme. */
  private final LocalDateTime datumVrijeme;

  /** The datum vrijeme otkaza. */
  private LocalDateTime datumVrijemeOtkaza;
  
  /** The stanje. */
  private StanjeRezervacije stanje;

  /**
   * Instantiates a new rezervacija.
   *
   * @param ime the ime
   * @param prezime the prezime
   * @param oznakaAranzmana the oznaka aranzmana
   * @param datumVrijeme the datum vrijeme
   */
  public Rezervacija(String ime, String prezime, String oznakaAranzmana,
      LocalDateTime datumVrijeme) {
    this(ime, prezime, oznakaAranzmana, datumVrijeme,
        StanjePrimljenaRezervacija.instanca(), null);
  }

  /**
   * Instantiates a new rezervacija.
   *
   * @param ime the ime
   * @param prezime the prezime
   * @param oznakaAranzmana the oznaka aranzmana
   * @param datumVrijeme the datum vrijeme
   * @param stanje the stanje
   * @param datumVrijemeOtkaza the datum vrijeme otkaza
   */
  public Rezervacija(String ime, String prezime, String oznakaAranzmana,
      LocalDateTime datumVrijeme, StanjeRezervacije stanje,
      LocalDateTime datumVrijemeOtkaza) {
    this.ime = ime;
    this.prezime = prezime;
    this.oznakaAranzmana = oznakaAranzmana;
    this.datumVrijeme = datumVrijeme;
    this.stanje = stanje;
    this.datumVrijemeOtkaza = datumVrijemeOtkaza;
  }

  /**
   * Gets the ime.
   *
   * @return the ime
   */
  public String getIme() {
    return ime;
  }

  /**
   * Gets the prezime.
   *
   * @return the prezime
   */
  public String getPrezime() {
    return prezime;
  }

  /**
   * Gets the oznaka aranzmana.
   *
   * @return the oznaka aranzmana
   */
  public String getOznakaAranzmana() {
    return oznakaAranzmana;
  }

  /**
   * Gets the datum vrijeme.
   *
   * @return the datum vrijeme
   */
  public LocalDateTime getDatumVrijeme() {
    return datumVrijeme;
  }

  /**
   * Gets the datum vrijeme otkaza.
   *
   * @return the datum vrijeme otkaza
   */
  public LocalDateTime getDatumVrijemeOtkaza() {
    return datumVrijemeOtkaza;
  }

  /**
   * Gets the stanje.
   *
   * @return the stanje
   */
  public StanjeRezervacije getStanje() {
    return stanje;
  }

  /**
   * Postavi stanje.
   *
   * @param novoStanje the novo stanje
   */
  public void postaviStanje(StanjeRezervacije novoStanje) {
    if (novoStanje == null) {
      return;
    }
    this.stanje = novoStanje;
  }

  /**
   * Je aktivna.
   *
   * @return true, if successful
   */
  public boolean jeAktivna() {
    return stanje.jeAktivna();
  }

  /**
   * Broji se U kvotu.
   *
   * @return true, if successful
   */
  public boolean brojiSeUKvotu() {
    return stanje.brojiSeUKvotu();
  }

  /**
   * Naziv stanja.
   *
   * @return the string
   */
  public String nazivStanja() {
    return stanje.naziv();
  }

  /**
   * Otkazi.
   *
   * @param vrijemeOtkaza the vrijeme otkaza
   */
  public void otkazi(LocalDateTime vrijemeOtkaza) {
    if (!stanje.mozeOtkazati()) {
      return;
    }
    this.datumVrijemeOtkaza = vrijemeOtkaza;
    this.stanje = StanjeOtkazanaRezervacija.instanca();
  }
  
  /**
   * Odgodi.
   *
   * @param vrijemeOdgode the vrijeme odgode
   */
  public void odgodi(LocalDateTime vrijemeOdgode) {
    // koristimo isto polje datumVrijemeOtkaza i za odgodu,
    // jer se u ispisima IRTA/ITAS ionako traži "Datum i vrijeme otkaza/odgode"
    this.datumVrijemeOtkaza = vrijemeOdgode;
    this.stanje = StanjeOdgodenaRezervacija.instanca();
  }
}
