package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

// TODO: Auto-generated Javadoc
/**
 * The Class StanjeAktivanAranzman.
 */
public class StanjeAktivanAranzman implements StanjeAranzmana {

  /** The Constant INSTANCA. */
  private static final StanjeAktivanAranzman INSTANCA =
      new StanjeAktivanAranzman();

  /**
   * Instantiates a new stanje aktivan aranzman.
   */
  private StanjeAktivanAranzman() {}

  /**
   * Instanca.
   *
   * @return the stanje aktivan aranzman
   */
  public static StanjeAktivanAranzman instanca() {
    return INSTANCA;
  }

  /**
   * Naziv.
   *
   * @return the string
   */
  @Override
  public String naziv() {
    return "aktivan";
  }

  /**
   * Moze primati rezervacije.
   *
   * @return true, if successful
   */
  @Override
  public boolean mozePrimatiRezervacije() {
    return true;
  }
}
