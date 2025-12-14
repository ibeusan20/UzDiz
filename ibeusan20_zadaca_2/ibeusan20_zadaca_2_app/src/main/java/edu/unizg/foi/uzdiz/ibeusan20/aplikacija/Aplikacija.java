package edu.unizg.foi.uzdiz.ibeusan20.aplikacija;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.unizg.foi.uzdiz.ibeusan20.datoteke.facade.DatotekeFacade;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.facade.DatotekeFacadeImpl;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.model.AranzmanCsv;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.model.RezervacijaCsv;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.AranzmanBuilder;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

/**
 * Glavna klasa aplikacije Turistička agencija.
 * <p>
 * Učitava podatke pomoću LIB facada, kreira domenske objekte,
 * puni Composite strukturu (aranžman -> rezervacije) i pokreće
 * interaktivno izvršavanje komandi.
 * </p>
 */
public class Aplikacija {

  /**
   * Ulazna točka programa.
   *
   * Očekuje argumente:
   * <ul>
   *   <li><code>--ta &lt;datoteka_aranzmana&gt;</code></li>
   *   <li><code>--rta &lt;datoteka_rezervacija&gt;</code></li>
   * </ul>
   *
   * @param args argumenti komandne linije
   */
  public static void main(String[] args) {
    try {
      Argumenti argumenti = new Argumenti(args);

      DatotekeFacade facade = DatotekeFacadeImpl.getInstance();

      // 1) čitanje CSV podataka preko LIB modula
      List<AranzmanCsv> aranzmaniCsv = new ArrayList<>();
      List<RezervacijaCsv> rezervacijeCsv = new ArrayList<>();

      if (argumenti.imaAranzmane()) {
        aranzmaniCsv = facade.ucitajAranzmane(argumenti.dohvatiPutanjuAranzmana());
      }
      if (argumenti.imaRezervacije()) {
        rezervacijeCsv = facade.ucitajRezervacije(argumenti.dohvatiPutanjuRezervacija());
      }


      // 2) kreiranje domenskih objekata (Aranzman) i mapa po oznaci
      List<Aranzman> aranzmani = new ArrayList<>();
      Map<String, Aranzman> mapaAranzmana = new LinkedHashMap<>();

      for (AranzmanCsv aCsv : aranzmaniCsv) {
        try {
          AranzmanBuilder b = new AranzmanBuilder()
              .postaviOznaku(aCsv.oznaka)
              .postaviNaziv(aCsv.naziv)
              .postaviProgram(aCsv.program)
              .postaviPocetniDatum(aCsv.pocetniDatum)
              .postaviZavrsniDatum(aCsv.zavrsniDatum)
              .postaviVrijemeKretanja(aCsv.vrijemeKretanja)
              .postaviVrijemePovratka(aCsv.vrijemePovratka)
              .postaviCijenu(aCsv.cijena)
              .postaviMinPutnika(aCsv.minPutnika)
              .postaviMaxPutnika(aCsv.maxPutnika)
              .postaviBrojNocenja(aCsv.brojNocenja)
              .postaviDoplatuJednokrevetna(aCsv.doplataJednokrevetna)
              .postaviPrijevoz(aCsv.prijevoz == null
                  ? ""
                  : String.join(";", aCsv.prijevoz))
              .postaviBrojDorucaka(aCsv.brojDorucaka)
              .postaviBrojRuckova(aCsv.brojRuckova)
              .postaviBrojVecera(aCsv.brojVecera);

          Aranzman a = b.izgradi();
          aranzmani.add(a);
          mapaAranzmana.put(a.getOznaka(), a);

        } catch (IllegalArgumentException e) {
          System.err.println(
              "Preskačem neispravan aranžman (" + aCsv.oznaka + "): " + e.getMessage());
        }
      }

      // 3) kreiranje domenskih rezervacija (još nisu u Composite-u)
      List<Rezervacija> rezervacije = new ArrayList<>();
      for (RezervacijaCsv rCsv : rezervacijeCsv) {
        Aranzman a = mapaAranzmana.get(rCsv.oznakaAranzmana);
        if (a == null) {
          // semantička provjera: ne postoji aranžman za rezervaciju
          System.err.println(
              "Preskačem rezervaciju " + rCsv.ime + " " + rCsv.prezime
                  + " - ne postoji aranžman s oznakom " + rCsv.oznakaAranzmana);
          continue;
        }
        if (rCsv.datumVrijeme == null) {
          System.err.println(
              "Preskačem rezervaciju (" + rCsv.ime + " " + rCsv.prezime
                  + "): neispravan datum/vrijeme.");
          continue;
        }

        Rezervacija r =
            new Rezervacija(rCsv.ime, rCsv.prezime, rCsv.oznakaAranzmana, rCsv.datumVrijeme);
        rezervacije.add(r);
      }

      // 4) upravitelji
      UpraviteljAranzmanima uprAranz = new UpraviteljAranzmanima(aranzmani);
      UpraviteljRezervacijama uprRez = new UpraviteljRezervacijama(uprAranz);

      // 5a) popunjavanje Composite strukture (aranžman -> rezervacije)
      uprRez.dodajPocetne(rezervacije);
      uprRez.rekalkulirajSve();

      // 5b) INICIJALNA REKALKULACIJA STATE-OVA ZA SVE ARANŽMANE
      for (Aranzman a : uprAranz.svi()) {
        uprRez.rekalkulirajZaAranzman(
            a.getOznaka(),
            a.getMinPutnika(),
            a.getMaxPutnika());
      }

      System.out.println("Učitano aranžmana: " + uprAranz.brojAranzmana());
      System.out.println("Učitano rezervacija: " + uprRez.brojRezervacija());

      // 6) pokretanje obrade komandi
      Komande komande = new Komande(uprAranz, uprRez);
      komande.pokreni();

    } catch (IllegalArgumentException e) {
      System.err.println("Greška u argumentima: " + e.getMessage());
      //return;
    } catch (Exception e) {
      System.err.println("Neočekivana greška pri pokretanju aplikacije: " + e.getMessage());
    }
  }
}
