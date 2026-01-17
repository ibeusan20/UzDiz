package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

public class IspisPretragaAranzmanaAdapter implements IspisniRed {
  private final Aranzman a;

  public IspisPretragaAranzmanaAdapter(Aranzman a) {
    this.a = a;
  }

  @Override
  public String[] zaglavlje() {
    return new String[] {"Oznaka", "Naziv"};
  }

  @Override
  public String[] vrijednosti() {
    if (a == null) {
      return new String[] {"", ""};
    }
    return new String[] {a.getOznaka(), a.getNaziv()};
  }
}
