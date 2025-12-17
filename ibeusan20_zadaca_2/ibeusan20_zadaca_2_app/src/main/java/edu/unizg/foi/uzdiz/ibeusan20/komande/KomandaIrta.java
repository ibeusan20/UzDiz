package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisRezervacijaAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisniRed;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

/**
 * IRTA – pregled rezervacija za turistički aranžman. Sintaksa: IRTA oznakaAranzmana [PA|Č|O|OD]
 *
 * Napomena: - Ako filter nije zadan → ispisuje sve (PAČOD). - OD ne znači O (otkazane); OD su
 * odgođene.
 */
public class KomandaIrta implements Komanda {

  private final UpraviteljRezervacijama upraviteljRezervacija;
  private final String[] argumenti;
  private final TablicniFormat tablica = new TablicniFormat();

  public KomandaIrta(UpraviteljRezervacijama upraviteljRezervacija, String... argumenti) {
    this.upraviteljRezervacija = upraviteljRezervacija;
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    if (argumenti == null || argumenti.length < 1) {
      tablica.ispisi("Sintaksa: IRTA <oznakaAranžmana> [PA|Č|O|OD]");
      return true;
    }

    String oznaka = argumenti[0].trim();

    // filter je opcionalan
    String filter = (argumenti.length >= 2 && argumenti[1] != null && !argumenti[1].isBlank())
        ? argumenti[1].toUpperCase().trim()
        : "PAČODO"; // sve

    String komandaTekst =
        (argumenti.length >= 2) ? ("IRTA " + oznaka + " " + filter) : ("IRTA " + oznaka);

    String nazivTablice = "Rezervacije za turistički aranžman " + oznaka;

    List<Rezervacija> lista = upraviteljRezervacija.dohvatiZaAranzmanIVrste(oznaka, filter);

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

  // Vraća true ako filter eksplicitno traži Otkazane.
  private boolean traziOtkazane(String filter) {
    if (filter == null)
      return false;
    String f = filter.toUpperCase();
    for (int i = 0; i < f.length(); i++) {
      if (f.charAt(i) == 'O') {
        boolean jeOD = (i + 1 < f.length() && f.charAt(i + 1) == 'D');
        if (!jeOD)
          return true;
      }
    }
    return false;
  }
}
