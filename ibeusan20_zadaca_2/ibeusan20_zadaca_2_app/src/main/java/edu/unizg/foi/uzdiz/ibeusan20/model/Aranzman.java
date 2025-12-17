package edu.unizg.foi.uzdiz.ibeusan20.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeAktivanAranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeAranzmana;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOtkazanAranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjePopunjenAranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeUPripremiAranzman;

// TODO: Auto-generated Javadoc
/**
 * The Class Aranzman.
 */
public class Aranzman {

  /** The oznaka. */
  private final String oznaka;
  
  /** The naziv. */
  private final String naziv;
  
  /** The program. */
  private final String program;
  
  /** The pocetni datum. */
  private final LocalDate pocetniDatum;
  
  /** The zavrsni datum. */
  private final LocalDate zavrsniDatum;
  
  /** The vrijeme kretanja. */
  private final LocalTime vrijemeKretanja;
  
  /** The vrijeme povratka. */
  private final LocalTime vrijemePovratka;
  
  /** The cijena. */
  private final float cijena;
  
  /** The min putnika. */
  private final int minPutnika;
  
  /** The max putnika. */
  private final int maxPutnika;
  
  /** The broj nocenja. */
  private final int brojNocenja;
  
  /** The doplata jednokrevetna. */
  private final float doplataJednokrevetna;
  
  /** The prijevoz. */
  private final List<String> prijevoz;
  
  /** The broj dorucaka. */
  private final int brojDorucaka;
  
  /** The broj ruckova. */
  private final int brojRuckova;
  
  /** The broj vecera. */
  private final int brojVecera;

  /** The rezervacije. */
  private final List<Rezervacija> rezervacije = new ArrayList<>();
  
  /** The stanje. */
  private StanjeAranzmana stanje = StanjeUPripremiAranzman.instanca();

  /**
   * Instantiates a new aranzman.
   *
   * @param builder the builder
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

  /**
   * Gets the oznaka.
   *
   * @return the oznaka
   */
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

  /**
   * Gets the rezervacije.
   *
   * @return the rezervacije
   */
  public List<Rezervacija> getRezervacije() {
    return Collections.unmodifiableList(rezervacije);
  }

  /**
   * Je otkazan.
   *
   * @return true, if successful
   */
  public boolean jeOtkazan() {
    return stanje instanceof StanjeOtkazanAranzman;
  }

  /**
   * Dodaj rezervaciju.
   *
   * @param r the r
   */
  public void dodajRezervaciju(Rezervacija r) {
    if (r == null) {
      return;
    }
    if (jeOtkazan()) {
      throw new IllegalStateException(
          "Aranžman '" + oznaka + "' je otkazan - nije moguće dodavati rezervacije.");
    }
    rezervacije.add(r);
  }

  /**
   * Gets the stanje.
   *
   * @return the stanje
   */
  public StanjeAranzmana getStanje() {
    return stanje;
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
   * Postavi otkazan.
   */
  public void postaviOtkazan() {
    stanje = StanjeOtkazanAranzman.instanca();
  }

  /**
   * Azuriraj stanje.
   *
   * @param brojAktivnih the broj aktivnih
   * @param brojPrijava the broj prijava
   */
  public void azurirajStanje(int brojAktivnih, int brojPrijava) {
    if (stanje instanceof StanjeOtkazanAranzman) {
      return;
    }

    // Nema dovoljno prijava -> u pripremi
    if (brojPrijava < minPutnika) {
      stanje = StanjeUPripremiAranzman.instanca();
      return;
    }

    // Dosegnut maksimum aktivnih mjesta -> popunjen
    if (brojAktivnih >= maxPutnika) {
      stanje = StanjePopunjenAranzman.instanca();
      return;
    }

    // Inače je aktivan
    stanje = StanjeAktivanAranzman.instanca();
  }


  /**
   * Obrisi sve rezervacije fizicki.
   *
   * @return the int
   */
  public int obrisiSveRezervacijeFizicki() {
    int n = (rezervacije == null) ? 0 : rezervacije.size();
    if (rezervacije != null) {
      rezervacije.clear();
    }
    return n;
  }
  
  /**
   * Obrisi rezervaciju fizicki.
   *
   * @param r the r
   * @return true, if successful
   */
  public boolean obrisiRezervacijuFizicki(Rezervacija r) {
    if (r == null || rezervacije == null) return false;
    return rezervacije.remove(r);
  }


  /**
   * To string.
   *
   * @return the string
   */
  @Override
  public String toString() {
    return "Aranzman{" + "oznaka='" + oznaka + '\'' + ", naziv='" + naziv + '\'' + ", pocetniDatum="
        + pocetniDatum + ", zavrsniDatum=" + zavrsniDatum + ", stanje=" + stanje.naziv() + '}';
  }
}
