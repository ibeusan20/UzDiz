package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import java.time.format.DateTimeFormatter;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

// TODO: Auto-generated Javadoc
/**
 * Adapter za osnovni ispis aranžmana (ITAK).
 */
public class IspisAranzmanaAdapter implements IspisniRed {

  /** The Constant FORMAT_DATUM. */
  private static final DateTimeFormatter FORMAT_DATUM = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
  
  /** The Constant FORMAT_VRIJEME. */
  private static final DateTimeFormatter FORMAT_VRIJEME = DateTimeFormatter.ofPattern("HH:mm:ss");

  /** The aranzman. */
  private final Aranzman aranzman;

  /**
   * Instantiates a new ispis aranzmana adapter.
   *
   * @param aranzman the aranzman
   */
  public IspisAranzmanaAdapter(Aranzman aranzman) {
    this.aranzman = aranzman;
  }

  /**
   * Zaglavlje.
   *
   * @return the string[]
   */
  @Override
  public String[] zaglavlje() {
    return new String[] {
        "Oznaka", "Naziv", "Početni datum", "Završni datum",
        "Kretanje", "Povratak", "Cijena", "Min", "Max", "Status"
    };
  }

  /**
   * Vrijednosti.
   *
   * @return the string[]
   */
  @Override
  public String[] vrijednosti() {
    if (aranzman == null) {
      return new String[] {"", "", "", "", "", "", "", "", "", ""};
    }

    String od = aranzman.getPocetniDatum() == null ? "" : aranzman.getPocetniDatum().format(FORMAT_DATUM);
    String d0 = aranzman.getZavrsniDatum() == null ? "" : aranzman.getZavrsniDatum().format(FORMAT_DATUM);

    String vk = aranzman.getVrijemeKretanja() == null ? "" : aranzman.getVrijemeKretanja().format(FORMAT_VRIJEME);
    String vp = aranzman.getVrijemePovratka() == null ? "" : aranzman.getVrijemePovratka().format(FORMAT_VRIJEME);

    String cijena = String.valueOf(aranzman.getCijena());

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
