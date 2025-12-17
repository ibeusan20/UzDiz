package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import java.time.format.DateTimeFormatter;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

// TODO: Auto-generated Javadoc
/**
 * Detaljni ispis jednog aranžmana (ITAP).
 */
public class IspisAranzmanDetaljnoAdapter {

  /** The Constant FORMAT_DATUM. */
  private static final DateTimeFormatter FORMAT_DATUM =
      DateTimeFormatter.ofPattern("dd.MM.yyyy.");
  
  /** The Constant FORMAT_VRIJEME. */
  private static final DateTimeFormatter FORMAT_VRIJEME =
      DateTimeFormatter.ofPattern("HH:mm");

  /** The aranzman. */
  private final Aranzman aranzman;

  /**
   * Instantiates a new ispis aranzman detaljno adapter.
   *
   * @param aranzman the aranzman
   */
  public IspisAranzmanDetaljnoAdapter(Aranzman aranzman) {
    this.aranzman = aranzman;
  }

  /**
   * Ispisi detalje.
   */
  public void ispisiDetalje() {
    String od = aranzman.getPocetniDatum() == null ? ""
        : aranzman.getPocetniDatum().format(FORMAT_DATUM);
    String d0 = aranzman.getZavrsniDatum() == null ? ""
        : aranzman.getZavrsniDatum().format(FORMAT_DATUM);
    String vk = aranzman.getVrijemeKretanja() == null ? ""
        : aranzman.getVrijemeKretanja().format(FORMAT_VRIJEME);
    String vp = aranzman.getVrijemePovratka() == null ? ""
        : aranzman.getVrijemePovratka().format(FORMAT_VRIJEME);

    String prijevoz = "";
    List<String> lista = aranzman.getPrijevoz();
    if (lista != null && !lista.isEmpty()) {
      prijevoz = lista.stream()
          .filter(s -> s != null && !s.isBlank())
          .map(String::trim)
          .reduce((a, b) -> a + ", " + b)
          .orElse("");
    }

    System.out.println("Oznaka: " + aranzman.getOznaka());
    System.out.println("Naziv: " + aranzman.getNaziv());
    System.out.println("Program: " + aranzman.getProgram());
    System.out.println("Početni datum: " + od);
    System.out.println("Završni datum: " + d0);
    System.out.println("Vrijeme kretanja: " + vk);
    System.out.println("Vrijeme povratka: " + vp);
    System.out.println("Cijena: " + FormatBrojeva.eur(aranzman.getCijena()));
    System.out.println("Min. putnika: " + FormatBrojeva.cijeli(aranzman.getMinPutnika()));
    System.out.println("Max. putnika: " + FormatBrojeva.cijeli(aranzman.getMaxPutnika()));
    System.out.println("Broj noćenja: " + FormatBrojeva.cijeli(aranzman.getBrojNocenja()));
    System.out.println("Doplata jednokrevetna: " + FormatBrojeva.eur(aranzman.getDoplataJednokrevetna()));
    System.out.println("Prijevoz: " + prijevoz);
    System.out.println("Doručaka: " + FormatBrojeva.cijeli(aranzman.getBrojDorucaka()));
    System.out.println("Ručkova: " + FormatBrojeva.cijeli(aranzman.getBrojRuckova()));
    System.out.println("Večera: " + FormatBrojeva.cijeli(aranzman.getBrojVecera()));

    System.out.println("Stanje aranžmana: " + aranzman.nazivStanja()); // STATE
  }
}
