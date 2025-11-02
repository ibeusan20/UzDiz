package ispisi;

/**
 * Implementacija Bridge uzorka za tablični ispis. Može ispisivati i aranžmane i rezervacije.
 */
public class TablicniFormat implements FormatIspisaBridge {

  private boolean zaglavljeIspisano = false;
  private boolean ispisujeOtkazane = false;

  public void setIspisujeOtkazane(boolean vrijednost) {
    this.ispisujeOtkazane = vrijednost;
  }

  public void reset() {
    this.zaglavljeIspisano = false;
  }


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

  private void ispisiAranzman(IspisAranzmanaAdapter a) {
    if (!zaglavljeIspisano) {
      ispisiZaglavljeAranzmana();
      zaglavljeIspisano = true;
    }

    System.out.printf("%-4s %-35s %-12s %-12s %-8s %-8s %10s € %6d %6d%n", a.getOznaka(),
        skrati(a.getNaziv(), 35), a.getDatumOd(), a.getDatumDo(), a.getVrijemeKretanja(),
        a.getVrijemePovratka(), a.getCijena(), a.getMinPutnika(), a.getMaxPutnika());
  }

  private void ispisiZaglavljeAranzmana() {
    System.out.println(
        "---------------------------------------------------------------------------------------------------------------");
    System.out.printf("%-4s %-35s %-12s %-12s %-8s %-8s %10s %8s %8s%n", "OZN", "NAZIV", "POČETAK",
        "KRAJ", "KRET.", "POVR.", "CIJENA", "MIN", "MAKS");
    System.out.println(
        "---------------------------------------------------------------------------------------------------------------");
  }

  private void ispisiRezervaciju(IspisRezervacijaAdapter r) {
    // ako imamo barem jednu otkazanu, dodaj dodatni stupac
    if (!zaglavljeIspisano) {
      if (r.getVrsta().equals("O"))
        ispisujeOtkazane = true;
      ispisiZaglavljeRezervacija();
      zaglavljeIspisano = true;
    }

    if (ispisujeOtkazane) {
      System.out.printf("%-12s %-12s %-20s %-5s %-20s%n", r.getIme(), r.getPrezime(),
          r.getDatumVrijeme(), r.getVrsta(), r.getDatumVrijemeOtkaza());
    } else {
      System.out.printf("%-12s %-12s %-20s %-5s%n", r.getIme(), r.getPrezime(), r.getDatumVrijeme(),
          r.getVrsta());
    }
  }

  private void ispisiZaglavljeRezervacija() {
    System.out.println("------------------------------------------------------------");
    if (ispisujeOtkazane) {
      System.out.printf("%-12s %-12s %-20s %-5s %-20s%n", "IME", "PREZIME", "DATUM I VRIJEME",
          "VRSTA", "DATUM OTKAZA");
    } else {
      System.out.printf("%-12s %-12s %-20s %-5s%n", "IME", "PREZIME", "DATUM I VRIJEME", "VRSTA");
    }
    System.out.println("------------------------------------------------------------");
  }

  private void ispisiRezervacijuOsobe(IspisRezervacijaOsobeAdapter ro) {
    if (!zaglavljeIspisano) {
      ispisiZaglavljeRezervacijaOsobe();
      zaglavljeIspisano = true;
    }

    System.out.printf("%-20s %-8s %-35s %-5s%n", ro.getDatumVrijeme(), ro.getOznaka(),
        skrati(ro.getNazivAranzmana(), 35), ro.getVrsta());
  }

  private void ispisiZaglavljeRezervacijaOsobe() {
    System.out.println(
        "------------------------------------------------------------------------------------------");
    System.out.printf("%-20s %-8s %-35s %-5s%n", "DATUM I VRIJEME", "OZN", "NAZIV ARANŽMANA",
        "VRSTA");
    System.out.println(
        "------------------------------------------------------------------------------------------");
  }


  private String skrati(String tekst, int max) {
    if (tekst == null)
      return "";
    if (tekst.length() <= max)
      return tekst;
    return tekst.substring(0, max - 3) + "...";
  }
}
