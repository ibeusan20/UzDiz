package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

// TODO: Auto-generated Javadoc
/**
 * The Class StanjeOtkazanAranzman.
 */
public class StanjeOtkazanAranzman implements StanjeAranzmana {

  /** The Constant INSTANCA. */
  private static final StanjeOtkazanAranzman INSTANCA =
      new StanjeOtkazanAranzman();

  /**
   * Instantiates a new stanje otkazan aranzman.
   */
  private StanjeOtkazanAranzman() {}

  /**
   * Instanca.
   *
   * @return the stanje otkazan aranzman
   */
  public static StanjeOtkazanAranzman instanca() {
    return INSTANCA;
  }

  /**
   * Naziv.
   *
   * @return the string
   */
  @Override
  public String naziv() {
    return "otkazan";
  }

  /**
   * Moze primati rezervacije.
   *
   * @return true, if successful
   */
  @Override
  public boolean mozePrimatiRezervacije() {
    return false;
  }
}
