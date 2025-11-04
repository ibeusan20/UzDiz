package komande;

import ispisi.IspisAranzmanDetaljnoAdapter;
import logika.UpraviteljAranzmanima;
import model.Aranzman;

/**
 * Komanda ITAP - ispis detalja pojedinog turističkog aranžmana.
 */
public class KomandaItap implements Komanda {
  private final UpraviteljAranzmanima upravitelj;
  private final String[] argumenti;

  /**
   * Instancira novu komandu itap.
   *
   * @param upravitelj the upravitelj
   * @param argumenti the argumenti
   */
  public KomandaItap(UpraviteljAranzmanima upravitelj, String... argumenti) {
    this.upravitelj = upravitelj;
    this.argumenti = argumenti;
  }

  /**
   * Izvrsi.
   *
   * @return true, if successful
   */
  @Override
  public boolean izvrsi() {
    if (argumenti.length < 1) {
      System.out.println("Nedostaje oznaka aranžmana (ITAP oznaka).");
      return true;
    }

    String oznaka = argumenti[0].trim();
    Aranzman a = upravitelj.pronadiPoOznaci(oznaka);

    if (a == null) {
      System.out.println("Aranžman s oznakom '" + oznaka + "' nije pronađen.");
      return true;
    }

    System.out.println("\n--- Detalji aranžmana ---");
    new IspisAranzmanDetaljnoAdapter(a).ispisiDetalje();
    return true;
  }
}
