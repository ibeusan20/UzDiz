package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

// TODO: Auto-generated Javadoc
/**
 * The Class StanjeOtkazanaRezervacija.
 */
public class StanjeOtkazanaRezervacija implements StanjeRezervacije {

  /** The Constant INSTANCA. */
  private static final StanjeOtkazanaRezervacija INSTANCA =
      new StanjeOtkazanaRezervacija();

  /**
   * Instantiates a new stanje otkazana rezervacija.
   */
  private StanjeOtkazanaRezervacija() {}

  /**
   * Instanca.
   *
   * @return the stanje otkazana rezervacija
   */
  public static StanjeOtkazanaRezervacija instanca() {
    return INSTANCA;
  }

  /**
   * Naziv.
   *
   * @return the string
   */
  @Override
  public String naziv() {
    return "otkazana";
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

  /**
   * Moze otkazati.
   *
   * @return true, if successful
   */
  @Override
  public boolean mozeOtkazati() {
    return false;
  }
}
