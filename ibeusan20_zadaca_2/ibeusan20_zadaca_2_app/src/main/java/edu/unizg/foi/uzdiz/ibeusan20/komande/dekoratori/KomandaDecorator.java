package edu.unizg.foi.uzdiz.ibeusan20.komande.dekoratori;

import edu.unizg.foi.uzdiz.ibeusan20.komande.Komanda;

// TODO: Auto-generated Javadoc
/**
 * The Class KomandaDecorator.
 */
public abstract class KomandaDecorator implements Komanda {

  /** The omotana. */
  protected final Komanda omotana;

  /**
   * Instantiates a new komanda decorator.
   *
   * @param omotana the omotana
   */
  protected KomandaDecorator(Komanda omotana) {
    this.omotana = omotana;
  }
}
