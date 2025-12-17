package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

// TODO: Auto-generated Javadoc
/**
 * The Class IspisAuditZbrojAdapter.
 */
public class IspisAuditZbrojAdapter implements IspisniRed {

  /** The komanda. */
  private final String komanda;
  
  /** The broj. */
  private final int broj;

  /**
   * Instantiates a new ispis audit zbroj adapter.
   *
   * @param komanda the komanda
   * @param broj the broj
   */
  public IspisAuditZbrojAdapter(String komanda, int broj) {
    this.komanda = komanda;
    this.broj = broj;
  }

  /**
   * Zaglavlje.
   *
   * @return the string[]
   */
  @Override
  public String[] zaglavlje() {
    return new String[] { "Komanda", "Broj" };
  }

  /**
   * Vrijednosti.
   *
   * @return the string[]
   */
  @Override
  public String[] vrijednosti() {
    return new String[] {
        komanda == null ? "" : komanda,
        String.valueOf(broj)
    };
  }
}
