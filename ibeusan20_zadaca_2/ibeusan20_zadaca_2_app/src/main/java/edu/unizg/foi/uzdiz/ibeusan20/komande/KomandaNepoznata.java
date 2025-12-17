package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;

// TODO: Auto-generated Javadoc
/**
 * The Class KomandaNepoznata.
 */
public class KomandaNepoznata implements Komanda {

  /** The unos. */
  private final String unos;
  
  /** The ispis. */
  private final FormatIspisaBridge ispis = new TablicniFormat();

  /**
   * Instantiates a new komanda nepoznata.
   *
   * @param unos the unos
   */
  public KomandaNepoznata(String unos) {
    this.unos = (unos == null) ? "" : unos.trim();
  }

  /**
   * Izvrsi.
   *
   * @return true, if successful
   */
  @Override
  public boolean izvrsi() {
    ispis.ispisi("Nepoznata komanda: " + unos);
    return true;
  }
}
