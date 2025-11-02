package logika;

import model.Aranzman;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UpraviteljAranzmanima {

  private final List<Aranzman> aranzmani = new ArrayList<>();

  public UpraviteljAranzmanima(List<Aranzman> pocetni) {
    if (pocetni != null) {
      aranzmani.addAll(pocetni);
    }
  }

  public int brojAranzmana() {
    return aranzmani.size();
  }

  public Aranzman pronadiPoOznaci(String oznaka) {
    for (Aranzman a : aranzmani) {
      if (a.getOznaka() != null && a.getOznaka().equalsIgnoreCase(oznaka)) {
        return a;
      }
    }
    return null;
  }

  public List<Aranzman> svi() {
    return new ArrayList<>(aranzmani);
  }

  public List<Aranzman> filtrirajPoRasponu(LocalDate od, LocalDate do_) {
    List<Aranzman> rezultat = new ArrayList<>();
    for (Aranzman a : aranzmani) {
      LocalDate poc = a.getPocetniDatum();
      if (poc == null)
        continue;
      boolean unutar =
          (poc.isEqual(od) || poc.isAfter(od)) && (poc.isEqual(do_) || poc.isBefore(do_));
      if (unutar)
        rezultat.add(a);
    }
    return rezultat;
  }

}
