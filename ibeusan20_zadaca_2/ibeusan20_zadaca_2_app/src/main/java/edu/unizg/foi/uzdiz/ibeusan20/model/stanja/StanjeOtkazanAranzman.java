package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

public class StanjeOtkazanAranzman implements StanjeAranzmana {

  private static final StanjeOtkazanAranzman INSTANCA =
      new StanjeOtkazanAranzman();

  private StanjeOtkazanAranzman() {}

  public static StanjeOtkazanAranzman instanca() {
    return INSTANCA;
  }

  @Override
  public String naziv() {
    return "otkazan";
  }

  @Override
  public boolean mozePrimatiRezervacije() {
    return false;
  }
}
