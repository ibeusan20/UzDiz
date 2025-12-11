package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.PomocnikDatum;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

/**
 * ITAS – ispis statističkih podataka za aranžmane.
 */
public class KomandaItas implements Komanda {

  private final UpraviteljAranzmanima uprAranz;
  private final String[] argumenti;

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
        System.out.println("Neispravan format datuma. Koristi dd.MM.yyyy.");
        return true;
      }
    } else if (argumenti.length != 0) {
      System.out.println("Sintaksa: ITAS [od do]");
      return true;
    }

    List<Aranzman> kandidati = (od == null)
        ? uprAranz.svi()
        : uprAranz.filtrirajPoRasponu(od, d0);

    if (kandidati.isEmpty()) {
      System.out.println("Nema aranžmana za zadano razdoblje.");
      return true;
    }

    // sortiranje po oznaci radi preglednosti
    // kandidati = new ArrayList<>(kandidati);
    // kandidati.sort(Comparator.comparing(Aranzman::getOznaka));

    System.out.println();
    if (od == null) {
      System.out.println("ITAS");
    } else {
      System.out.println("ITAS " + argumenti[0] + " " + argumenti[1]);
    }
    System.out.println(
        "Oznaka | Ukupno | Aktivne | Cekanje | Odgodene | Otkazane | Prihod");

    for (Aranzman a : kandidati) {
      int ukupno = 0;
      int aktivne = 0;
      int cekanje = 0;
      int odgodene = 0;
      int otkazane = 0;

      for (Rezervacija r : a.getRezervacije()) {
        String s = r.nazivStanja().toUpperCase();
        ukupno++;

        if (s.contains("AKTIV")) {
          aktivne++;
        } else if (s.contains("ČEKAN")) {
          cekanje++;
        } else if (s.contains("ODGOĐ") || s.contains("ODGOD")) {
          odgodene++;
        } else if (s.contains("OTKAZ")) {
          otkazane++;
        }
      }

      double prihod = aktivne * a.getCijena();

      System.out.printf("%6s | %7d | %8d | %7d | %9d | %9d | %10.2f%n",
          a.getOznaka(), ukupno, aktivne, cekanje, odgodene, otkazane, prihod);
    }

    return true;
  }
}
