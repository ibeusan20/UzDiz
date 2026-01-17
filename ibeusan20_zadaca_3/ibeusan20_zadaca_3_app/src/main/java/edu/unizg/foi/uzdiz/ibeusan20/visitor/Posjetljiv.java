package edu.unizg.foi.uzdiz.ibeusan20.visitor;

import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

public interface Posjetljiv {
  void prihvati(Posjetitelj p, Aranzman kontekst);
}
