package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

public class StanjePopunjenAranzman implements StanjeAranzmana {

  private static final StanjePopunjenAranzman INSTANCA =
      new StanjePopunjenAranzman();

  private StanjePopunjenAranzman() {}

  public static StanjePopunjenAranzman instanca() {
    return INSTANCA;
  }

  @Override
  public String naziv() {
    return "popunjen";
  }

  @Override
  public boolean mozePrimatiRezervacije() {
    return false;
  }
}
