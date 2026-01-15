package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

public class StanjePrimljenaRezervacija implements StanjeRezervacije {

  private static final StanjePrimljenaRezervacija INSTANCA = new StanjePrimljenaRezervacija();

  private StanjePrimljenaRezervacija() {}

  public static StanjePrimljenaRezervacija instanca() {
    return INSTANCA;
  }

  @Override
  public String naziv() {
    return "primljena";
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
