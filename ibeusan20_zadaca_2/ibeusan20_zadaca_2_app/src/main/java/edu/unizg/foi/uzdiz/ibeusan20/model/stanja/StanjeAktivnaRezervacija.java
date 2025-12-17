package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

public class StanjeAktivnaRezervacija implements StanjeRezervacije {

  private static final StanjeAktivnaRezervacija INSTANCA = new StanjeAktivnaRezervacija();

  private StanjeAktivnaRezervacija() {}

  public static StanjeAktivnaRezervacija instanca() {
    return INSTANCA;
  }

  @Override
  public String naziv() {
    return "aktivna";
  }

  @Override
  public boolean brojiSeUKvotu() {
    return true;
  }

  @Override
  public boolean jeAktivna() {
    return true;
  }
}
