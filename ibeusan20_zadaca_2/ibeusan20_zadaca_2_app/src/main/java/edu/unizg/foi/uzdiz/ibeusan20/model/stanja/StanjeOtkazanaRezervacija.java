package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

public class StanjeOtkazanaRezervacija implements StanjeRezervacije {

  private static final StanjeOtkazanaRezervacija INSTANCA =
      new StanjeOtkazanaRezervacija();

  private StanjeOtkazanaRezervacija() {}

  public static StanjeOtkazanaRezervacija instanca() {
    return INSTANCA;
  }

  @Override
  public String naziv() {
    return "otkazana";
  }

  @Override
  public boolean brojiSeUKvotu() {
    return false;
  }

  @Override
  public boolean jeAktivna() {
    return false;
  }

  @Override
  public boolean mozeOtkazati() {
    return false;
  }
}
