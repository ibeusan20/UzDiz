package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

// TODO: Auto-generated Javadoc
/**
 * The Class StanjeUPripremiAranzman.
 */
public class StanjeUPripremiAranzman implements StanjeAranzmana {

  /** The Constant INSTANCA. */
  private static final StanjeUPripremiAranzman INSTANCA =
      new StanjeUPripremiAranzman();

  /**
   * Instantiates a new stanje U pripremi aranzman.
   */
  private StanjeUPripremiAranzman() {}

  /**
   * Instanca.
   *
   * @return the stanje U pripremi aranzman
   */
  public static StanjeUPripremiAranzman instanca() {
    return INSTANCA;
  }

  /**
   * Naziv.
   *
   * @return the string
   */
  @Override
  public String naziv() {
    return "u pripremi";
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
