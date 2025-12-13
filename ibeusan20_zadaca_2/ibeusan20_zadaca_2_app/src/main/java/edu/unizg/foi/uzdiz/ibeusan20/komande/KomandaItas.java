package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.time.LocalDate;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.PomocnikDatum;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisItasAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisTekstAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

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

    List<Aranzman> kandidati = (od == null)
        ? uprAranz.svi()
        : uprAranz.filtrirajPoRasponu(od, d0);

    if (kandidati.isEmpty()) {
      ispis.ispisi(new IspisTekstAdapter("Nema aranžmana za zadano razdoblje."));
      return true;
    }

    ispis.ispisi(new IspisTekstAdapter("ITAS" + (od == null ? "" : " " + argumenti[0] + " " + argumenti[1])));

    for (Aranzman a : kandidati) {
      int ukupno = 0, aktivne = 0, cekanje = 0, odgodene = 0, otkazane = 0;

      for (Rezervacija r : a.getRezervacije()) {
        ukupno++;
        String s = (r.nazivStanja() == null) ? "" : r.nazivStanja().toUpperCase();

        if (s.contains("AKTIV")) aktivne++;
        else if (s.contains("ČEKAN") || s.contains("CEKAN")) cekanje++;
        else if (s.contains("ODGOĐ") || s.contains("ODGOD")) odgodene++;
        else if (s.contains("OTKAZ")) otkazane++;
      }

      double prihod = aktivne * a.getCijena();
      ispis.ispisi(new IspisItasAdapter(a.getOznaka(), ukupno, aktivne, cekanje, odgodene, otkazane, prihod));
    }

    return true;
  }
}
