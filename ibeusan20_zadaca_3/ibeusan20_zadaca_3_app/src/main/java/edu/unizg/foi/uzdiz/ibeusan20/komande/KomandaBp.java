package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;

/**
 * BP – brisanje podataka.
 * <p>
 * Podržane varijante:
 * </p>
 * <ul>
 * <li>{@code BP A} – fizičko brisanje svih aranžmana</li>
 * <li>{@code BP R} – fizičko brisanje svih rezervacija</li>
 * </ul>
 */
public class KomandaBp implements Komanda {

  private final UpraviteljAranzmanima uprAranz;
  private final UpraviteljRezervacijama uprRez;
  private final String[] argumenti;
  private final FormatIspisaBridge ispis = new TablicniFormat();

  public KomandaBp(UpraviteljAranzmanima uprAranz, UpraviteljRezervacijama uprRez,
      String... argumenti) {
    this.uprAranz = uprAranz;
    this.uprRez = uprRez;
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    if (argumenti.length < 1) {
      ispis.ispisi("Sintaksa: BP [A|R]");
      return true;
    }

    String mod = argumenti[0].trim().toUpperCase();
    ispis.ispisi("BP" + mod);

    switch (mod) {
      case "A" -> obrisiSveAranzmaneFizicki();
      case "R" -> obrisiSveRezervacijeFizicki();
      default -> ispis.ispisi("Neispravan argument za BP. Dozvoljeno je A ili R.");
    }

    return true;
  }

  private void obrisiSveAranzmaneFizicki() {
    int prije = uprAranz.svi().size();

    if (prije == 0) {
      ispis.ispisi("Nema aranžmana za brisanje.");
      return;
    }

    // fizičko brisanje aranžmana
    int obrisano = uprAranz.obrisiSveAranzmaneFizicki();

    ispis.ispisi("Fizički obrisani svi aranžmani: " + obrisano);
  }

  private void obrisiSveRezervacijeFizicki() {
    int ukupnoRez = uprRez.brojRezervacija();
    if (ukupnoRez == 0) {
      ispis.ispisi("Nema rezervacija za brisanje.");
      return;
    }

    int obrisano = uprRez.obrisiSveRezervacijeFizicki();
    ispis.ispisi("Fizički obrisane sve rezervacije: " + obrisano);
  }
}
