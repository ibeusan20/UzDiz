package edu.unizg.foi.uzdiz.ibeusan20.visitor;

import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

public interface Posjetitelj {
  default void posjetiAranzman(Aranzman a) {}

  default void posjetiRezervaciju(Rezervacija r, Aranzman aranzman) {}
}
