package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljRezervacijama;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

/**
 * BP – brisanje podataka o aranžmanima ili rezervacijama.
 */
public class KomandaBp implements Komanda {

  private final UpraviteljAranzmanima uprAranz;
  private final UpraviteljRezervacijama uprRez;
  private final String[] argumenti;

  public KomandaBp(UpraviteljAranzmanima uprAranz,
      UpraviteljRezervacijama uprRez, String... argumenti) {
    this.uprAranz = uprAranz;
    this.uprRez = uprRez;
    this.argumenti = argumenti;
  }

  @Override
  public boolean izvrsi() {
    if (argumenti.length < 1) {
      System.out.println("Sintaksa: BP [A|R]");
      return true;
    }

    String mod = argumenti[0].trim().toUpperCase();

    switch (mod) {
      case "A" -> obrisiSveAranzmaneLogicki();
      case "R" -> obrisiSveRezervacijeLogicki();
      default -> System.out.println("Neispravan argument za BP. Dozvoljeno je A ili R.");
    }

    return true;
  }

  private void obrisiSveAranzmaneLogicki() {
    List<Aranzman> svi = uprAranz.svi();
    if (svi.isEmpty()) {
      System.out.println("Nema aranžmana za brisanje.");
      return;
    }

    for (Aranzman a : svi) {
      // otkaži sve rezervacije tog aranžmana (primljene, aktivne, čekanje, odgođene)
      List<Rezervacija> rez =
          uprRez.dohvatiZaAranzmanIVrste(a.getOznaka(), "PAČOD");
      for (Rezervacija r : rez) {
        uprRez.otkaziRezervaciju(r.getIme(), r.getPrezime(), a.getOznaka());
      }
      // stanje aranžmana: OTKAZAN
      a.postaviOtkazan();
      // dodatna rekalkulacija radi konzistencije koja ne mijenja status otkazanosti
      uprRez.rekalkulirajZaAranzman(a.getOznaka(), a.getMinPutnika(), a.getMaxPutnika());
    }

    System.out.println("Logički obrisani (otkazani) svi aranžmani i sve njihove rezervacije.");
  }

  private void obrisiSveRezervacijeLogicki() {
    List<Aranzman> svi = uprAranz.svi();
    boolean bilo = false;

    for (Aranzman a : svi) {
      List<Rezervacija> rez =
          uprRez.dohvatiZaAranzmanIVrste(a.getOznaka(), "PAČOD");
      for (Rezervacija r : rez) {
        boolean ok =
            uprRez.otkaziRezervaciju(r.getIme(), r.getPrezime(), a.getOznaka());
        if (ok) {
          bilo = true;
        }
      }
      // svaki put rekalkulacija kota i stanja aranžmana
      uprRez.rekalkulirajZaAranzman(a.getOznaka(), a.getMinPutnika(), a.getMaxPutnika());
    }

    if (bilo) {
      System.out.println("Logički obrisane (otkazane) sve rezervacije.");
    } else {
      System.out.println("Nema rezervacija za brisanje.");
    }
  }
}
