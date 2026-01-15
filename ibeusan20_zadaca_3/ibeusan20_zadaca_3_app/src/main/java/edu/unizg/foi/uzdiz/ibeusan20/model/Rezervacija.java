package edu.unizg.foi.uzdiz.ibeusan20.model;

import java.time.LocalDateTime;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOdgodenaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOtkazanaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjePrimljenaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeRezervacije;

/**
 * Predstavlja rezervaciju turističkog aranžmana. Koristi uzorak State za upravljanje statusima.
 * 
 * <p>
 * Status rezervacije se vodi uz pomoć uzorka <b>State</b> (implementacije
 * {@link StanjeRezervacije}).
 * </p>
 */
public class Rezervacija {

  private final String ime;
  private final String prezime;
  private final String oznakaAranzmana;
  private final LocalDateTime datumVrijeme;

  private LocalDateTime datumVrijemeOtkaza;
  private StanjeRezervacije stanje;

  public Rezervacija(String ime, String prezime, String oznakaAranzmana,
      LocalDateTime datumVrijeme) {
    this(ime, prezime, oznakaAranzmana, datumVrijeme, StanjePrimljenaRezervacija.instanca(), null);
  }

  public Rezervacija(String ime, String prezime, String oznakaAranzmana, LocalDateTime datumVrijeme,
      StanjeRezervacije stanje, LocalDateTime datumVrijemeOtkaza) {
    this.ime = ime;
    this.prezime = prezime;
    this.oznakaAranzmana = oznakaAranzmana;
    this.datumVrijeme = datumVrijeme;
    this.stanje = stanje;
    this.datumVrijemeOtkaza = datumVrijemeOtkaza;
  }

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

  public LocalDateTime getDatumVrijemeOtkaza() {
    return datumVrijemeOtkaza;
  }

  public StanjeRezervacije getStanje() {
    return stanje;
  }

  public void postaviStanje(StanjeRezervacije novoStanje) {
    if (novoStanje == null) {
      return;
    }
    this.stanje = novoStanje;
  }

  public boolean jeAktivna() {
    return stanje.jeAktivna();
  }

  public boolean brojiSeUKvotu() {
    return stanje.brojiSeUKvotu();
  }

  public String nazivStanja() {
    return stanje.naziv();
  }

  public void otkazi(LocalDateTime vrijemeOtkaza) {
    if (!stanje.mozeOtkazati()) {
      return;
    }
    this.datumVrijemeOtkaza = vrijemeOtkaza;
    this.stanje = StanjeOtkazanaRezervacija.instanca();
  }

  public void odgodi(LocalDateTime vrijemeOdgode) {
    this.datumVrijemeOtkaza = vrijemeOdgode;
    this.stanje = StanjeOdgodenaRezervacija.instanca();
  }
}
