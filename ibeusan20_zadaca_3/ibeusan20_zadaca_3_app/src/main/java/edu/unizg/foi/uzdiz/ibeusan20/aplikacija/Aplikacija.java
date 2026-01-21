package edu.unizg.foi.uzdiz.ibeusan20.aplikacija;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.AranzmanPodaci;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.RezervacijaPodaci;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.facade.DatotekeFacade;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.facade.DatotekeFacadeImpl;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.AranzmanDirector;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.RezervacijaDirector;
import edu.unizg.foi.uzdiz.ibeusan20.logika.ogranicenja.StrategijaOgranicenjaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.logika.ogranicenja.TvornicaStrategijeOgranicenja;


/**
 * Glavna klasa aplikacije Turistička agencija.
 * <p>
 * Učitava podatke pomoću LIB facada, kreira domenske objekte, puni Composite strukturu (aranžman ->
 * rezervacije) i pokreće interaktivno izvršavanje komandi.
 * </p>
 */
public class Aplikacija {

  /**
   * Ulazna točka programa.
   *
   * Očekuje argumente:
   * <ul>
   * <li><code>--ta &lt;datoteka_aranzmana&gt;</code></li>
   * <li><code>--rta &lt;datoteka_rezervacija&gt;</code></li>
   * <li><code>potencijalno --jdr ili --vdr</code></li>
   * </ul>
   *
   * @param args argumenti komandne linije
   */
  public static void main(String[] args) {
    int redniBrojGreske = 0;
    int redniBrojA = 0;
    int redniBrojR = 0;
    try {
      Argumenti argumenti = new Argumenti(args);

      DatotekeFacade facade = DatotekeFacadeImpl.getInstance();

      // čitanje CSV podataka preko LIB modula
      List<AranzmanPodaci> aranzmaniDto = List.of();
      List<RezervacijaPodaci> rezervacijeDto = List.of();

      if (argumenti.imaAranzmane()) {
        aranzmaniDto = facade.ucitajAranzmane(argumenti.dohvatiPutanjuAranzmana());
      }
      if (argumenti.imaRezervacije()) {
        rezervacijeDto = facade.ucitajRezervacije(argumenti.dohvatiPutanjuRezervacija());
      }

      // Direktori (Builder + Director u APP-u)
      AranzmanDirector arDirector = new AranzmanDirector();
      RezervacijaDirector rezDirector = new RezervacijaDirector();

      List<Aranzman> aranzmani = new ArrayList<>();
      Map<String, Aranzman> mapaAranzmana = new LinkedHashMap<>();

      redniBrojA = 0;
      for (AranzmanPodaci dto : aranzmaniDto) {
        redniBrojA++;
        try {
          Aranzman a = arDirector.konstruiraj(dto);
          aranzmani.add(a);
          mapaAranzmana.put(a.getOznaka(), a);

        } catch (IllegalArgumentException e) {
          redniBrojGreske++;
          System.err.println("[" + redniBrojGreske + ". greška (aranžmani)] u " + redniBrojA
              + ". retku rezervacije: " + "Preskačem neispravan aranžman (" + dto.getOznaka()
              + "): " + e.getMessage());
        }
      }

      List<Rezervacija> rezervacije = new ArrayList<>();
      redniBrojR = 0;
      for (RezervacijaPodaci dto : rezervacijeDto) {
        redniBrojR++;
        try {
          Rezervacija r = rezDirector.konstruiraj(dto);

          Aranzman a = mapaAranzmana.get(r.getOznakaAranzmana());
          if (a == null) {
            // semantička provjera: ne postoji aranžman za rezervaciju
            redniBrojGreske++;
            System.err.println("[" + redniBrojGreske + ". greška (rezervacije)] u " + redniBrojR
                + ". retku rezervacije: " + "Preskačem rezervaciju " + dto.getIme() + " "
                + dto.getPrezime() + " - ne postoji aranžman s oznakom "
                + dto.getOznakaAranzmana());
            continue;
          }
          if (dto.getDatumVrijeme() == null) {
            System.err.println();
            redniBrojGreske++;
            System.err.println("[" + redniBrojGreske + ". greška (rezervacije)] u " + redniBrojR
                + ". retku rezervacije: " + "Preskačem rezervaciju (" + dto.getIme() + " "
                + dto.getPrezime() + "): neispravan datum/vrijeme.");
            continue;
          }
          rezervacije.add(r);
        } catch (IllegalArgumentException e) {
          redniBrojGreske++;
          System.err.println("[" + redniBrojGreske + ". greška (rezervacije)] u "
              + ". retku rezervacije: " + e.getMessage());
        }
      }

      // upravitelji
      UpraviteljAranzmanima uprAranz = new UpraviteljAranzmanima(aranzmani);

      TvornicaStrategijeOgranicenja tvornica = new TvornicaStrategijeOgranicenja();
      StrategijaOgranicenjaRezervacija strategija =
          tvornica.kreiraj(argumenti.jeJdr(), argumenti.jeVdr());

      UpraviteljRezervacijama uprRez = new UpraviteljRezervacijama(uprAranz, strategija);


      // popunjavanje Composite strukture (aranžman -> rezervacije)
      uprRez.dodajPocetne(rezervacije);
      uprRez.rekalkulirajSve();

      // INICIJALNA REKALKULACIJA STATE-OVA ZA SVE ARANŽMANE
      for (Aranzman arr : uprAranz.svi()) {
        uprRez.rekalkulirajZaAranzman(arr.getOznaka(), arr.getMinPutnika(), arr.getMaxPutnika());
      }

      System.out.println("Učitano aranžmana: " + uprAranz.brojAranzmana());
      System.out.println("Učitano rezervacija: " + uprRez.brojRezervacija());

      // pokretanje obrade komandi
      Komande komande = new Komande(uprAranz, uprRez);
      komande.pokreni();

    } catch (IllegalArgumentException e) {
      System.err.println("Greška u argumentima: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Neočekivana greška pri pokretanju aplikacije: " + e.getMessage());
    }
  }
}
