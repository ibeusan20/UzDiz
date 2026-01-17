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
import edu.unizg.foi.uzdiz.ibeusan20.visitor.Posjetitelj;
import edu.unizg.foi.uzdiz.ibeusan20.visitor.Posjetljiv;


/**
 * Predstavlja turistički aranžman.
 * <p>
 * Aranžman sadrži osnovne podatke (termin, cijenu, ograničenja broja putnika, prijevoz i obroke) te
 * povezane {@link Rezervacija} zapise.
 * </p>
 *
 * <p>
 * Stanje aranžmana se vodi uz pomoć uzorka <b>State</b> (implementacije {@link StanjeAranzmana}).
 * Početno stanje je {@link StanjeUPripremiAranzman}. Kada je aranžman otkazan
 * ({@link StanjeOtkazanAranzman}), dodavanje rezervacija nije dozvoljeno.
 * </p>
 */
public class Aranzman implements Posjetljiv {

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

  private final List<Rezervacija> rezervacije = new ArrayList<>();
  private StanjeAranzmana stanje = StanjeUPripremiAranzman.instanca();

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

  public List<Rezervacija> getRezervacije() {
    return Collections.unmodifiableList(rezervacije);
  }

  public boolean jeOtkazan() {
    return stanje instanceof StanjeOtkazanAranzman;
  }

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

  public StanjeAranzmana getStanje() {
    return stanje;
  }

  public String nazivStanja() {
    return stanje.naziv();
  }

  public void postaviOtkazan() {
    stanje = StanjeOtkazanAranzman.instanca();
  }

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

  public int obrisiSveRezervacijeFizicki() {
    int n = (rezervacije == null) ? 0 : rezervacije.size();
    if (rezervacije != null) {
      rezervacije.clear();
    }
    return n;
  }

  public boolean obrisiRezervacijuFizicki(Rezervacija r) {
    if (r == null || rezervacije == null)
      return false;
    return rezervacije.remove(r);
  }

  @Override
  public String toString() {
    return "Aranzman{" + "oznaka='" + oznaka + '\'' + ", naziv='" + naziv + '\'' + ", pocetniDatum="
        + pocetniDatum + ", zavrsniDatum=" + zavrsniDatum + ", stanje=" + stanje.naziv() + '}';
  }

  @Override
  public void prihvati(Posjetitelj p, Aranzman kontekst) {
    if (p == null) {
      return;
    }
    p.posjetiAranzman(this);
    for (Rezervacija r : rezervacije) {
      if (r != null) {
        r.prihvati(p, this);
      }
    }
  }

}
