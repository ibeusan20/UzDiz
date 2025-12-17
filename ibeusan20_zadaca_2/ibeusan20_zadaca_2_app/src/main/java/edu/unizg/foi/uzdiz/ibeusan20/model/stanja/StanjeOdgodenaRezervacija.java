package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

// TODO: Auto-generated Javadoc
/**
 * The Class StanjeOdgodenaRezervacija.
 */
public class StanjeOdgodenaRezervacija implements StanjeRezervacije {

  /** The Constant INSTANCA. */
  private static final StanjeOdgodenaRezervacija INSTANCA =
      new StanjeOdgodenaRezervacija();

  /**
   * Instantiates a new stanje odgodena rezervacija.
   */
  private StanjeOdgodenaRezervacija() {}

  /**
   * Instanca.
   *
   * @return the stanje odgodena rezervacija
   */
  public static StanjeOdgodenaRezervacija instanca() {
    return INSTANCA;
  }

  /**
   * Naziv.
   *
   * @return the string
   */
  @Override
  public String naziv() {
    return "odgođena";
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
    // korisnik može otkazati i odgođenu rezervaciju
    return true;
  }
}
