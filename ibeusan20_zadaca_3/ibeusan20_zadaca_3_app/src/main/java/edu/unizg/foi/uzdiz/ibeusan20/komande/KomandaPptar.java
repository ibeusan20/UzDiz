package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisPretragaAranzmanaAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisPretragaRezervacijaAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisniRed;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.visitor.PretragaAranzmanaVisitor;
import edu.unizg.foi.uzdiz.ibeusan20.visitor.PretragaRezervacijaVisitor;
import edu.unizg.foi.uzdiz.ibeusan20.visitor.RezultatPretrageRezervacije;

public class KomandaPptar implements Komanda {

  private final UpraviteljAranzmanima uprAranz;
  private final String tip; // A ili R
  private final String rijec;

  public KomandaPptar(UpraviteljAranzmanima uprAranz, String tip, String rijec) {
    this.uprAranz = uprAranz;
    this.tip = tip == null ? "" : tip.trim();
    this.rijec = rijec == null ? "" : rijec;
  }

  @Override
  public boolean izvrsi() {
    if (uprAranz == null) {
      return true;
    }
    if (!tip.equals("A") && !tip.equals("R")) {
      System.out.println("Neispravna komanda. Sintaksa: PPTAR [A|R] riječ");
      return true;
    }
    if (rijec.isBlank()) {
      System.out.println("Neispravna komanda. Nedostaje riječ. Sintaksa: PPTAR [A|R] riječ");
      return true;
    }

    TablicniFormat tf = new TablicniFormat();

    if (tip.equals("A")) {
      PretragaAranzmanaVisitor v = new PretragaAranzmanaVisitor(rijec);
      for (Aranzman a : uprAranz.svi()) {
        a.prihvati(v, null);
      }

      List<IspisniRed> redovi = new ArrayList<>();
      for (Aranzman a : v.rezultat()) {
        redovi.add(new IspisPretragaAranzmanaAdapter(a));
      }
      tf.ispisiTablicu("PPTAR A " + rijec, "Pretraga aranžmana", redovi);
      return true;
    }

    PretragaRezervacijaVisitor v = new PretragaRezervacijaVisitor(rijec);
    for (Aranzman a : uprAranz.svi()) {
      a.prihvati(v, null);
    }

    List<IspisniRed> redovi = new ArrayList<>();
    for (RezultatPretrageRezervacije x : v.rezultat()) {
      redovi.add(new IspisPretragaRezervacijaAdapter(x.rezervacija(), x.aranzman()));
    }
    tf.ispisiTablicu("PPTAR R " + rijec, "Pretraga rezervacija", redovi);
    return true;
  }
}
