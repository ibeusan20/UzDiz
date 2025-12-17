package edu.unizg.foi.uzdiz.ibeusan20.komande;

import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

// TODO: Auto-generated Javadoc
/**
 * Komanda ORTA - otkaz rezervacije turističkog aranžmana.
 */
public class KomandaOrta implements Komanda {
  
  /** The upravitelj rezervacija. */
  private final UpraviteljRezervacijama upraviteljRezervacija;
  
  /** The upravitelj aranzmanima. */
  private final UpraviteljAranzmanima upraviteljAranzmanima;
  
  /** The argumenti. */
  private final String[] argumenti;
  
  /** The ispis. */
  private final FormatIspisaBridge ispis = new TablicniFormat();


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
      ispis.ispisi("Sintaksa: ORTA <ime> <prezime> <oznakaAranzmana>");
      return true;
    }

    String ime = argumenti[0].trim();
    String prezime = argumenti[1].trim();
    String oznaka = argumenti[2].trim();
    
    ispis.ispisi("ORTA " + ime + " " + prezime + " " + oznaka);

    boolean uspjeh = upraviteljRezervacija.otkaziRezervaciju(ime, prezime, oznaka);

    if (!uspjeh) {
      ispis.ispisi("Nije pronađena rezervacija za " + ime + " " + prezime + " (" + oznaka + ").\n");
      return true;
    }

    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznaka);
    if (a != null) {
      // upraviteljRezervacija.rekalkulirajZaAranzman(oznaka, a.getMinPutnika(), a.getMaxPutnika());
      upraviteljRezervacija.rekalkulirajSve();
    }

    ispis.ispisi("Uspješno otkazana rezervacija za " + ime + " " + prezime + " (" + oznaka + ").\n");
    return true;
  }
}
