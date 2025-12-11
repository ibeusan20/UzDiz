package edu.unizg.foi.uzdiz.ibeusan20.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Aranžman (Composite + State).
 */
public class Aranzman implements ElementRezervacijskeStrukture {

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

  /** Rezervacije ovog aranžmana (Composite). */
  private final List<Rezervacija> rezervacije = new ArrayList<>();

  /** Stanje aranžmana (State). */
  private StanjeAranzmana stanje = new UPripremiAranzman();

  /**
   * Konstruktor dostupan samo Builderu.
   *
   * @param builder graditelj
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

  // --- Composite operacije ---

  @Override
  public void dodaj(ElementRezervacijskeStrukture element) {
    if (element instanceof Rezervacija r) {
      rezervacije.add(r);
    }
  }

  @Override
  public void ukloni(ElementRezervacijskeStrukture element) {
    if (element instanceof Rezervacija r) {
      rezervacije.remove(r);
    }
  }

  @Override
  public List<Rezervacija> dohvatiSveRezervacije() {
    return new ArrayList<>(rezervacije);
  }

  /**
   * Dodaje rezervaciju ovom aranžmanu.
   *
   * @param r rezervacija
   */
  public void dodajRezervaciju(Rezervacija r) {
    if (r == null) {
      return;
    }
    rezervacije.add(r);
  }

  /**
   * Dohvaća rezervacije ovog aranžmana.
   *
   * @return lista rezervacija
   */
  public List<Rezervacija> getRezervacije() {
    return new ArrayList<>(rezervacije);
  }

  // --- State operacije ---

  public StanjeAranzmana getStanje() {
    return stanje;
  }

  public void postaviStanje(StanjeAranzmana novoStanje) {
    if (novoStanje == null) {
      return;
    }
    this.stanje = novoStanje;
  }

  // --- Getteri postojećeg modela (ostaju isti) ---

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

  @Override
  public String toString() {
    return "Aranzman{" + "oznaka='" + oznaka + '\'' + ", naziv='" + naziv + '\''
        + ", pocetniDatum=" + pocetniDatum + ", zavrsniDatum=" + zavrsniDatum + ", cijena="
        + cijena + ", minPutnika=" + minPutnika + ", maxPutnika=" + maxPutnika + '}';
  }
}
