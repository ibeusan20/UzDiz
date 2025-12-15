package edu.unizg.foi.uzdiz.ibeusan20.komande.dekoratori;

import edu.unizg.foi.uzdiz.ibeusan20.komande.Komanda;

public abstract class KomandaDecorator implements Komanda {

  protected final Komanda omotana;

  protected KomandaDecorator(Komanda omotana) {
    this.omotana = omotana;
  }
}
