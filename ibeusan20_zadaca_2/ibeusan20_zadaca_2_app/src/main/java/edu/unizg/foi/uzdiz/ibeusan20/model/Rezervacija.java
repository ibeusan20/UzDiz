package edu.unizg.foi.uzdiz.ibeusan20.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Rezervacija turističkog aranžmana (State + Leaf u Compositeu).
 */
public class Rezervacija implements ElementRezervacijskeStrukture {

  private final String ime;
  private final String prezime;
  private final String oznakaAranzmana;
  private final LocalDateTime datumVrijeme;

  /** Stanje rezervacije (State). */
  private StanjeRezervacije stanje;

  private LocalDateTime datumVrijemeOtkaza;

  /**
   * Glavni konstruktor.
   *
   * @param ime ime osobe
   * @param prezime prezime osobe
   * @param oznakaAranzmana oznaka aranžmana
   * @param datumVrijeme datum i vrijeme rezervacije
   */
  public Rezervacija(String ime, String prezime, String oznakaAranzmana,
      LocalDateTime datumVrijeme) {
    this(ime, prezime, oznakaAranzmana, datumVrijeme, new NovaRezervacija(), null);
  }

  /**
   * Puni konstruktor.
   *
   * @param ime ime
   * @param prezime prezime
   * @param oznakaAranzmana oznaka aranžmana
   * @param datumVrijeme datum i vrijeme
   * @param stanje početno stanje
   * @param datumVrijemeOtkaza datum otkazivanja
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

  // --- Composite (leaf) ---

  @Override
  public void dodaj(ElementRezervacijskeStrukture element) {
    throw new UnsupportedOperationException("Rezervacija ne može imati djecu.");
  }

  @Override
  public void ukloni(ElementRezervacijskeStrukture element) {
    throw new UnsupportedOperationException("Rezervacija ne može imati djecu.");
  }

  @Override
  public List<Rezervacija> dohvatiSveRezervacije() {
    return Collections.singletonList(this);
  }

  // --- State ---

  public StanjeRezervacije getStanje() {
    return stanje;
  }

  /**
   * Postavlja novo stanje rezervacije.
   *
   * @param novo novo stanje
   */
  public void postaviStanje(StanjeRezervacije novo) {
    if (novo == null) {
      return;
    }
    this.stanje = novo;
  }

  /**
   * Otkazuje rezervaciju i postavlja stanje na "otkazana".
   *
   * @param vrijemeOtkaza datum i vrijeme otkazivanja
   */
  public void otkazi(LocalDateTime vrijemeOtkaza) {
    this.stanje = new OtkazanaRezervacija();
    this.datumVrijemeOtkaza = vrijemeOtkaza;
  }

  public boolean jeAktivna() {
    return stanje != null && stanje.jeAktivna();
  }

  public boolean jeOtkazana() {
    return stanje != null && stanje.jeOtkazana();
  }

  // --- Getteri za postojeći kod ---

  public String getIme() {
    return ime;
  }

  public String getPrezime() {
    return prezime;
  }

  public String getOznakaAranzmana() {
    return oznakaAranzmana;
  }

  public LocalDateTime getDatumVrijeme() {
    return datumVrijeme;
  }

  /**
   * Vraća oznaku stanja (N, P, A, Č, D, O) za potrebe starog koda.
   *
   * @return šifra stanja ili prazno ako nije definirano
   */
  public String getVrsta() {
    if (stanje == null) {
      return "";
    }
    return stanje.oznaka();
  }

  public LocalDateTime getDatumVrijemeOtkaza() {
    return datumVrijemeOtkaza;
  }
}
