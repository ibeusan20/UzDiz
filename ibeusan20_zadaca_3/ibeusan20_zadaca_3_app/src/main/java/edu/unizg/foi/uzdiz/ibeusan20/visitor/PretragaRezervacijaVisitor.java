package edu.unizg.foi.uzdiz.ibeusan20.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

public class PretragaRezervacijaVisitor implements Posjetitelj {
  private final String rijec;
  private final List<RezultatPretrageRezervacije> pogodeni = new ArrayList<>();

  public PretragaRezervacijaVisitor(String rijec) {
    this.rijec = Objects.requireNonNullElse(rijec, "");
  }

  @Override
  public void posjetiRezervaciju(Rezervacija r, Aranzman aranzman) {
    if (r == null || rijec.isEmpty()) {
      return;
    }
    if (sadrzi(r.getIme()) || sadrzi(r.getPrezime())) {
      pogodeni.add(new RezultatPretrageRezervacije(r, aranzman));
    }
  }

  public List<RezultatPretrageRezervacije> rezultat() {
    return List.copyOf(pogodeni);
  }

  private boolean sadrzi(String t) {
    return t != null && t.contains(rijec);
  }
}
