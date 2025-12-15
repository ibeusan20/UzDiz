package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.PomocnikDatum;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisAranzmanaAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisniRed;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

/**
 * Komanda ITAK - ispis svih aranžmana (ili filtriranih po datumu).
 */
public class KomandaItak implements Komanda {
  private final UpraviteljAranzmanima upravitelj;
  private final String[] argumenti;
  private final FormatIspisaBridge ispis = new TablicniFormat();


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
        if (datumOd == null || datumDo == null) {
          System.out.println("Neispravan format datuma. Koristi dd.MM.yyyy.");
          return true;
        }
      }
    } catch (Exception e) {
      ispis.ispisi("Neispravan format datuma. Koristi dd.MM.yyyy.");
      return true;
    }

    List<Aranzman> lista = (datumOd == null)
        ? upravitelj.sviZaIspis()
        : upravitelj.filtrirajPoRasponuZaIspis(datumOd, datumDo);
    
    String komandaTekst = (datumOd == null) ? "ITAK" : ("ITAK " + argumenti[0] + " " + argumenti[1]);
    String nazivTablice = "Turistički aranžmani";

    List<IspisniRed> redovi = new ArrayList<>();
    for (Aranzman a : lista) redovi.add(new IspisAranzmanaAdapter(a));

    TablicniFormat tab = new TablicniFormat();
    tab.ispisiTablicu(komandaTekst, nazivTablice, redovi);

    return true;
  }
}
