package ispisi;

import model.Rezervacija;
import java.time.format.DateTimeFormatter;
import model.Aranzman;

/**
 * Adapter koji povezuje {@link Rezervacija} i {@link Aranzman} radi ispisa rezervacija jedne osobe.
 */
public class IspisRezervacijaOsobeAdapter {
  private final Rezervacija r;
  private final Aranzman a;

  /**
   * @param r rezervacija osobe
   * @param a aranžman povezan s tom rezervacijom
   */
  public IspisRezervacijaOsobeAdapter(Rezervacija r, Aranzman a) {
    this.r = r;
    this.a = a;
  }

  /** @return formatirani datum i vrijeme rezervacije */
  public String getDatumVrijeme() {
    if (r.getDatumVrijeme() == null)
      return "";
    return r.getDatumVrijeme().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss"));
  }

  /** @return oznaka aranžmana */
  public String getOznaka() {
    return r.getOznakaAranzmana();
  }

  /** @return naziv aranžmana (ili poruka ako nedostaje) */
  public String getNazivAranzmana() {
    return (a != null) ? a.getNaziv() : "(Nepoznat aranžman)";
  }

  /** @return status rezervacije */
  public String getVrsta() {
    return r.getVrsta();
  }
}
