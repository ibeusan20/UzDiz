package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;

/**
 * Komanda Q - završetak rada programa.
 */
public class KomandaQ implements Komanda {
  
  private final FormatIspisaBridge ispis = new TablicniFormat();

  /**
   * Izvrsi.
   *
   * @return true, if successful
   */
  @Override
  public boolean izvrsi() {
    ispis.ispisi("Q \n Program završava. Doviđenja!");
    return false;
  }
}
