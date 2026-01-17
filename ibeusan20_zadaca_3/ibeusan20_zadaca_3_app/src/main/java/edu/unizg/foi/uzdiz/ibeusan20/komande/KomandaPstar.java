package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.spremiste.SpremisteAranzmana;

public class KomandaPstar implements Komanda {

  private final UpraviteljAranzmanima ua;
  private final String[] argumenti;
  private final TablicniFormat ispis = new TablicniFormat();

  public KomandaPstar(UpraviteljAranzmanima ua, String... argumenti) {
    this.ua = ua;
    this.argumenti = argumenti == null ? new String[0] : argumenti;
  }

  @Override
  public boolean izvrsi() {
    if (argumenti.length < 1) {
      ispis.ispisi("Sintaksa: PSTAR <oznaka>");
      return true;
    }

    String oznaka = argumenti[0].trim();
    ispis.ispisi("PSTAR " + oznaka);

    Aranzman a = ua.pronadiPoOznaci(oznaka);
    if (a == null) {
      ispis.ispisi("Ne postoji aranžman s oznakom: " + oznaka);
      return true;
    }

    SpremisteAranzmana.instanca().spremi(a);
    int br = SpremisteAranzmana.instanca().brojSpremljenih(a.getOznaka());
    ispis.ispisi("Spremljeno stanje aranžmana " + a.getOznaka() + " (rezervacija: "
        + a.getRezervacije().size() + "). Ukupno spremljenih stanja: " + br);
    return true;
  }
}
