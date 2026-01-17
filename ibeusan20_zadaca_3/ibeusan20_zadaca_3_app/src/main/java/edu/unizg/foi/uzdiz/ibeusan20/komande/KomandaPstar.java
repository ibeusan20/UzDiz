package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.memento.AranzmanMemento;
import edu.unizg.foi.uzdiz.ibeusan20.memento.SpremisteAranzmana;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

public class KomandaPstar implements Komanda {

  private final UpraviteljAranzmanima ua;
  private final String[] argumenti;
  private final FormatIspisaBridge ispis = new TablicniFormat();

  public KomandaPstar(UpraviteljAranzmanima ua, String[] argumenti) {
    this.ua = ua;
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    // argumenti sadrže SAMO parametre nakon naredbe
    if (argumenti == null || argumenti.length < 1) {
      ispis.ispisi("Neispravna komanda. Sintaksa: PSTAR oznaka");
      return true;
    }

    String oznaka = argumenti[0];

    Aranzman a = ua.pronadiPoOznaci(oznaka);
    if (a == null) {
      ispis.ispisi("Aranžman '" + oznaka + "' ne postoji.");
      return true;
    }

    SpremisteAranzmana.instanca().spremi(oznaka, AranzmanMemento.from(a));
    ispis.ispisi("Spremljeno stanje aranžmana '" + oznaka + "'.");
    return true;
  }
}
