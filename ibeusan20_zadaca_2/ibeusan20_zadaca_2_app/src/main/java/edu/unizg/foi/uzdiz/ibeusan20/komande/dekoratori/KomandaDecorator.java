package edu.unizg.foi.uzdiz.ibeusan20.komande.dekoratori;

import edu.unizg.foi.uzdiz.ibeusan20.komande.Komanda;

/**
 * Bazni dekorator za komande
 * <p>
 * Omogućuje dodavanje dodatnog ponašanja oko izvršavanja komandi bez mijenjanja izvornih klasa.
 * </p>
 */
public abstract class KomandaDecorator implements Komanda {

  protected final Komanda omotana;

  protected KomandaDecorator(Komanda omotana) {
    this.omotana = omotana;
  }
}
