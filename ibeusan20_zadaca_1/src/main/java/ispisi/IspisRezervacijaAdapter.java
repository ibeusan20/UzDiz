package ispisi;

import model.Rezervacija;
import java.time.format.DateTimeFormatter;

/**
 * Adapter između klase Rezervacija i formata ispisa.
 */
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
    if (r.getDatumVrijeme() == null)
      return "";
    return r.getDatumVrijeme().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss"));
  }

  public String getVrsta() {
    switch (r.getVrsta()) {
      case "PA":
        return r.isAktivna() ? "Aktivna" : "Primljena";
      case "Č":
        return "Na čekanju";
      case "O":
        return "Otkazana";
      default:
        return r.getVrsta();
    }
  }

  public String getDatumVrijemeOtkaza() {
    if (r.getDatumVrijemeOtkaza() == null)
      return "";
    return r.getDatumVrijemeOtkaza().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss"));
  }
}
