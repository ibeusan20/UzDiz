package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

public class StanjeOdgodenaRezervacija implements StanjeRezervacije {

  private static final StanjeOdgodenaRezervacija INSTANCA =
      new StanjeOdgodenaRezervacija();

  private StanjeOdgodenaRezervacija() {}

  public static StanjeOdgodenaRezervacija instanca() {
    return INSTANCA;
  }

  @Override
  public String naziv() {
    return "odgođena";
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
    // korisnik može otkazati i odgođenu rezervaciju
    return true;
  }
}
