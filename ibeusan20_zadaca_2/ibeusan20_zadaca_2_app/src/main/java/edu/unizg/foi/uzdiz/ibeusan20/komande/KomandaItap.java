package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisAranzmanDetaljnoAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

public class KomandaItap implements Komanda {

  private final UpraviteljAranzmanima upravitelj;
  private final String[] argumenti;
  private final FormatIspisaBridge ispis = new TablicniFormat();

  public KomandaItap(UpraviteljAranzmanima upravitelj, String... argumenti) {
    this.upravitelj = upravitelj;
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    if (argumenti.length < 1) {
      ispis.ispisi("Nedostaje oznaka aranžmana (ITAP oznaka).");
      return true;
    }

    String oznaka = argumenti[0].trim();
    Aranzman a = upravitelj.pronadiPoOznaci(oznaka);

    ispis.ispisi("ITAP " + oznaka);

    if (a == null) {
      ispis.ispisi("Aranžman s oznakom '" + oznaka + "' nije pronađen.");
      return true;
    }

    ispis.ispisi("Pregled turističkog aranžmana");
    new IspisAranzmanDetaljnoAdapter(a).ispisiDetalje();
    ispis.ispisi("");

    return true;
  }
}
