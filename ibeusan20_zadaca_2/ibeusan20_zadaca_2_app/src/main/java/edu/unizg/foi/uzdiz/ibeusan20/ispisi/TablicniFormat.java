package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

/**
 * Jednostavan tablični ispis u konzolu.
 */
public class TablicniFormat implements FormatIspisaBridge {

  private boolean zaglavljeIspisano = false;
  private boolean ispiseOtkazane = true;

  /**
   * Ako je false, redovi sa stanjem "OTKAZANA" se ne ispisuju.
   * Koristi se u IRTA komandi.
   */
  public void setIspisujeOtkazane(boolean vrijednost) {
    this.ispiseOtkazane = vrijednost;
  }

  @Override
  public void ispisi(IspisniRed red) {
    if (red == null) {
      return;
    }

    String[] vrijednosti = red.vrijednosti();
    String[] zaglavlje = red.zaglavlje();

    if (!zaglavljeIspisano && zaglavlje != null && zaglavlje.length > 0) {
      ispisiRed(zaglavlje);
      ispisiSeparator(zaglavlje.length);
      zaglavljeIspisano = true;
    }

    // jednostavna provjera: zadnji stupac je naziv stanja
    if (!ispiseOtkazane && vrijednosti != null && vrijednosti.length > 0) {
      String zadnje = vrijednosti[vrijednosti.length - 1];
      if (zadnje != null && zadnje.toUpperCase().contains("OTKAZ")) {
        return;
      }
    }

    ispisiRed(vrijednosti);
  }

  private void ispisiRed(String[] polja) {
    if (polja == null) {
      return;
    }
    StringBuilder sb = new StringBuilder();
    for (String p : polja) {
      sb.append(String.format("%-20s", p == null ? "" : p));
    }
    System.out.println(sb.toString());
  }

  private void ispisiSeparator(int stupci) {
    int sirina = stupci * 20;
    System.out.println("-".repeat(Math.max(10, sirina)));
  }
}
