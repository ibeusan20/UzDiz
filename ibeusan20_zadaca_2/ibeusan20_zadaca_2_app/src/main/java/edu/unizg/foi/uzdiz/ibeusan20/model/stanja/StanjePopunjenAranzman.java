package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

// TODO: Auto-generated Javadoc
/**
 * The Class StanjePopunjenAranzman.
 */
public class StanjePopunjenAranzman implements StanjeAranzmana {

  /** The Constant INSTANCA. */
  private static final StanjePopunjenAranzman INSTANCA =
      new StanjePopunjenAranzman();

  /**
   * Instantiates a new stanje popunjen aranzman.
   */
  private StanjePopunjenAranzman() {}

  /**
   * Instanca.
   *
   * @return the stanje popunjen aranzman
   */
  public static StanjePopunjenAranzman instanca() {
    return INSTANCA;
  }

  /**
   * Naziv.
   *
   * @return the string
   */
  @Override
  public String naziv() {
    return "popunjen";
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
