package ispisi;

import model.Rezervacija;
import java.time.format.DateTimeFormatter;
import model.Aranzman;

/**
 * Adapter između rezervacije i aranžmana za ispis rezervacija osobe.
 */
public class IspisRezervacijaOsobeAdapter {

  private final Rezervacija r;
  private final Aranzman a;

  public IspisRezervacijaOsobeAdapter(Rezervacija r, Aranzman a) {
    this.r = r;
    this.a = a;
  }

  public String getDatumVrijeme() {
    if (r.getDatumVrijeme() == null)
      return "";
    return r.getDatumVrijeme().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss"));
  }

  public String getOznaka() {
    return r.getOznakaAranzmana();
  }

  public String getNazivAranzmana() {
    return (a != null) ? a.getNaziv() : "(Nepoznat aranžman)";
  }

  public String getVrsta() {
    return r.getVrsta();
  }
}
