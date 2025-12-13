package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

/**
 * Adapter za ispis jedne tekstualne poruke preko Bridge-a.
 * Ne ispisuje zaglavlje.
 */
public class IspisTekstAdapter implements IspisniRed {

  private final String poruka;

  public IspisTekstAdapter(String poruka) {
    this.poruka = poruka == null ? "" : poruka;
  }

  @Override
  public String[] zaglavlje() {
    return null; // bez zaglavlja
  }

  @Override
  public String[] vrijednosti() {
    return new String[] {poruka};
  }
}
