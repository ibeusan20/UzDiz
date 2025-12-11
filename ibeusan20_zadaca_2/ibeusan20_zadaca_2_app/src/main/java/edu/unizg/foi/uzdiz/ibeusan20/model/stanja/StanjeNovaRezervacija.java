package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

public class StanjeNovaRezervacija implements StanjeRezervacije {

  private static final StanjeNovaRezervacija INSTANCA =
      new StanjeNovaRezervacija();

  private StanjeNovaRezervacija() {}

  public static StanjeNovaRezervacija instanca() {
    return INSTANCA;
  }

  @Override
  public String naziv() {
    return "nova";
  }

  @Override
  public boolean brojiSeUKvotu() {
    return false;
  }

  @Override
  public boolean jeAktivna() {
    return false;
  }
}
