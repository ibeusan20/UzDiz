package edu.unizg.foi.uzdiz.ibeusan20.aplikacija;

public class Argumenti {

  private String putanjaAranzmana;    // --ta
  private String putanjaRezervacija;  // --rta

  public Argumenti(String[] args) {
    parsiraj(args);
  }

  private void parsiraj(String[] args) {
    if (args == null) {
      return; // tretiraj kao "nema argumenata"
    }

    for (int i = 0; i < args.length; ) {
      String opt = args[i];

      if (opt == null || opt.isBlank()) {
        i++;
        continue;
      }

      if (!opt.startsWith("--")) {
        throw new IllegalArgumentException("Neispravan argument: '" + opt
            + "'. Očekujem opcije: --ta <datoteka> i/ili --rta <datoteka>.");
      }

      if (!opt.equals("--ta") && !opt.equals("--rta")) {
        throw new IllegalArgumentException("Nepoznata opcija: " + opt);
      }

      if (i + 1 >= args.length) {
        throw new IllegalArgumentException("Nedostaje vrijednost za opciju: " + opt);
      }

      String vrijednost = args[i + 1];
      if (vrijednost.startsWith("--")) {
        throw new IllegalArgumentException("Nedostaje vrijednost za opciju: " + opt);
      }

      if (opt.equals("--ta")) {
        putanjaAranzmana = vrijednost;
      } else { // --rta
        putanjaRezervacija = vrijednost;
      }

      i += 2;
    }
  }

  public boolean imaAranzmane() {
    return putanjaAranzmana != null && !putanjaAranzmana.isBlank();
  }

  public boolean imaRezervacije() {
    return putanjaRezervacija != null && !putanjaRezervacija.isBlank();
  }

  public String dohvatiPutanjuAranzmana() {
    return putanjaAranzmana;
  }

  public String dohvatiPutanjuRezervacija() {
    return putanjaRezervacija;
  }
}
