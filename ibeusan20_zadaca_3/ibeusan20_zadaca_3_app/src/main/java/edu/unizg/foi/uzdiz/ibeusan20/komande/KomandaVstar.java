package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.memento.AranzmanMemento;
import edu.unizg.foi.uzdiz.ibeusan20.memento.SpremisteAranzmana;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

public class KomandaVstar implements Komanda {

  private final UpraviteljAranzmanima ua;
  private final String[] argumenti;

  public KomandaVstar(UpraviteljAranzmanima ua, String[] argumenti) {
    this.ua = ua;
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    if (argumenti == null || argumenti.length < 1) {
      System.out.println("Neispravna komanda. Sintaksa: VSTAR oznaka");
      return true;
    }

    String oznaka = argumenti[0];

    // KLJUČ: ne tražimo u listi, nego vraćamo iz spremišta
    AranzmanMemento m = SpremisteAranzmana.instanca().zadnji(oznaka);
    if (m == null) {
      System.out.println("Nema spremljenog stanja za aranžman '" + oznaka + "'.");
      return true;
    }

    Aranzman obnovljen = m.restore();

    // vrati aranžman u upravitelja čak i nakon BP A
    ua.dodajIliZamijeni(obnovljen);

    System.out.println("Vraćeno spremljeno stanje aranžmana '" + oznaka + "'.");
    return true;
  }
}
