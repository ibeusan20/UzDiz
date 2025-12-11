package edu.unizg.foi.uzdiz.ibeusan20.aplikacija;

import java.util.List;

import edu.unizg.foi.uzdiz.ibeusan20.datoteke.UcitavacFactory;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.UcitavacPodataka;
import edu.unizg.foi.uzdiz.ibeusan20.komande.Komande;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

/**
 * Glavna klasa aplikacije Turistička agencija.
 * <p>
 * Učitava podatke o aranžmanima i rezervacijama pomoću {@link UcitavacFactory},
 * inicijalizira upravitelje logike te pokreće konzolno sučelje za unos komandi.
 * </p>
 */
public class Aplikacija {

  /**
   * Ulazna točka programa.
   * <p>
   * Očekuje argumente:
   * <ul>
   *   <li><code>--ta &lt;putanja_datoteke_aranzmana&gt;</code></li>
   *   <li><code>--rta &lt;putanja_datoteke_rezervacija&gt;</code></li>
   * </ul>
   * </p>
   *
   * @param args argumenti komandne linije
   */
  public static void main(String[] args) {
    try {
      Argumenti argumenti = new Argumenti(args);

      UcitavacPodataka<Aranzman> ucitacAranzmana =
          UcitavacFactory.createAranzmanReader();
      UcitavacPodataka<Rezervacija> ucitacRezervacija =
          UcitavacFactory.createRezervacijaReader();

      List<Aranzman> aranzmani =
          ucitacAranzmana.ucitaj(argumenti.dohvatiPutanjuAranzmana());
      List<Rezervacija> rezervacije =
          ucitacRezervacija.ucitaj(argumenti.dohvatiPutanjuRezervacija());

      // Upravitelji domenskih objekata
      UpraviteljAranzmanima uprAranz =
          new UpraviteljAranzmanima(aranzmani);
      UpraviteljRezervacijama uprRez =
          new UpraviteljRezervacijama(rezervacije, uprAranz);

      // inicijalna rekalkulacija stanja za sve aranžmane
      for (Aranzman a : aranzmani) {
        uprRez.rekalkulirajZaAranzman(
            a.getOznaka(), a.getMinPutnika(), a.getMaxPutnika());
      }

      System.out.println("Učitano aranžmana: " + uprAranz.brojAranzmana());
      System.out.println("Učitano rezervacija: " + uprRez.brojRezervacija());

      Komande komande = new Komande(uprAranz, uprRez);
      komande.pokreni();

    } catch (IllegalArgumentException e) {
      System.err.println("Greška u argumentima: " + e.getMessage());
    } catch (Exception e) {
      System.err.println(
          "Neočekivana greška pri pokretanju aplikacije: " + e.getMessage());
    }
  }
}
