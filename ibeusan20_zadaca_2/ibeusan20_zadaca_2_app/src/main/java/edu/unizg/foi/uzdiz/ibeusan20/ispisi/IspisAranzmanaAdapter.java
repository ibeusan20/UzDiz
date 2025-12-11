package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import java.time.format.DateTimeFormatter;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

/**
 * Adapter za osnovni ispis aranžmana (koristi se u ITAK).
 */
public class IspisAranzmanaAdapter implements IspisniRed {

  private static final DateTimeFormatter FORMAT_DATUM =
      DateTimeFormatter.ofPattern("dd.MM.yyyy.");

  private final Aranzman aranzman;

  public IspisAranzmanaAdapter(Aranzman aranzman) {
    this.aranzman = aranzman;
  }

  @Override
  public String[] zaglavlje() {
    return new String[] {"Oznaka", "Naziv", "Od", "Do", "Stanje"};
  }

  @Override
  public String[] vrijednosti() {
    String od = aranzman.getPocetniDatum() == null ? ""
        : aranzman.getPocetniDatum().format(FORMAT_DATUM);
    String d0 = aranzman.getZavrsniDatum() == null ? ""
        : aranzman.getZavrsniDatum().format(FORMAT_DATUM);

    return new String[] {
        aranzman.getOznaka(),
        aranzman.getNaziv(),
        od,
        d0,
        aranzman.nazivStanja()   // STATE, tekstualno
    };
  }
}
