package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

// TODO: Auto-generated Javadoc
/**
 * The Class StanjeNovaRezervacija.
 */
public class StanjeNovaRezervacija implements StanjeRezervacije {

  /** The Constant INSTANCA. */
  private static final StanjeNovaRezervacija INSTANCA =
      new StanjeNovaRezervacija();

  /**
   * Instantiates a new stanje nova rezervacija.
   */
  private StanjeNovaRezervacija() {}

  /**
   * Instanca.
   *
   * @return the stanje nova rezervacija
   */
  public static StanjeNovaRezervacija instanca() {
    return INSTANCA;
  }

  /**
   * Naziv.
   *
   * @return the string
   */
  @Override
  public String naziv() {
    return "nova";
  }

  /**
   * Broji se U kvotu.
   *
   * @return true, if successful
   */
  @Override
  public boolean brojiSeUKvotu() {
    return false;
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
