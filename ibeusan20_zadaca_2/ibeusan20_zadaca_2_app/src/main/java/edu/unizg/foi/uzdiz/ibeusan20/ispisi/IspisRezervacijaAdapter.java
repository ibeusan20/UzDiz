package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import java.time.format.DateTimeFormatter;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

/**
 * Adapter između klase {@link Rezervacija} i formata ispisa.
 * <p>
 * Pretvara podatke rezervacije u oblik pogodan za prikaz u tabličnom formatu.
 * </p>
 */
public class IspisRezervacijaAdapter {
  private final Rezervacija r;

  /**
   * @param r objekt rezervacije za ispis
   */
  public IspisRezervacijaAdapter(Rezervacija r) {
    this.r = r;
  }

  /** @return ime korisnika */
  public String getIme() {
    return r.getIme();
  }

  /** @return prezime korisnika */
  public String getPrezime() {
    return r.getPrezime();
  }

  /** @return formatirani datum i vrijeme rezervacije */
  public String getDatumVrijeme() {
    if (r.getDatumVrijeme() == null)
      return "";
    return r.getDatumVrijeme().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss"));
  }

  /** @return tekstualni status rezervacije */
  public String getVrsta() {
    switch (r.getVrsta()) {
      case "PA":
        return r.jeAktivna() ? "Aktivna" : "Primljena";
      case "Č":
        return "Na čekanju";
      case "O":
        return "Otkazana";
      default:
        return r.getVrsta();
    }
  }

  /** @return formatirani datum otkazivanja (ako postoji) */
  public String getDatumVrijemeOtkaza() {
    if (r.getDatumVrijemeOtkaza() == null)
      return "";
    return r.getDatumVrijemeOtkaza().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss"));
  }
}
