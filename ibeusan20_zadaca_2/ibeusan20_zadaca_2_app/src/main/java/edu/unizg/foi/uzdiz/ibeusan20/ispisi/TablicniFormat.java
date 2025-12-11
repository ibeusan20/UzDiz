package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

/**
 * Implementacija Bridge uzorka za tablični ispis.
 * <p>
 * Omogućuje ispis više vrsta podataka (aranžmani, rezervacije) koristeći njihove adaptere bez
 * poznavanja detalja strukture podataka.
 * </p>
 */
public class TablicniFormat implements FormatIspisaBridge {
  private boolean zaglavljeIspisano = false;
  private boolean ispisujeOtkazane = false;

  /**
   * Postavlja oznaku treba li ispisivati stupac s otkazanim rezervacijama.
   *
   * @param vrijednost the new ispisuje otkazane
   */
  public void setIspisujeOtkazane(boolean vrijednost) {
    this.ispisujeOtkazane = vrijednost;
  }

  /** Resetira stanje ispisa (npr. prije novog bloka ispisa). */
  public void reset() {
    this.zaglavljeIspisano = false;
  }

  /**
   * Ispisuje podatke ovisno o tipu adaptera.
   */
  @Override
  public void ispisi(Object adapter) {
    if (adapter instanceof IspisAranzmanaAdapter a) {
      ispisiAranzman(a);
    } else if (adapter instanceof IspisRezervacijaAdapter r) {
      ispisiRezervaciju(r);
    } else if (adapter instanceof IspisRezervacijaOsobeAdapter ro) {
      ispisiRezervacijuOsobe(ro);
    }
  }

  /**
   * Ispisi aranzman.
   *
   * @param a the a
   */
  private void ispisiAranzman(IspisAranzmanaAdapter a) {
    if (!zaglavljeIspisano) {
      ispisiZaglavljeAranzmana();
      zaglavljeIspisano = true;
    }

    System.out.printf("%-4s %-35s %-12s %-12s %-8s %-8s %10s %6d %6d%n", a.getOznaka(),
        skrati(a.getNaziv(), 35), a.getDatumOd(), a.getDatumDo(), a.getVrijemeKretanja(),
        a.getVrijemePovratka(), a.getCijena(), a.getMinPutnika(), a.getMaxPutnika());
  }

  /**
   * Ispisi zaglavlje aranzmana.
   */
  private void ispisiZaglavljeAranzmana() {
    ispisiLiniju(111);
    System.out.printf("%-4s %-35s %-12s %-12s %-8s %-8s %10s %8s %8s%n", "OZN", "NAZIV", "POČETAK",
        "KRAJ", "KRET.", "POVR.", "CIJENA", "MIN", "MAKS");
    ispisiLiniju(111);
  }


  /**
   * Ispisi zaglavlje rezervacija.
   */
  private void ispisiZaglavljeRezervacija() {
    ispisiLiniju(80);
    if (ispisujeOtkazane) {
      System.out.printf("%-12s %-12s %-20s %-15s %-20s%n", "IME", "PREZIME", "DATUM I VRIJEME",
          "VRSTA", "DATUM OTKAZA");
    } else {
      System.out.printf("%-12s %-12s %-20s %-15s%n", "IME", "PREZIME", "DATUM I VRIJEME", "VRSTA");
    }
    ispisiLiniju(80);
  }

  /**
   * Ispisi rezervaciju.
   *
   * @param r the r
   */
  private void ispisiRezervaciju(IspisRezervacijaAdapter r) {
    if (!zaglavljeIspisano) {
      if (r.getVrsta().equals("O"))
        ispisujeOtkazane = true;
      ispisiZaglavljeRezervacija();
      zaglavljeIspisano = true;
    }

    if (ispisujeOtkazane) {
      System.out.printf("%-12s %-12s %-20s %-15s %-20s%n", r.getIme(), r.getPrezime(),
          r.getDatumVrijeme(), r.getVrsta(), r.getDatumVrijemeOtkaza());
    } else {
      System.out.printf("%-12s %-12s %-20s %-15s%n", r.getIme(), r.getPrezime(),
          r.getDatumVrijeme(), r.getVrsta());
    }
  }

  /**
   * Ispisi rezervaciju osobe.
   *
   * @param ro the ro
   */
  private void ispisiRezervacijuOsobe(IspisRezervacijaOsobeAdapter ro) {
    if (!zaglavljeIspisano) {
      ispisiZaglavljeRezervacijaOsobe();
      zaglavljeIspisano = true;
    }

    System.out.printf("%-20s %-8s %-35s %-5s%n", ro.getDatumVrijeme(), ro.getOznaka(),
        skrati(ro.getNazivAranzmana(), 35), ro.getVrsta());
  }

  /**
   * Ispisi zaglavlje rezervacija osobe.
   */
  private void ispisiZaglavljeRezervacijaOsobe() {
    ispisiLiniju(80);
    System.out.printf("%-20s %-8s %-35s %-5s%n", "DATUM I VRIJEME", "OZN", "NAZIV ARANŽMANA",
        "VRSTA");
    ispisiLiniju(80);

  }

  /**
   * Skrati.
   *
   * @param tekst the tekst
   * @param max the max
   * @return the string
   */
  private String skrati(String tekst, int max) {
    if (tekst == null)
      return "";
    if (tekst.length() <= max)
      return tekst;
    return tekst.substring(0, max - 3) + "...";
  }

  /**
   * Ispisi liniju.
   *
   * @param duzina the duzina
   */
  private void ispisiLiniju(int duzina) {
    System.out.println("-".repeat(Math.max(0, duzina)));
  }
}
