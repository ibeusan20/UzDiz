package model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Kreacijski uzorak: Builder.
 * 
 * Služi za stvaranje složenih objekata Aranzman uz provjeru obaveznih polja. Ako bilo koji od
 * obaveznih atributa nije ispravan, baca se iznimka.
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

  // ------------------------------------------------------------
  // Postavljači (setter metode)
  // ------------------------------------------------------------

  public AranzmanBuilder postaviOznaku(String oznaka) {
    if (oznaka == null || oznaka.isBlank()) {
      throw new IllegalArgumentException("Oznaka aranžmana nije definirana.");
    }
    this.oznaka = oznaka.trim();
    return this;
  }

  public AranzmanBuilder postaviNaziv(String naziv) {
    if (naziv == null || naziv.isBlank()) {
      throw new IllegalArgumentException("Naziv aranžmana nije definiran.");
    }
    this.naziv = naziv.trim();
    return this;
  }

  public AranzmanBuilder postaviProgram(String program) {
    if (program == null || program.isBlank()) {
      throw new IllegalArgumentException("Program aranžmana nije definiran.");
    }
    this.program = program.trim();
    return this;
  }

  public AranzmanBuilder postaviPocetniDatum(LocalDate datum) {
    if (datum == null) {
      throw new IllegalArgumentException("Početni datum aranžmana nije definiran.");
    }
    this.pocetniDatum = datum;
    return this;
  }

  public AranzmanBuilder postaviZavrsniDatum(LocalDate datum) {
    if (datum == null) {
      throw new IllegalArgumentException("Završni datum aranžmana nije definiran.");
    }
    this.zavrsniDatum = datum;
    return this;
  }

  public AranzmanBuilder postaviVrijemeKretanja(LocalTime vrijeme) {
    // vrijeme nije obavezno
    this.vrijemeKretanja = vrijeme;
    return this;
  }

  public AranzmanBuilder postaviVrijemePovratka(LocalTime vrijeme) {
    // vrijeme nije obavezno
    this.vrijemePovratka = vrijeme;
    return this;
  }

  public AranzmanBuilder postaviCijenu(float cijena) {
    if (cijena <= 0) {
      throw new IllegalArgumentException("Cijena aranžmana mora biti veća od 0.");
    }
    this.cijena = cijena;
    return this;
  }

  public AranzmanBuilder postaviMinPutnika(int min) {
    if (min <= 0) {
      throw new IllegalArgumentException("Minimalan broj putnika mora biti veći od 0.");
    }
    this.minPutnika = min;
    return this;
  }

  public AranzmanBuilder postaviMaxPutnika(int max) {
    if (max <= 0) {
      throw new IllegalArgumentException("Maksimalan broj putnika mora biti veći od 0.");
    }
    this.maxPutnika = max;
    return this;
  }

  public AranzmanBuilder postaviBrojNocenja(int broj) {
    this.brojNocenja = broj;
    return this;
  }

  public AranzmanBuilder postaviDoplatuJednokrevetna(float doplata) {
    this.doplataJednokrevetna = doplata;
    return this;
  }

  public AranzmanBuilder postaviPrijevoz(String tekst) {
    if (tekst != null && !tekst.isBlank()) {
      this.prijevoz = Arrays.asList(tekst.split(";"));
    }
    return this;
  }

  public AranzmanBuilder postaviBrojDorucaka(int broj) {
    this.brojDorucaka = broj;
    return this;
  }

  public AranzmanBuilder postaviBrojRuckova(int broj) {
    this.brojRuckova = broj;
    return this;
  }

  public AranzmanBuilder postaviBrojVecera(int broj) {
    this.brojVecera = broj;
    return this;
  }

  // ------------------------------------------------------------
  // Izgradnja objekta
  // ------------------------------------------------------------
  public Aranzman izgradi() {
    // 1) Provjera obaveznih atributa
    if (oznaka == null || naziv == null || program == null || pocetniDatum == null
        || zavrsniDatum == null || cijena <= 0 || minPutnika <= 0 || maxPutnika <= 0) {
      throw new IllegalArgumentException("Nisu popunjeni svi obavezni atributi aranžmana.");
    }

    // 2) Logička provjera datuma
    if (zavrsniDatum.isBefore(pocetniDatum)) {
      throw new IllegalArgumentException("Završni datum ne može biti prije početnog datuma.");
    }

    // 3) Logička provjera broja putnika
    if (minPutnika > maxPutnika) {
      throw new IllegalArgumentException(
          "Minimalan broj putnika ne može biti veći od maksimalnog.");
    }

    // 4) Logička provjera vremena (ako je postavljeno)
    if (vrijemeKretanja == null && vrijemePovratka != null) {
      throw new IllegalArgumentException(
          "Definirano je vrijeme povratka, ali ne i vrijeme kretanja.");
    }

    if (vrijemeKretanja != null && vrijemePovratka != null) {
      // uspoređujemo samo ako su datumi isti
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
  // ------------------------------------------------------------
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
}
