package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

// TODO: Auto-generated Javadoc
/**
 * The Class StanjeAktivnaRezervacija.
 */
public class StanjeAktivnaRezervacija implements StanjeRezervacije {

  /** The Constant INSTANCA. */
  private static final StanjeAktivnaRezervacija INSTANCA =
      new StanjeAktivnaRezervacija();

  /**
   * Instantiates a new stanje aktivna rezervacija.
   */
  private StanjeAktivnaRezervacija() {}

  /**
   * Instanca.
   *
   * @return the stanje aktivna rezervacija
   */
  public static StanjeAktivnaRezervacija instanca() {
    return INSTANCA;
  }

  /**
   * Naziv.
   *
   * @return the string
   */
  @Override
  public String naziv() {
    return "aktivna";
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
    return true;
  }
}
