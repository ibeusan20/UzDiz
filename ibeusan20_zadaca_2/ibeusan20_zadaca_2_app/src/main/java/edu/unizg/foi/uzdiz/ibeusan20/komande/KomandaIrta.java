package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisRezervacijaAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisniRed;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

// TODO: Auto-generated Javadoc
/**
 * IRTA – pregled rezervacija za turistički aranžman.
 * Sintaksa: IRTA oznakaAranzmana [PA|Č|O|OD]
 *
 * Napomena:
 * - Ako filter nije zadan → ispisuje sve (PAČOD).
 * - OD ne znači O (otkazane); OD su odgođene.
 */
public class KomandaIrta implements Komanda {

  /** The upravitelj rezervacija. */
  private final UpraviteljRezervacijama upraviteljRezervacija;
  
  /** The argumenti. */
  private final String[] argumenti;
  
  /** The tablica. */
  private final TablicniFormat tablica = new TablicniFormat(); // jedan objekt za sve ispise

  /**
   * Instantiates a new komanda irta.
   *
   * @param upraviteljRezervacija the upravitelj rezervacija
   * @param argumenti the argumenti
   */
  public KomandaIrta(UpraviteljRezervacijama upraviteljRezervacija, String... argumenti) {
    this.upraviteljRezervacija = upraviteljRezervacija;
    this.argumenti = argumenti;
  }

  /**
   * Izvrsi.
   *
   * @return true, if successful
   */
  @Override
  public boolean izvrsi() {
    // IRTA mora imati barem oznaku
    if (argumenti == null || argumenti.length < 1) {
      tablica.ispisi("Sintaksa: IRTA <oznakaAranžmana> [PA|Č|O|OD]");
      return true;
    }

    String oznaka = argumenti[0].trim();

    // filter je opcionalan
    String filter = (argumenti.length >= 2 && argumenti[1] != null && !argumenti[1].isBlank())
        ? argumenti[1].toUpperCase().trim()
        : "PAČOD"; // sve

    String komandaTekst = (argumenti.length >= 2)
        ? ("IRTA " + oznaka + " " + filter)
        : ("IRTA " + oznaka);

    String nazivTablice = "Rezervacije za turistički aranžman " + oznaka;

    List<Rezervacija> lista = upraviteljRezervacija.dohvatiZaAranzmanIVrste(oznaka, filter);

    // ako filter NE traži otkazane → sakrij ih (sigurnosno; i ako ih je netko vratio)
    tablica.setIspisujeOtkazane(traziOtkazane(filter));

    List<IspisniRed> redovi = new ArrayList<>();
    for (Rezervacija r : lista) {
      redovi.add(new IspisRezervacijaAdapter(r));
    }

    tablica.ispisiTablicu(komandaTekst, nazivTablice, redovi);

    if (lista.isEmpty()) {
      tablica.ispisi("Nema rezervacija za tražene kriterije.");
      tablica.ispisi("");
    }

    return true;
  }

  /**
   * Vraća true ako filter eksplicitno traži Otkazane.
   * Važno: "OD" NE smije paliti "O".
   *
   * @param filter the filter
   * @return true, if successful
   */
  private boolean traziOtkazane(String filter) {
    if (filter == null) return false;
    String f = filter.toUpperCase();
    for (int i = 0; i < f.length(); i++) {
      if (f.charAt(i) == 'O') {
        boolean jeOD = (i + 1 < f.length() && f.charAt(i + 1) == 'D');
        if (!jeOD) return true;
      }
    }
    return false;
  }
}
