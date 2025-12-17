package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

public class StanjeAktivanAranzman implements StanjeAranzmana {

  private static final StanjeAktivanAranzman INSTANCA =
      new StanjeAktivanAranzman();

  private StanjeAktivanAranzman() {}

  public static StanjeAktivanAranzman instanca() {
    return INSTANCA;
  }

  @Override
  public String naziv() {
    return "aktivan";
  }

  @Override
  public boolean mozePrimatiRezervacije() {
    return true;
  }
}
