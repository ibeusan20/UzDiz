package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

/**
 * Jedan red detaljnog ispisa u obliku "Polje | Vrijednost".
 */
public class IspisParAdapter implements IspisniRed {

  private final String polje;
  private final String vrijednost;

  public IspisParAdapter(String polje, String vrijednost) {
    this.polje = polje == null ? "" : polje;
    this.vrijednost = vrijednost == null ? "" : vrijednost;
  }

  @Override
  public String[] zaglavlje() {
    return new String[] {"Polje", "Vrijednost"};
  }

  @Override
  public String[] vrijednosti() {
    return new String[] {polje, vrijednost};
  }
}
