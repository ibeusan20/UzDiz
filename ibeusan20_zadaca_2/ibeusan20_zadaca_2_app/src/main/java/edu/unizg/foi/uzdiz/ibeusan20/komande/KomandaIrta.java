package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisRezervacijaAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisTekstAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisniRed;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

/**
 * Komanda IRTA - ispis rezervacija za aranžman (s mogućim filtriranjem po statusu).
 */
public class KomandaIrta implements Komanda {
  private final UpraviteljRezervacijama upraviteljRezervacija;
  private final TablicniFormat formatIspisa = new TablicniFormat();
  private final String[] argumenti;
  private final FormatIspisaBridge ispis = new TablicniFormat();

  /**
   * Instancira novu koamndu irta.
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
    if (argumenti.length < 2) {
      ispis.ispisi(new IspisTekstAdapter("Sintaksa: IRTA <oznakaAranžmana> [PA|Č|O|OD]"));
      return true;
    }

    String oznaka = argumenti[0].trim();
    String filter = (argumenti.length == 2) ? argumenti[1].trim() : "PAČOOD"; // ako nema -> sve

    String komandaTekst = (argumenti.length == 2) ? ("IRTA " + oznaka + " " + filter) : ("IRTA " + oznaka);
    String nazivTablice = "Rezervacije za turistički aranžman " + oznaka;

    List<Rezervacija> lista = upraviteljRezervacija.dohvatiZaAranzmanIVrste(oznaka, filter);

    List<IspisniRed> redovi = new ArrayList<>();
    for (Rezervacija r : lista) redovi.add(new IspisRezervacijaAdapter(r));

    formatIspisa.ispisiTablicu(komandaTekst, nazivTablice, redovi);

    if (lista.isEmpty()) {
      ispis.ispisi(new IspisTekstAdapter("Nema rezervacija za tražene kriterije."));
      ispis.ispisi(new IspisTekstAdapter(""));
    }

    return true;
  }
}
