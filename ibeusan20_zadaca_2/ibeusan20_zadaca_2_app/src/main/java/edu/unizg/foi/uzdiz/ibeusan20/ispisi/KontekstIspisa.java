package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

/**
 * Globalni kontekst za način ispisa (N/S).
 * <p>
 * N = normalno (kronološki, prvo stari pa novi)<br>
 * S = obrnuto (prvo novi pa stari)
 * </p>
 */
public final class KontekstIspisa {

  private static boolean obrnuto = false; // default: N

  private KontekstIspisa() {
  }

  /**
   * Vraća je li trenutno postavljen obrnuti poredak (S).
   *
   * @return true ako je obrnuto, false ako je kronološki
   */
  public static boolean jeObrnuto() {
    return obrnuto;
  }

  /**
   * Postavlja način ispisa.
   *
   * @param obrnuto true za obrnuti poredak (S), false za normalni (N)
   */
  public static void postaviObrnuto(boolean obrnuto) {
    KontekstIspisa.obrnuto = obrnuto;
  }
}
