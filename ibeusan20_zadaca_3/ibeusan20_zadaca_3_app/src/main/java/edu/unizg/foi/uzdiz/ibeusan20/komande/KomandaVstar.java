package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.spremiste.AranzmanMemento;
import edu.unizg.foi.uzdiz.ibeusan20.spremiste.SpremisteAranzmana;

public class KomandaVstar implements Komanda {

  private final UpraviteljAranzmanima ua;
  private final String[] argumenti;
  private final TablicniFormat ispis = new TablicniFormat();

  public KomandaVstar(UpraviteljAranzmanima ua, String... argumenti) {
    this.ua = ua;
    this.argumenti = argumenti == null ? new String[0] : argumenti;
  }

  @Override
  public boolean izvrsi() {
    if (argumenti.length < 1) {
      ispis.ispisi("Sintaksa: VSTAR <oznaka>");
      return true;
    }

    String oznaka = argumenti[0].trim();
    ispis.ispisi("VSTAR " + oznaka);

    Aranzman a = ua.pronadiPoOznaci(oznaka);
    if (a == null) {
      ispis.ispisi("Ne postoji aranžman s oznakom: " + oznaka);
      return true;
    }

    AranzmanMemento m = SpremisteAranzmana.instanca().uzmiZadnji(a.getOznaka());
    if (m == null) {
      ispis.ispisi("Nema spremljenog stanja za aranžman " + a.getOznaka() + ".");
      return true;
    }

    SpremisteAranzmana.instanca().vratiStanje(a, m);

    int preostalo = SpremisteAranzmana.instanca().brojSpremljenih(a.getOznaka());
    ispis.ispisi("Vraćeno zadnje spremljeno stanje za aranžman " + a.getOznaka()
        + " (rezervacija: " + a.getRezervacije().size() + "). Preostalo u spremištu: " + preostalo);
    return true;
  }
}
