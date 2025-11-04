package model;

import java.time.LocalDateTime;

/**
 * Predstavlja rezervaciju turističkog aranžmana.
 * <p>
 * Status može biti:
 * <ul>
 * <li><b>PA</b> – Primljena/aktivna rezervacija</li>
 * <li><b>Č</b> – Na čekanju</li>
 * <li><b>O</b> – Otkazana</li>
 * </ul>
 * </p>
 */
public class Rezervacija {
  private final String ime;
  private final String prezime;
  private final String oznakaAranzmana;
  private final LocalDateTime datumVrijeme;
  private String vrsta; // "PA", "Č", "O"
  private LocalDateTime datumVrijemeOtkaza;

  private boolean aktivna;

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
    this(ime, prezime, oznakaAranzmana, datumVrijeme, "PA", null, false);
  }

  /**
   * Instantiates a new rezervacija.
   *
   * @param ime the ime
   * @param prezime the prezime
   * @param oznakaAranzmana the oznaka aranzmana
   * @param datumVrijeme the datum vrijeme
   * @param vrsta the vrsta
   * @param datumVrijemeOtkaza the datum vrijeme otkaza
   * @param aktivna the aktivna
   */
  // puni konstruktor
  public Rezervacija(String ime, String prezime, String oznakaAranzmana, LocalDateTime datumVrijeme,
      String vrsta, LocalDateTime datumVrijemeOtkaza, boolean aktivna) {
    this.ime = ime;
    this.prezime = prezime;
    this.oznakaAranzmana = oznakaAranzmana;
    this.datumVrijeme = datumVrijeme;
    this.vrsta = vrsta;
    this.datumVrijemeOtkaza = datumVrijemeOtkaza;
    this.aktivna = aktivna;
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
   * Gets the vrsta.
   *
   * @return the vrsta
   */
  public String getVrsta() {
    return vrsta;
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
   * Checks if is aktivna.
   *
   * @return true, if is aktivna
   */
  public boolean isAktivna() {
    return aktivna;
  }

  /**
   * Sets the vrsta.
   *
   * @param vrsta the new vrsta
   */
  public void setVrsta(String vrsta) {
    this.vrsta = vrsta;
  }

  /**
   * Sets the datum vrijeme otkaza.
   *
   * @param dt the new datum vrijeme otkaza
   */
  public void setDatumVrijemeOtkaza(LocalDateTime dt) {
    this.datumVrijemeOtkaza = dt;
  }

  /**
   * Sets the aktivna.
   *
   * @param aktivna the new aktivna
   */
  public void setAktivna(boolean aktivna) {
    this.aktivna = aktivna;
  }

  /**
   * Otkazi.
   *
   * @param vrijemeOtkaza the vrijeme otkaza
   */
  public void otkazi(LocalDateTime vrijemeOtkaza) {
    this.vrsta = "O";
    this.datumVrijemeOtkaza = vrijemeOtkaza;
  }
}
