package edu.unizg.foi.uzdiz.ibeusan20.model.stanja;

public class StanjeUPripremiAranzman implements StanjeAranzmana {

  private static final StanjeUPripremiAranzman INSTANCA =
      new StanjeUPripremiAranzman();

  private StanjeUPripremiAranzman() {}

  public static StanjeUPripremiAranzman instanca() {
    return INSTANCA;
  }

  @Override
  public String naziv() {
    return "u pripremi";
  }

  @Override
  public boolean mozePrimatiRezervacije() {
    return true;
  }
}
