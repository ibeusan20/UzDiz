package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;

public class KomandaNepoznata implements Komanda {

  private final String unos;
  private final FormatIspisaBridge ispis = new TablicniFormat();

  public KomandaNepoznata(String unos) {
    this.unos = (unos == null) ? "" : unos.trim();
  }

  @Override
  public boolean izvrsi() {
    ispis.ispisi("Nepoznata komanda: " + unos);
    return true;
  }
}
