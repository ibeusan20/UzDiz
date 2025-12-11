package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.ispisi.KontekstIspisa;

/**
 * Komanda IP - postavljanje načina sređivanja podataka kod ispisa tablica.
 * <p>
 * IP N - kronološki (prvo stari, zatim novi)<br>
 * IP S - obrnuto kronološki (prvo novi, zatim stari)
 * </p>
 */
public class KomandaIp implements Komanda {

  private final String[] argumenti;

  public KomandaIp(String... argumenti) {
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    if (argumenti.length < 1) {
      System.out.println("Sintaksa: IP [N|S]");
      return true;
    }

    String mod = argumenti[0].trim().toUpperCase();

    switch (mod) {
      case "N" -> {
        KontekstIspisa.postaviObrnuto(false);
        System.out.println("Postavljen ispis u kronološkom redoslijedu (N).");
      }
      case "S" -> {
        KontekstIspisa.postaviObrnuto(true);
        System.out.println("Postavljen ispis u obrnutom kronološkom redoslijedu (S).");
      }
      default -> {
        System.out.println("Nepoznata opcija za IP. Dozvoljeno: N ili S.");
      }
    }

    return true;
  }
}
