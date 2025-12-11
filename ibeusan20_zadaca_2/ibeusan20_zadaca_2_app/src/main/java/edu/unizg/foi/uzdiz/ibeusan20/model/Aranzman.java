package edu.unizg.foi.uzdiz.ibeusan20.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Klasa Aranzman predstavlja turistički aranžman.
 *
 * Kreira se pomoću kreacijskog uzorka Builder (klasa {@link AranzmanBuilder}).
 *
 * Osnovni atributi (oznaka, naziv, datumi, cijena, ...) su nepromjenjivi,
 * ali aranžman kao "kompozit" sadrži svoje rezervacije.
 */
public class Aranzman {

  private final String oznaka;
  private final String naziv;
  private final String program;
  private final LocalDate pocetniDatum;
  private final LocalDate zavrsniDatum;
  private final LocalTime vrijemeKretanja;
  private final LocalTime vrijemePovratka;
  private final float cijena;
  private final int minPutnika;
  private final int maxPutnika;
  private final int brojNocenja;
  private final float doplataJednokrevetna;
  private final List<String> prijevoz;
  private final int brojDorucaka;
  private final int brojRuckova;
  private final int brojVecera;

  /** Rezervacije koje pripadaju ovom aranžmanu (Composite). */
  private final List<Rezervacija> rezervacije = new ArrayList<>();

  /**
   * Konstruktor dostupan samo Builderu.
   */
  protected Aranzman(AranzmanBuilder builder) {
    this.oznaka = builder.getOznaka();
    this.naziv = builder.getNaziv();
    this.program = builder.getProgram();
    this.pocetniDatum = builder.getPocetniDatum();
    this.zavrsniDatum = builder.getZavrsniDatum();
    this.vrijemeKretanja = builder.getVrijemeKretanja();
    this.vrijemePovratka = builder.getVrijemePovratka();
    this.cijena = builder.getCijena();
    this.minPutnika = builder.getMinPutnika();
    this.maxPutnika = builder.getMaxPutnika();
    this.brojNocenja = builder.getBrojNocenja();
    this.doplataJednokrevetna = builder.getDoplataJednokrevetna();
    this.prijevoz = builder.getPrijevoz();
    this.brojDorucaka = builder.getBrojDorucaka();
    this.brojRuckova = builder.getBrojRuckova();
    this.brojVecera = builder.getBrojVecera();
  }

  // ------------------- osnovni getteri -------------------

  public String getOznaka() {
    return oznaka;
  }

  public String getNaziv() {
    return naziv;
  }

  public String getProgram() {
    return program;
  }

  public LocalDate getPocetniDatum() {
    return pocetniDatum;
  }

  public LocalDate getZavrsniDatum() {
    return zavrsniDatum;
  }

  public LocalTime getVrijemeKretanja() {
    return vrijemeKretanja;
  }

  public LocalTime getVrijemePovratka() {
    return vrijemePovratka;
  }

  public float getCijena() {
    return cijena;
  }

  public int getMinPutnika() {
    return minPutnika;
  }

  public int getMaxPutnika() {
    return maxPutnika;
  }

  public int getBrojNocenja() {
    return brojNocenja;
  }

  public float getDoplataJednokrevetna() {
    return doplataJednokrevetna;
  }

  public List<String> getPrijevoz() {
    return prijevoz;
  }

  public int getBrojDorucaka() {
    return brojDorucaka;
  }

  public int getBrojRuckova() {
    return brojRuckova;
  }

  public int getBrojVecera() {
    return brojVecera;
  }

  // ------------------- Composite dio -------------------

  /**
   * Dodaje rezervaciju u ovaj aranžman.
   *
   * @param rezervacija rezervacija koja se dodaje
   */
  public void dodajRezervaciju(Rezervacija rezervacija) {
    if (rezervacija != null) {
      rezervacije.add(rezervacija);
    }
  }

  /**
   * Uklanja rezervaciju iz ovog aranžmana.
   *
   * @param rezervacija rezervacija koja se uklanja
   */
  public void ukloniRezervaciju(Rezervacija rezervacija) {
    if (rezervacija != null) {
      rezervacije.remove(rezervacija);
    }
  }

  /**
   * Dohvaća sve rezervacije ovog aranžmana.
   *
   * @return nova lista rezervacija (kopija interne kolekcije)
   */
  public List<Rezervacija> getRezervacije() {
    return Collections.unmodifiableList(new ArrayList<>(rezervacije));
  }

  @Override
  public String toString() {
    return "Aranzman{" + "oznaka='" + oznaka + '\'' + ", naziv='" + naziv + '\''
        + ", pocetniDatum=" + pocetniDatum + ", zavrsniDatum=" + zavrsniDatum + ", cijena="
        + cijena + ", minPutnika=" + minPutnika + ", maxPutnika=" + maxPutnika + '}';
  }
}
