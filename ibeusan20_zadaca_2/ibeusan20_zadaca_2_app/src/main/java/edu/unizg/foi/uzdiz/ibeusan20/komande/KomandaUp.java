package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.AranzmanPodaci;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.RezervacijaPodaci;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.facade.DatotekeFacade;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.facade.DatotekeFacadeImpl;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.AranzmanDirector;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.RezervacijaDirector;

// TODO: Auto-generated Javadoc
/**
 * UP – učitavanje podataka o aranžmanima ili rezervacijama iz datoteke.
 * <p>
 * UP A datoteka – novi aranžmani<br>
 * UP R datoteka – nove rezervacije
 * </p>
 */
public class KomandaUp implements Komanda {

  /** The upr aranz. */
  private final UpraviteljAranzmanima uprAranz;
  
  /** The upr rez. */
  private final UpraviteljRezervacijama uprRez;
  
  /** The argumenti. */
  private final String[] argumenti;
  
  /** The ispis. */
  private final FormatIspisaBridge ispis = new TablicniFormat();

  /**
   * Instantiates a new komanda up.
   *
   * @param uprAranz the upr aranz
   * @param uprRez the upr rez
   * @param argumenti the argumenti
   */
  public KomandaUp(UpraviteljAranzmanima uprAranz, UpraviteljRezervacijama uprRez,
      String... argumenti) {
    this.uprAranz = uprAranz;
    this.uprRez = uprRez;
    this.argumenti = argumenti;
  }

  /**
   * Izvrsi.
   *
   * @return true, if successful
   */
  @Override
  public boolean izvrsi() {
    if (argumenti == null || argumenti.length < 2) {
      ispis.ispisi("Sintaksa: UP [A|R] nazivDatoteke");
      return true;
    }

    String mod = argumenti[0].trim().toUpperCase();
    String datoteka = argumenti[1].trim();

    ispis.ispisi("UP " + mod + " " + datoteka);

    DatotekeFacade facade = DatotekeFacadeImpl.getInstance();

    switch (mod) {
      case "A" -> ucitajAranzmane(facade, datoteka);
      case "R" -> ucitajRezervacije(facade, datoteka);
      default -> ispis.ispisi("Neispravan argument za UP. Dozvoljeno je A ili R. \n");
    }

    return true;
  }

  /**
   * Ucitaj aranzmane.
   *
   * @param facade the facade
   * @param datoteka the datoteka
   */
  private void ucitajAranzmane(DatotekeFacade facade, String datoteka) {
    List<AranzmanPodaci> dto = facade.ucitajAranzmane(datoteka);
    if (dto == null || dto.isEmpty()) {
      ispis.ispisi("Nije učitan nijedan aranžman iz datoteke " + datoteka + ". \n");
      return;
    }
    AranzmanDirector director = new AranzmanDirector();

    int dodano = 0;
    int preskocenoPostoji = 0;
    int greske = 0;

    for (AranzmanPodaci p : dto) {
      try {
        Aranzman a = director.konstruiraj(p);

        if (uprAranz.pronadiPoOznaci(a.getOznaka()) != null) {
          preskocenoPostoji++;
          continue;
        }

        uprAranz.dodaj(a);
        dodano++;
      } catch (IllegalArgumentException e) {
        greske++;
        System.err.println("[" + greske + ". greška (UP A)] " + e.getMessage());
      }
    }

    ispis.ispisi("Učitano novih aranžmana iz datoteke " + datoteka + ": " + dodano);
    if (preskocenoPostoji > 0) {
      ispis.ispisi("Preskočeno (aranžman već postoji): " + preskocenoPostoji);
    }
  }

  /**
   * Ucitaj rezervacije.
   *
   * @param facade the facade
   * @param datoteka the datoteka
   */
  private void ucitajRezervacije(DatotekeFacade facade, String datoteka) {
    List<RezervacijaPodaci> dto = facade.ucitajRezervacije(datoteka);

    if (dto == null || dto.isEmpty()) {
      ispis.ispisi("Nije učitana nijedna rezervacija iz datoteke " + datoteka + ".");
      return;
    }

    RezervacijaDirector director = new RezervacijaDirector();

    int dodano = 0;
    int preskocenoNepoznatiAranzman = 0;
    int greske = 0;

    for (RezervacijaPodaci p : dto) {
      try {
        Rezervacija r = director.konstruiraj(p);

        Aranzman a = uprAranz.pronadiPoOznaci(r.getOznakaAranzmana());
        if (a == null) {
          preskocenoNepoznatiAranzman++;
          continue;
        }

        if (a.jeOtkazan()) {
          ispis.ispisi("Greška: aranžman '" + a.getOznaka()
              + "' je otkazan - preskačem rezervaciju " + r.getIme() + " " + r.getPrezime() + ".");
          continue;
        }

        try {
          uprRez.dodaj(r);
          dodano++;
        } catch (IllegalStateException ex) {
          ispis.ispisi("Greška: " + ex.getMessage());
        }

      } catch (IllegalArgumentException e) {
        greske++;
        System.err.println("[" + greske + ". greška (UP R)] " + e.getMessage());
      }
    }

    // OBAVEZNO: jedna globalna rekalkulacija na kraju (kvote + preklapanja + stabilizacija)
    uprRez.rekalkulirajSve();

    ispis.ispisi("Učitano novih rezervacija iz datoteke " + datoteka + ": " + dodano);
    if (preskocenoNepoznatiAranzman > 0) {
      ispis.ispisi("Preskočeno (nepoznat aranžman): " + preskocenoNepoznatiAranzman);
    }
  }

}
