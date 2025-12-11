package edu.unizg.foi.uzdiz.ibeusan20.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <b>Kreacijski uzorak:</b> Builder
 * <p>
 * Omogućuje stvaranje složenog objekta {@link Aranzman} uz validaciju svih obaveznih polja. Ako je
 * neko polje neispravno, baca se {@link IllegalArgumentException}.
 * </p>
 */
public class AranzmanBuilder {
  private String oznaka;
  private String naziv;
  private String program;
  private LocalDate pocetniDatum;
  private LocalDate zavrsniDatum;
  private LocalTime vrijemeKretanja;
  private LocalTime vrijemePovratka;
  private float cijena;
  private int minPutnika;
  private int maxPutnika;
  private int brojNocenja;
  private float doplataJednokrevetna;
  private List<String> prijevoz = new ArrayList<>();
  private int brojDorucaka;
  private int brojRuckova;
  private int brojVecera;

  // setteri

  /**
   * Postavi oznaku.
   *
   * @param oznaka the oznaka
   * @return the aranzman builder
   */
  public AranzmanBuilder postaviOznaku(String oznaka) {
    if (oznaka == null || oznaka.isBlank()) {
      throw new IllegalArgumentException("Oznaka aranžmana nije definirana.");
    }
    this.oznaka = oznaka.trim();
    return this;
  }

  /**
   * Postavi naziv.
   *
   * @param naziv the naziv
   * @return the aranzman builder
   */
  public AranzmanBuilder postaviNaziv(String naziv) {
    if (naziv == null || naziv.isBlank()) {
      throw new IllegalArgumentException("Naziv aranžmana nije definiran.");
    }
    this.naziv = naziv.trim();
    return this;
  }

  /**
   * Postavi program.
   *
   * @param program the program
   * @return the aranzman builder
   */
  public AranzmanBuilder postaviProgram(String program) {
    if (program == null || program.isBlank()) {
      throw new IllegalArgumentException("Program aranžmana nije definiran.");
    }
    this.program = program.trim();
    return this;
  }

  /**
   * Postavi pocetni datum.
   *
   * @param datum the datum
   * @return the aranzman builder
   */
  public AranzmanBuilder postaviPocetniDatum(LocalDate datum) {
    if (datum == null) {
      throw new IllegalArgumentException("Početni datum aranžmana nije definiran.");
    }
    this.pocetniDatum = datum;
    return this;
  }

  /**
   * Postavi zavrsni datum.
   *
   * @param datum the datum
   * @return the aranzman builder
   */
  public AranzmanBuilder postaviZavrsniDatum(LocalDate datum) {
    if (datum == null) {
      throw new IllegalArgumentException("Završni datum aranžmana nije definiran.");
    }
    this.zavrsniDatum = datum;
    return this;
  }

  /**
   * Postavi vrijeme kretanja.
   *
   * @param vrijeme the vrijeme
   * @return the aranzman builder
   */
  public AranzmanBuilder postaviVrijemeKretanja(LocalTime vrijeme) {
    this.vrijemeKretanja = vrijeme;
    return this;
  }

  /**
   * Postavi vrijeme povratka.
   *
   * @param vrijeme the vrijeme
   * @return the aranzman builder
   */
  public AranzmanBuilder postaviVrijemePovratka(LocalTime vrijeme) {
    this.vrijemePovratka = vrijeme;
    return this;
  }

  /**
   * Postavi cijenu.
   *
   * @param cijena the cijena
   * @return the aranzman builder
   */
  public AranzmanBuilder postaviCijenu(float cijena) {
    if (cijena <= 0) {
      throw new IllegalArgumentException("Cijena aranžmana mora biti veća od 0.");
    }
    this.cijena = cijena;
    return this;
  }

  /**
   * Postavi min putnika.
   *
   * @param min the min
   * @return the aranzman builder
   */
  public AranzmanBuilder postaviMinPutnika(int min) {
    if (min <= 0) {
      throw new IllegalArgumentException("Minimalan broj putnika mora biti veći od 0.");
    }
    this.minPutnika = min;
    return this;
  }

  /**
   * Postavi max putnika.
   *
   * @param max the max
   * @return the aranzman builder
   */
  public AranzmanBuilder postaviMaxPutnika(int max) {
    if (max <= 0) {
      throw new IllegalArgumentException("Maksimalan broj putnika mora biti veći od 0.");
    }
    this.maxPutnika = max;
    return this;
  }

  /**
   * Postavi broj nocenja.
   *
   * @param broj the broj
   * @return the aranzman builder
   */
  public AranzmanBuilder postaviBrojNocenja(int broj) {
    this.brojNocenja = broj;
    return this;
  }

  /**
   * Postavi doplatu jednokrevetna.
   *
   * @param doplata the doplata
   * @return the aranzman builder
   */
  public AranzmanBuilder postaviDoplatuJednokrevetna(float doplata) {
    this.doplataJednokrevetna = doplata;
    return this;
  }

  /**
   * Postavi prijevoz.
   *
   * @param tekst the tekst
   * @return the aranzman builder
   */
  public AranzmanBuilder postaviPrijevoz(String tekst) {
    if (tekst != null && !tekst.isBlank()) {
      this.prijevoz = Arrays.asList(tekst.split(";"));
    }
    return this;
  }

  /**
   * Postavi broj dorucaka.
   *
   * @param broj the broj
   * @return the aranzman builder
   */
  public AranzmanBuilder postaviBrojDorucaka(int broj) {
    this.brojDorucaka = broj;
    return this;
  }

  /**
   * Postavi broj ruckova.
   *
   * @param broj the broj
   * @return the aranzman builder
   */
  public AranzmanBuilder postaviBrojRuckova(int broj) {
    this.brojRuckova = broj;
    return this;
  }

  /**
   * Postavi broj vecera.
   *
   * @param broj the broj
   * @return the aranzman builder
   */
  public AranzmanBuilder postaviBrojVecera(int broj) {
    this.brojVecera = broj;
    return this;
  }

  // ------------------------------------------------------------
  // Izgradnja objekta
  /**
   * Izgrađuje objekt {@link Aranzman} uz validaciju svih logičkih pravila.
   *
   * @throws IllegalArgumentException ako bilo koje polje nije ispravno
   */
  public Aranzman izgradi() {
    if (oznaka == null || naziv == null || program == null || pocetniDatum == null
        || zavrsniDatum == null || cijena <= 0 || minPutnika <= 0 || maxPutnika <= 0) {
      throw new IllegalArgumentException("Nisu popunjeni svi obavezni atributi aranžmana.");
    }
    if (zavrsniDatum.isBefore(pocetniDatum)) {
      throw new IllegalArgumentException("Završni datum ne može biti prije početnog datuma.");
    }
    if (minPutnika > maxPutnika) {
      throw new IllegalArgumentException(
          "Minimalan broj putnika ne može biti veći od maksimalnog.");
    }
    if (vrijemeKretanja == null && vrijemePovratka != null) {
      throw new IllegalArgumentException(
          "Definirano je vrijeme povratka, ali ne i vrijeme kretanja.");
    }

    if (vrijemeKretanja != null && vrijemePovratka != null) {
      if (pocetniDatum != null && zavrsniDatum != null && pocetniDatum.equals(zavrsniDatum)) {
        if (vrijemePovratka.isBefore(vrijemeKretanja)) {
          throw new IllegalArgumentException(
              "Vrijeme povratka ne može biti prije vremena kretanja (isti dan).");
        }
      }
    }

    // Ako sve prolazi, kreiraj objekt
    return new Aranzman(this);
  }

  // ------------------------------------------------------------
  // Getteri za Aranzman
  /**
   * Gets the oznaka.
   *
   * @return the oznaka
   */
  // ------------------------------------------------------------
  public String getOznaka() {
    return oznaka;
  }

  /**
   * Gets the naziv.
   *
   * @return the naziv
   */
  public String getNaziv() {
    return naziv;
  }

  /**
   * Gets the program.
   *
   * @return the program
   */
  public String getProgram() {
    return program;
  }

  /**
   * Gets the pocetni datum.
   *
   * @return the pocetni datum
   */
  public LocalDate getPocetniDatum() {
    return pocetniDatum;
  }

  /**
   * Gets the zavrsni datum.
   *
   * @return the zavrsni datum
   */
  public LocalDate getZavrsniDatum() {
    return zavrsniDatum;
  }

  /**
   * Gets the vrijeme kretanja.
   *
   * @return the vrijeme kretanja
   */
  public LocalTime getVrijemeKretanja() {
    return vrijemeKretanja;
  }

  /**
   * Gets the vrijeme povratka.
   *
   * @return the vrijeme povratka
   */
  public LocalTime getVrijemePovratka() {
    return vrijemePovratka;
  }

  /**
   * Gets the cijena.
   *
   * @return the cijena
   */
  public float getCijena() {
    return cijena;
  }

  /**
   * Gets the min putnika.
   *
   * @return the min putnika
   */
  public int getMinPutnika() {
    return minPutnika;
  }

  /**
   * Gets the max putnika.
   *
   * @return the max putnika
   */
  public int getMaxPutnika() {
    return maxPutnika;
  }

  /**
   * Gets the broj nocenja.
   *
   * @return the broj nocenja
   */
  public int getBrojNocenja() {
    return brojNocenja;
  }

  /**
   * Gets the doplata jednokrevetna.
   *
   * @return the doplata jednokrevetna
   */
  public float getDoplataJednokrevetna() {
    return doplataJednokrevetna;
  }

  /**
   * Gets the prijevoz.
   *
   * @return the prijevoz
   */
  public List<String> getPrijevoz() {
    return prijevoz;
  }

  /**
   * Gets the broj dorucaka.
   *
   * @return the broj dorucaka
   */
  public int getBrojDorucaka() {
    return brojDorucaka;
  }

  /**
   * Gets the broj ruckova.
   *
   * @return the broj ruckova
   */
  public int getBrojRuckova() {
    return brojRuckova;
  }

  /**
   * Gets the broj vecera.
   *
   * @return the broj vecera
   */
  public int getBrojVecera() {
    return brojVecera;
  }
}
