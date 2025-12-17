package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

// TODO: Auto-generated Javadoc
/**
 * The Class StanjeNaCekanjuRezervacija.
 */
public class StanjeNaCekanjuRezervacija implements StanjeRezervacije {

  /** The Constant INSTANCA. */
  private static final StanjeNaCekanjuRezervacija INSTANCA =
      new StanjeNaCekanjuRezervacija();

  /**
   * Instantiates a new stanje na cekanju rezervacija.
   */
  private StanjeNaCekanjuRezervacija() {}

  /**
   * Instanca.
   *
   * @return the stanje na cekanju rezervacija
   */
  public static StanjeNaCekanjuRezervacija instanca() {
    return INSTANCA;
  }

  /**
   * Naziv.
   *
   * @return the string
   */
  @Override
  public String naziv() {
    return "na čekanju";
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
