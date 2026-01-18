package edu.unizg.foi.uzdiz.ibeusan20.aplikacija.unos;

public final class ObradeniUnos {
  private final String naredba;
  private final String[] argumenti;
  private final boolean izlaz;
  private final boolean ignoriraj;
  private final String poruka;

  private ObradeniUnos(String naredba, String[] argumenti, boolean izlaz, boolean ignoriraj,
      String poruka) {
    this.naredba = naredba;
    this.argumenti = argumenti == null ? new String[0] : argumenti;
    this.izlaz = izlaz;
    this.ignoriraj = ignoriraj;
    this.poruka = poruka;
  }

  public static ObradeniUnos izlaz() {
    return new ObradeniUnos(null, new String[0], true, false, null);
  }

  public static ObradeniUnos ignoriraj() {
    return new ObradeniUnos(null, new String[0], false, true, null);
  }

  public static ObradeniUnos greska(String poruka) {
    return new ObradeniUnos(null, new String[0], false, false, poruka);
  }

  public static ObradeniUnos ok(String naredba, String[] argumenti) {
    return new ObradeniUnos(naredba, argumenti, false, false, null);
  }

  public String getNaredba() {
    return naredba;
  }

  public String[] getArgumenti() {
    return argumenti.clone();
  }

  public boolean jeIzlaz() {
    return izlaz;
  }

  public boolean jeIgnoriraj() {
    return ignoriraj;
  }

  public String getPoruka() {
    return poruka;
  }
}
