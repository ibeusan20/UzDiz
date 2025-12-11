package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

/**
 * Komanda ORTA - otkaz rezervacije turističkog aranžmana.
 */
public class KomandaOrta implements Komanda {
  private final UpraviteljRezervacijama upraviteljRezervacija;
  private final UpraviteljAranzmanima upraviteljAranzmanima;
  private final String[] argumenti;

  /**
   * Instancira novu komandu orta.
   *
   * @param upraviteljRezervacija the upravitelj rezervacija
   * @param upraviteljAranzmanima the upravitelj aranzmanima
   * @param argumenti the argumenti
   */
  public KomandaOrta(UpraviteljRezervacijama upraviteljRezervacija,
      UpraviteljAranzmanima upraviteljAranzmanima, String... argumenti) {
    this.upraviteljRezervacija = upraviteljRezervacija;
    this.upraviteljAranzmanima = upraviteljAranzmanima;
    this.argumenti = argumenti;
  }

  /**
   * Izvrsi.
   *
   * @return true, if successful
   */
  @Override
  public boolean izvrsi() {
    if (argumenti.length < 3) {
      System.out.println("Sintaksa: ORTA <ime> <prezime> <oznakaAranzmana>");
      return true;
    }

    String ime = argumenti[0].trim();
    String prezime = argumenti[1].trim();
    String oznaka = argumenti[2].trim();

    boolean uspjeh = upraviteljRezervacija.otkaziRezervaciju(ime, prezime, oznaka);

    if (!uspjeh) {
      System.out
          .println("Nije pronađena rezervacija za " + ime + " " + prezime + " (" + oznaka + ").");
      return true;
    }

    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznaka);
    if (a != null) {
      upraviteljRezervacija.rekalkulirajZaAranzman(oznaka, a.getMinPutnika(), a.getMaxPutnika());
    }

    System.out
        .println("Uspješno otkazana rezervacija za " + ime + " " + prezime + " (" + oznaka + ").");
    return true;
  }
}
