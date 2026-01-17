package edu.unizg.foi.uzdiz.ibeusan20.aplikacija;

public class Argumenti {

  private String putanjaAranzmana; // --ta
  private String putanjaRezervacija; // --rta

  private boolean jdr; // --jdr
  private boolean vdr; // --vdr

  public Argumenti(String[] args) {
    parsiraj(args);
    provjeriStrategije();
  }

  private void parsiraj(String[] args) {
    if (args == null) {
      return;
    }

    for (int i = 0; i < args.length; i++) {
      String opt = args[i];

      if (opt == null || opt.isBlank()) {
        continue;
      }

      if (!opt.startsWith("--")) {
        throw new IllegalArgumentException("Neispravan argument: '" + opt + "'.");
      }

      if (opt.equals("--ta")) {
        i = obradiOpcijuSaVrijednoscu(args, i, true);
        continue;
      }

      if (opt.equals("--rta")) {
        i = obradiOpcijuSaVrijednoscu(args, i, false);
        continue;
      }

      if (opt.equals("--jdr")) {
        jdr = true;
        continue;
      }

      if (opt.equals("--vdr")) {
        vdr = true;
        continue;
      }

      throw new IllegalArgumentException("Nepoznata opcija: " + opt);
    }
  }

  private int obradiOpcijuSaVrijednoscu(String[] args, int i, boolean jeTa) {
    if (i + 1 >= args.length) {
      throw new IllegalArgumentException("Nedostaje vrijednost za opciju: " + args[i]);
    }

    String vrijednost = args[i + 1];
    if (vrijednost == null || vrijednost.isBlank() || vrijednost.startsWith("--")) {
      throw new IllegalArgumentException("Nedostaje vrijednost za opciju: " + args[i]);
    }

    if (jeTa) {
      putanjaAranzmana = vrijednost;
    } else {
      putanjaRezervacija = vrijednost;
    }

    return i + 1;
  }

  private void provjeriStrategije() {
    if (jdr && vdr) {
      throw new IllegalArgumentException("Opcije --jdr i --vdr se ne smiju koristiti zajedno.");
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

  public boolean jeJdr() {
    return jdr;
  }

  public boolean jeVdr() {
    return vdr;
  }
}
