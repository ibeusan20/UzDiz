package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;
import java.time.format.DateTimeFormatter;

public class IspisRezervacijaAdapter {

  private final Rezervacija r;

  public IspisRezervacijaAdapter(Rezervacija r) {
    this.r = r;
  }

  public String getIme() {
    return r.getIme();
  }

  public String getPrezime() {
    return r.getPrezime();
  }

  public String getDatumVrijeme() {
    if (r.getDatumVrijeme() == null) {
      return "";
    }
    return r.getDatumVrijeme()
        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss"));
  }

  /**
   * Vraca tekstualni naziv stanja rezervacije.
   */
  public String getVrsta() {
    return r.nazivStanja();
  }

  public String getDatumVrijemeOtkaza() {
    if (r.getDatumVrijemeOtkaza() == null) {
      return "";
    }
    return r.getDatumVrijemeOtkaza()
        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss"));
  }
}
