package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisTekstAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.KontekstIspisa;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;

/**
 * Komanda IP - postavljanje načina sređivanja podataka kod ispisa tablica.
 * <p>
 * IP N - kronološki (prvo stari, zatim novi)<br>
 * IP S - obrnuto kronološki (prvo novi, zatim stari)
 * </p>
 */
public class KomandaIp implements Komanda {

  private final String[] argumenti;
  private final FormatIspisaBridge ispis = new TablicniFormat();


  public KomandaIp(String... argumenti) {
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    if (argumenti.length < 1) {
      ispis.ispisi(new IspisTekstAdapter("Sintaksa: IP [N|S]"));
      return true;
    }

    String mod = argumenti[0].trim().toUpperCase();

    switch (mod) {
      case "N" -> {
        KontekstIspisa.postaviObrnuto(false);
        ispis.ispisi(new IspisTekstAdapter("Postavljen ispis u kronološkom redoslijedu (N)."));
      }
      case "S" -> {
        KontekstIspisa.postaviObrnuto(true);
        ispis.ispisi(new IspisTekstAdapter("Postavljen ispis u obrnutom kronološkom redu. \n"
            + "slijedu (S)."));
      }
      default -> {
        ispis.ispisi(new IspisTekstAdapter("Nepoznata opcija za IP. Dozvoljeno: N ili S."));
      }
    }

    return true;
  }
}
