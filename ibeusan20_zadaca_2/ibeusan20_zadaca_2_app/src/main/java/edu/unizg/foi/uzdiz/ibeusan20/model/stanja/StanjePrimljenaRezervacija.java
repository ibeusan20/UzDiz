package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

// TODO: Auto-generated Javadoc
/**
 * The Class StanjePrimljenaRezervacija.
 */
public class StanjePrimljenaRezervacija implements StanjeRezervacije {

  /** The Constant INSTANCA. */
  private static final StanjePrimljenaRezervacija INSTANCA =
      new StanjePrimljenaRezervacija();

  /**
   * Instantiates a new stanje primljena rezervacija.
   */
  private StanjePrimljenaRezervacija() {}

  /**
   * Instanca.
   *
   * @return the stanje primljena rezervacija
   */
  public static StanjePrimljenaRezervacija instanca() {
    return INSTANCA;
  }

  /**
   * Naziv.
   *
   * @return the string
   */
  @Override
  public String naziv() {
    return "primljena";
  }

  /**
   * Broji se U kvotu.
   *
   * @return true, if successful
   */
  @Override
  public boolean brojiSeUKvotu() {
    return true;
  }

  /**
   * Je aktivna.
   *
   * @return true, if successful
   */
  @Override
  public boolean jeAktivna() {
    return false;
  }
}
