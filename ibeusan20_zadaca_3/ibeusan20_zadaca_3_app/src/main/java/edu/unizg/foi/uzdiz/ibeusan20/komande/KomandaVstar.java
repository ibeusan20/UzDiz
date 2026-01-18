package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.memento.AranzmanMemento;
import edu.unizg.foi.uzdiz.ibeusan20.memento.SpremisteAranzmana;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

public class KomandaVstar implements Komanda {

  private final UpraviteljAranzmanima ua;
  private final String[] argumenti;
  private final FormatIspisaBridge ispis = new TablicniFormat();

  public KomandaVstar(UpraviteljAranzmanima ua, String[] argumenti) {
    this.ua = ua;
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    if (argumenti == null || argumenti.length < 1) {
      ispis.ispisi("Neispravna komanda. Sintaksa: VSTAR oznaka");
      return true;
    }

    String oznaka = argumenti[0];
    
    ispis.ispisi("VSTAR " + oznaka);

    //  ne tražimo u listi, nego vraćamo iz spremišta
    AranzmanMemento m = SpremisteAranzmana.instanca().zadnji(oznaka);
    if (m == null) {
      ispis.ispisi("Nema spremljenog stanja za aranžman '" + oznaka + "'.");
      return true;
    }

    Aranzman obnovljen = m.restore();

    // vraća aranžman u upravitelja
    ua.dodajIliZamijeni(obnovljen);

    ispis.ispisi("Vraćeno spremljeno stanje aranžmana '" + oznaka + "'.");
    return true;
  }
}
