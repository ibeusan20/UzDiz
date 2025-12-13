package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.PomocnikDatum;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisStatistikaAranzmanaAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisTekstAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisniRed;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

public class KomandaItas implements Komanda {

  private final UpraviteljAranzmanima uprAranz;
  private final String[] argumenti;
  private final FormatIspisaBridge ispis = new TablicniFormat();

  public KomandaItas(UpraviteljAranzmanima uprAranz, String... argumenti) {
    this.uprAranz = uprAranz;
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    LocalDate od = null;
    LocalDate d0 = null;

    if (argumenti.length == 2) {
      od = PomocnikDatum.procitajDatum(argumenti[0]);
      d0 = PomocnikDatum.procitajDatum(argumenti[1]);
      if (od == null || d0 == null) {
        ispis.ispisi(new IspisTekstAdapter("Neispravan format datuma. Koristi dd.MM.yyyy."));
        return true;
      }
    } else if (argumenti.length != 0) {
      ispis.ispisi(new IspisTekstAdapter("Sintaksa: ITAS [od do]"));
      return true;
    }

    List<Aranzman> lista = (od == null) ? uprAranz.svi() : uprAranz.filtrirajPoRasponu(od, d0);

    String komandaTekst = (od == null) ? "ITAS" : ("ITAS " + argumenti[0] + " " + argumenti[1]);
    String nazivTablice = "Statistika turističkih aranžmana";

    List<IspisniRed> redovi = new ArrayList<>();
    for (Aranzman a : lista) {
      redovi.add(new IspisStatistikaAranzmanaAdapter(a));
    }

    TablicniFormat tab = new TablicniFormat();
    tab.ispisiTablicu(komandaTekst, nazivTablice, redovi);

    if (lista.isEmpty()) {
      ispis.ispisi(new IspisTekstAdapter("Nema aranžmana za zadano razdoblje. \n"));
    }

    return true;
  }
}
