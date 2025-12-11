package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import java.util.stream.Collectors;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.FormatDatuma;

/**
 * Adapter za detaljan ispis jednog turističkog aranžmana.
 * <p>
 * Koristi {@link Aranzman} i pretvara sve njegove atribute u čitljiv tekstualni oblik za prikaz na
 * konzoli.
 * </p>
 */
public class IspisAranzmanDetaljnoAdapter {
  private final Aranzman a;

  /**
   * @param a objekt aranžmana koji se ispisuje
   */
  public IspisAranzmanDetaljnoAdapter(Aranzman a) {
    this.a = a;
  }

  /**
   * Ispisuje sve detalje o aranžmanu u čitljivom obliku.
   */
  public void ispisiDetalje() {
    System.out.println("Oznaka: " + a.getOznaka());
    System.out.println("Naziv: " + a.getNaziv());
    System.out.println("Program: " + a.getProgram());
    System.out.println("Početni datum: " + FormatDatuma.formatiraj(a.getPocetniDatum()));
    System.out.println("Završni datum: " + FormatDatuma.formatiraj(a.getZavrsniDatum()));
    System.out.println("Vrijeme kretanja: " + FormatDatuma.formatiraj(a.getVrijemeKretanja()));
    System.out.println("Vrijeme povratka: " + FormatDatuma.formatiraj(a.getVrijemePovratka()));
    System.out.println("Cijena: " + String.format("%.2f €", a.getCijena()));
    System.out.println("Min putnika: " + a.getMinPutnika());
    System.out.println("Max putnika: " + a.getMaxPutnika());
    System.out.println("Broj noćenja: " + a.getBrojNocenja());
    System.out
        .println("Doplata jednokrevetna: " + String.format("%.2f €", a.getDoplataJednokrevetna()));
    System.out.println("Prijevoz: " + (a.getPrijevoz() == null ? ""
        : a.getPrijevoz().stream().collect(Collectors.joining("; "))));
    System.out.println("Doručci: " + a.getBrojDorucaka());
    System.out.println("Ručkovi: " + a.getBrojRuckova());
    System.out.println("Večere: " + a.getBrojVecera());
  }
}
