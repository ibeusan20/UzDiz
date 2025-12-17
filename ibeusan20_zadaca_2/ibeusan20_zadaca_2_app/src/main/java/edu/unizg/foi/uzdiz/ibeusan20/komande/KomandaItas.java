package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.PomocnikDatum;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisStatistikaAranzmanaAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisniRed;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

// TODO: Auto-generated Javadoc
/**
 * The Class KomandaItas.
 */
public class KomandaItas implements Komanda {

  /** The upr aranz. */
  private final UpraviteljAranzmanima uprAranz;
  
  /** The argumenti. */
  private final String[] argumenti;
  
  /** The ispis. */
  private final FormatIspisaBridge ispis = new TablicniFormat();

  /**
   * Instantiates a new komanda itas.
   *
   * @param uprAranz the upr aranz
   * @param argumenti the argumenti
   */
  public KomandaItas(UpraviteljAranzmanima uprAranz, String... argumenti) {
    this.uprAranz = uprAranz;
    this.argumenti = argumenti;
  }

  /**
   * Izvrsi.
   *
   * @return true, if successful
   */
  @Override
  public boolean izvrsi() {
    LocalDate od = null;
    LocalDate d0 = null;

    if (argumenti.length == 2) {
      od = PomocnikDatum.procitajDatum(argumenti[0]);
      d0 = PomocnikDatum.procitajDatum(argumenti[1]);
      if (od == null || d0 == null) {
        ispis.ispisi("Neispravan format datuma. Koristi dd.MM.yyyy.");
        return true;
      }
    } else if (argumenti.length != 0) {
      ispis.ispisi("Sintaksa: ITAS [od do]");
      return true;
    }

    List<Aranzman> lista = (od == null)
        ? uprAranz.sviZaIspis()
        : uprAranz.filtrirajPoRasponuZaIspis(od, d0);
    
    String komandaTekst = (od == null) ? "ITAS" : ("ITAS " + argumenti[0] + " " + argumenti[1]);
    String nazivTablice = "Statistika turističkih aranžmana";

    List<IspisniRed> redovi = new ArrayList<>();
    for (Aranzman a : lista) {
      redovi.add(new IspisStatistikaAranzmanaAdapter(a));
    }

    TablicniFormat tab = new TablicniFormat();
    tab.ispisiTablicu(komandaTekst, nazivTablice, redovi);

    if (lista.isEmpty()) {
      ispis.ispisi("Nema aranžmana za zadano razdoblje. \n");
    }

    return true;
  }
}
