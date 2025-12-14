package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import java.time.format.DateTimeFormatter;
import java.util.StringJoiner;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

/**
 * Detaljni ispis jednog aranžmana (ITAP).
 */
public class IspisAranzmanDetaljnoAdapter {

  private static final DateTimeFormatter FORMAT_DATUM =
      DateTimeFormatter.ofPattern("dd.MM.yyyy.");
  private static final DateTimeFormatter FORMAT_VRIJEME =
      DateTimeFormatter.ofPattern("HH:mm");

  private final Aranzman aranzman;

  public IspisAranzmanDetaljnoAdapter(Aranzman aranzman) {
    this.aranzman = aranzman;
  }

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
    if (aranzman.getPrijevoz() != null && !aranzman.getPrijevoz().isEmpty()) {
      StringJoiner sj = new StringJoiner(", ");
      aranzman.getPrijevoz().forEach(sj::add);
      prijevoz = sj.toString();
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
    System.out.println("Doručaka: " + FormatBrojeva.cijeli(aranzman.getBrojDorucaka()));
    System.out.println("Ručkova: " + FormatBrojeva.cijeli(aranzman.getBrojRuckova()));
    System.out.println("Večera: " + FormatBrojeva.cijeli(aranzman.getBrojVecera()));

    System.out.println("Stanje aranžmana: " + aranzman.nazivStanja()); // STATE
  }
}
