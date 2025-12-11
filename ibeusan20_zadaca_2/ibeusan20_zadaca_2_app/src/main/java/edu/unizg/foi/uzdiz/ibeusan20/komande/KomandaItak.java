package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.time.LocalDate;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.PomocnikDatum;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisAranzmanaAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

/**
 * Komanda ITAK - ispis svih aranžmana (ili filtriranih po datumu).
 */
public class KomandaItak implements Komanda {
  private final UpraviteljAranzmanima upravitelj;
  private final FormatIspisaBridge formatIspisa = new TablicniFormat();
  private final String[] argumenti;

  /**
   * Instanciranje nove komanda itak.
   *
   * @param upravitelj the upravitelj
   * @param argumenti the argumenti
   */
  public KomandaItak(UpraviteljAranzmanima upravitelj, String... argumenti) {
    this.upravitelj = upravitelj;
    this.argumenti = argumenti;
  }

  /**
   * Izvrsi.
   *
   * @return true, if successful
   */
  @Override
  public boolean izvrsi() {
    LocalDate datumOd = null;
    LocalDate datumDo = null;

    try {
      if (argumenti.length >= 2) {
        datumOd = PomocnikDatum.procitajDatum(argumenti[0]);
        datumDo = PomocnikDatum.procitajDatum(argumenti[1]);
      }
    } catch (Exception e) {
      System.out.println("Neispravan format datuma. Koristi dd.MM.yyyy.");
      return true;
    }

    List<Aranzman> lista =
        (datumOd == null) ? upravitelj.svi() : upravitelj.filtrirajPoRasponu(datumOd, datumDo);

    System.out.println();
    System.out.println("Pregled turističkih aranzmana:");

    if (lista.isEmpty()) {
      System.out.println("Nema aranzmana u zadanom razdoblju.");
    } else {
      for (Aranzman a : lista) {
        IspisAranzmanaAdapter adapter = new IspisAranzmanaAdapter(a);
        formatIspisa.ispisi(adapter);
      }
    }
    return true;
  }
}
