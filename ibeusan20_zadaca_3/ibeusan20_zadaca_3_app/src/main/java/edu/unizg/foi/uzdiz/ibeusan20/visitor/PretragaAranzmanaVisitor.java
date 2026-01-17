package edu.unizg.foi.uzdiz.ibeusan20.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

public class PretragaAranzmanaVisitor implements Posjetitelj {
  private final String rijec;
  private final List<Aranzman> pogodeni = new ArrayList<>();

  public PretragaAranzmanaVisitor(String rijec) {
    this.rijec = Objects.requireNonNullElse(rijec, "");
  }

  @Override
  public void posjetiAranzman(Aranzman a) {
    if (a == null || rijec.isEmpty()) {
      return;
    }
    if (sadrzi(a.getNaziv()) || sadrzi(a.getProgram())) {
      pogodeni.add(a);
    }
  }

  public List<Aranzman> rezultat() {
    return List.copyOf(pogodeni);
  }

  private boolean sadrzi(String t) {
    return t != null && t.contains(rijec);
  }
}
