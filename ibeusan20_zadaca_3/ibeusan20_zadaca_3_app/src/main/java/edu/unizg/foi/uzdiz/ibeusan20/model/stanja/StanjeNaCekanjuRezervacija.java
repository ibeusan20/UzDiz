package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

public class StanjeNaCekanjuRezervacija implements StanjeRezervacije {

  private static final StanjeNaCekanjuRezervacija INSTANCA = new StanjeNaCekanjuRezervacija();

  private StanjeNaCekanjuRezervacija() {}

  public static StanjeNaCekanjuRezervacija instanca() {
    return INSTANCA;
  }

  @Override
  public String naziv() {
    return "na čekanju";
  }

  @Override
  public boolean brojiSeUKvotu() {
    return true;
  }

  @Override
  public boolean jeAktivna() {
    return false;
  }
}
