package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

// TODO: Auto-generated Javadoc
/**
 * Jedan red detaljnog ispisa u obliku "Polje | Vrijednost".
 */
public class IspisParAdapter implements IspisniRed {

  /** The polje. */
  private final String polje;
  
  /** The vrijednost. */
  private final String vrijednost;

  /**
   * Instantiates a new ispis par adapter.
   *
   * @param polje the polje
   * @param vrijednost the vrijednost
   */
  public IspisParAdapter(String polje, String vrijednost) {
    this.polje = polje == null ? "" : polje;
    this.vrijednost = vrijednost == null ? "" : vrijednost;
  }

  /**
   * Zaglavlje.
   *
   * @return the string[]
   */
  @Override
  public String[] zaglavlje() {
    return new String[] {"Polje", "Vrijednost"};
  }

  /**
   * Vrijednosti.
   *
   * @return the string[]
   */
  @Override
  public String[] vrijednosti() {
    return new String[] {polje, vrijednost};
  }
}
