package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import java.time.format.DateTimeFormatter;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

/**
 * Adapter za osnovni ispis aranžmana (ITAK).
 */
public class IspisAranzmanaAdapter implements IspisniRed {

  private static final DateTimeFormatter FORMAT_DATUM = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
  private static final DateTimeFormatter FORMAT_VRIJEME = DateTimeFormatter.ofPattern("HH:mm");

  private final Aranzman aranzman;

  public IspisAranzmanaAdapter(Aranzman aranzman) {
    this.aranzman = aranzman;
  }

  @Override
  public String[] zaglavlje() {
    return new String[] {
        "Oznaka", "Naziv", "Početni datum", "Završni datum",
        "Kretanje", "Povratak", "Cijena", "Min", "Max", "Status"
    };
  }

  @Override
  public String[] vrijednosti() {
    if (aranzman == null) {
      return new String[] {"", "", "", "", "", "", "", "", "", ""};
    }

    String od = aranzman.getPocetniDatum() == null ? "" : aranzman.getPocetniDatum().format(FORMAT_DATUM);
    String d0 = aranzman.getZavrsniDatum() == null ? "" : aranzman.getZavrsniDatum().format(FORMAT_DATUM);

    String vk = aranzman.getVrijemeKretanja() == null ? "" : aranzman.getVrijemeKretanja().format(FORMAT_VRIJEME);
    String vp = aranzman.getVrijemePovratka() == null ? "" : aranzman.getVrijemePovratka().format(FORMAT_VRIJEME);

    String cijena = String.format("%.2f", aranzman.getCijena());

    return new String[] {
        aranzman.getOznaka(),
        aranzman.getNaziv(),
        od,
        d0,
        vk,
        vp,
        cijena,
        String.valueOf(aranzman.getMinPutnika()),
        String.valueOf(aranzman.getMaxPutnika()),
        aranzman.nazivStanja()
    };
  }
}
