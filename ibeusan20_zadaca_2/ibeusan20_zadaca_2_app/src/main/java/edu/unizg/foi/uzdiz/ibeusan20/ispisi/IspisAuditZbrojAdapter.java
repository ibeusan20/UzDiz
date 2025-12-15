package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

public class IspisAuditZbrojAdapter implements IspisniRed {

  private final String komanda;
  private final int broj;

  public IspisAuditZbrojAdapter(String komanda, int broj) {
    this.komanda = komanda;
    this.broj = broj;
  }

  @Override
  public String[] zaglavlje() {
    return new String[] { "Komanda", "Broj" };
  }

  @Override
  public String[] vrijednosti() {
    return new String[] {
        komanda == null ? "" : komanda,
        String.valueOf(broj)
    };
  }
}
