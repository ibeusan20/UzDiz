package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.util.List;
import java.util.Set;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.facade.DatotekeFacade;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.facade.DatotekeFacadeImpl;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.model.AranzmanCsv;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.model.RezervacijaCsv;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.FormatIspisaBridge;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisTekstAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.AranzmanBuilder;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

/**
 * UP – učitavanje podataka o aranžmanima ili rezervacijama iz datoteke.
 * <p>
 * UP A datoteka – novi aranžmani<br>
 * UP R datoteka – nove rezervacije
 * </p>
 */
public class KomandaUp implements Komanda {

  private final UpraviteljAranzmanima uprAranz;
  private final UpraviteljRezervacijama uprRez;
  private final String[] argumenti;
  private final FormatIspisaBridge ispis = new TablicniFormat();

  public KomandaUp(UpraviteljAranzmanima uprAranz,
      UpraviteljRezervacijama uprRez, String... argumenti) {
    this.uprAranz = uprAranz;
    this.uprRez = uprRez;
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    if (argumenti.length < 2) {
      ispis.ispisi(new IspisTekstAdapter("Sintaksa: UP [A|R] nazivDatoteke"));
      return true;
    }

    String mod = argumenti[0].trim().toUpperCase();
    String datoteka = argumenti[1].trim();
    
    ispis.ispisi(new IspisTekstAdapter("UP " + mod + " " + datoteka));

    DatotekeFacade facade = DatotekeFacadeImpl.getInstance();

    switch (mod) {
      case "A" -> ucitajAranzmane(facade, datoteka);
      case "R" -> ucitajRezervacije(facade, datoteka);
      default -> ispis.ispisi(new IspisTekstAdapter("Neispravan argument za UP. Dozvoljeno je A ili R. \n"));
    }

    return true;
  }

  private void ucitajAranzmane(DatotekeFacade facade, String datoteka) {
    List<AranzmanCsv> dto = facade.ucitajAranzmane(datoteka);
    if (dto.isEmpty()) {
      ispis.ispisi(new IspisTekstAdapter("Nije učitan nijedan aranžman iz datoteke " + datoteka + ". \n"));
      return;
    }

    int brojac = 0;
    for (AranzmanCsv c : dto) {
      try {
        String prijevozTekst =
            (c.prijevoz == null || c.prijevoz.isEmpty())
                ? null
                : String.join(";", c.prijevoz);

        Aranzman a = new AranzmanBuilder()
            .postaviOznaku(c.oznaka)
            .postaviNaziv(c.naziv)
            .postaviProgram(c.program)
            .postaviPocetniDatum(c.pocetniDatum)
            .postaviZavrsniDatum(c.zavrsniDatum)
            .postaviVrijemeKretanja(c.vrijemeKretanja)
            .postaviVrijemePovratka(c.vrijemePovratka)
            .postaviCijenu(c.cijena)
            .postaviMinPutnika(c.minPutnika)
            .postaviMaxPutnika(c.maxPutnika)
            .postaviBrojNocenja(c.brojNocenja)
            .postaviDoplatuJednokrevetna(c.doplataJednokrevetna)
            .postaviPrijevoz(prijevozTekst)
            .postaviBrojDorucaka(c.brojDorucaka)
            .postaviBrojRuckova(c.brojRuckova)
            .postaviBrojVecera(c.brojVecera)
            .izgradi();
        
        if (uprAranz.pronadiPoOznaci(a.getOznaka()) != null) {
          System.out.println("Preskačem aranžman (već postoji): " + a.getOznaka());
          continue;
        }

        uprAranz.dodaj(a);
        brojac++;
      } catch (IllegalArgumentException ex) {
        System.err.println("Preskacem aranžman " + c.oznaka + ": " + ex.getMessage());
      }
    }

    ispis.ispisi(new IspisTekstAdapter("Učitano novih aranžmana iz datoteke " + datoteka + ": " + brojac));
  }

  private void ucitajRezervacije(DatotekeFacade facade, String datoteka) {
    List<RezervacijaCsv> dto = facade.ucitajRezervacije(datoteka);
    if (dto.isEmpty()) {
      System.out.println("Nije učitana nijedna rezervacija iz datoteke " + datoteka + ".");
      return;
    }

    int dodano = 0;
    int preskocenoDuplikata = 0;
    int preskocenoNepoznatiAranzman = 0;
    int preskocenoLosDatum = 0;

    // da rekalkuliramo jednom po aranžmanu
    Set<String> dirnutiAranzmani = new java.util.HashSet<>();

    for (RezervacijaCsv c : dto) {
      Aranzman a = uprAranz.pronadiPoOznaci(c.oznakaAranzmana);
      if (a == null) {
        preskocenoNepoznatiAranzman++;
        continue;
      }

      if (c.datumVrijeme == null) {
        preskocenoLosDatum++;
        continue;
      }

      // ključna stvar: provjera identične rezervacije
      //if (uprRez.postojiIdenticna(c.ime, c.prezime, c.oznakaAranzmana, c.datumVrijeme)) {
      //  preskocenoDuplikata++;
      //  continue;
      //}

      Rezervacija r = new Rezervacija(c.ime, c.prezime, c.oznakaAranzmana, c.datumVrijeme);
      uprRez.dodaj(r);
      
      uprRez.rekalkulirajSve(); // dobro

      dirnutiAranzmani.add(a.getOznaka());
      dodano++;
    }
    // OBAVEZNO: globalna rekalkulacija (kvote + preklapanja + stabilizacija)
    uprRez.rekalkulirajSve();

    ispis.ispisi(new IspisTekstAdapter("Učitano novih rezervacija iz datoteke " + datoteka + ": " + dodano));
    //if (preskocenoDuplikata > 0) {
    //  ispis.ispisi(new IspisTekstAdapter("Preskočeno duplikata (identične rezervacije): " + preskocenoDuplikata));
    //}
    if (preskocenoNepoznatiAranzman > 0) {
      ispis.ispisi(new IspisTekstAdapter("Preskočeno (nepoznat aranžman): " + preskocenoNepoznatiAranzman));
    }
    if (preskocenoLosDatum > 0) {
      ispis.ispisi(new IspisTekstAdapter("Preskočeno (neispravan datum/vrijeme): " + preskocenoLosDatum));
    }
  }

}
