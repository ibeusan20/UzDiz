package edu.unizg.foi.uzdiz.ibeusan20.memento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.AranzmanBuilder;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOtkazanAranzman;
import edu.unizg.foi.uzdiz.ibeusan20.pretplate.OsobaPretplatnik;
import edu.unizg.foi.uzdiz.ibeusan20.pretplate.PretplataPodaci;
import edu.unizg.foi.uzdiz.ibeusan20.pretplate.Pretplatnik;

public class AranzmanMemento {

  private final String oznaka;
  private final String naziv;
  private final String program;
  private final LocalDate pocetniDatum;
  private final LocalDate zavrsniDatum;
  private final LocalTime vrijemeKretanja;
  private final LocalTime vrijemePovratka;
  private final float cijena;
  private final int minPutnika;
  private final int maxPutnika;
  private final int brojNocenja;
  private final float doplataJednokrevetna;
  private final List<String> prijevoz;
  private final int brojDorucaka;
  private final int brojRuckova;
  private final int brojVecera;

  private final boolean otkazan;
  private final List<RezervacijaMemento> rezervacije;
  private final List<PretplataPodaci> pretplate;

  private AranzmanMemento(String oznaka, String naziv, String program, LocalDate pocetniDatum,
      LocalDate zavrsniDatum, LocalTime vrijemeKretanja, LocalTime vrijemePovratka, float cijena,
      int minPutnika, int maxPutnika, int brojNocenja, float doplataJednokrevetna,
      List<String> prijevoz, int brojDorucaka, int brojRuckova, int brojVecera, boolean otkazan,
      List<RezervacijaMemento> rezervacije, List<PretplataPodaci> pretplate) {

    this.oznaka = oznaka;
    this.naziv = naziv;
    this.program = program;
    this.pocetniDatum = pocetniDatum;
    this.zavrsniDatum = zavrsniDatum;
    this.vrijemeKretanja = vrijemeKretanja;
    this.vrijemePovratka = vrijemePovratka;
    this.cijena = cijena;
    this.minPutnika = minPutnika;
    this.maxPutnika = maxPutnika;
    this.brojNocenja = brojNocenja;
    this.doplataJednokrevetna = doplataJednokrevetna;
    this.prijevoz = prijevoz == null ? List.of() : new ArrayList<>(prijevoz);
    this.brojDorucaka = brojDorucaka;
    this.brojRuckova = brojRuckova;
    this.brojVecera = brojVecera;

    this.otkazan = otkazan;
    this.rezervacije = rezervacije == null ? List.of() : new ArrayList<>(rezervacije);
    this.pretplate = pretplate == null ? List.of() : new ArrayList<>(pretplate);
  }

  public static AranzmanMemento from(Aranzman a) {
    List<RezervacijaMemento> rez = new ArrayList<>();
    for (Rezervacija r : a.getRezervacije()) {
      rez.add(RezervacijaMemento.from(r));
    }

    List<PretplataPodaci> pp = new ArrayList<>();
    for (Pretplatnik p : a.getPretplatnici()) {
      pp.add(new PretplataPodaci(p.getIme(), p.getPrezime()));
    }

    boolean otkazan = a.getStanje() instanceof StanjeOtkazanAranzman;

    return new AranzmanMemento(a.getOznaka(), a.getNaziv(), a.getProgram(), a.getPocetniDatum(),
        a.getZavrsniDatum(), a.getVrijemeKretanja(), a.getVrijemePovratka(), a.getCijena(),
        a.getMinPutnika(), a.getMaxPutnika(), a.getBrojNocenja(), a.getDoplataJednokrevetna(),
        a.getPrijevoz(), a.getBrojDorucaka(), a.getBrojRuckova(), a.getBrojVecera(), otkazan, rez,
        pp);
  }

  public Aranzman restore() {
    String prijevozTekst = prijevoz.isEmpty() ? null : String.join(";", prijevoz);

    Aranzman a = new AranzmanBuilder().postaviOznaku(oznaka).postaviNaziv(naziv)
        .postaviProgram(program).postaviPocetniDatum(pocetniDatum).postaviZavrsniDatum(zavrsniDatum)
        .postaviVrijemeKretanja(vrijemeKretanja).postaviVrijemePovratka(vrijemePovratka)
        .postaviCijenu(cijena).postaviMinPutnika(minPutnika).postaviMaxPutnika(maxPutnika)
        .postaviBrojNocenja(brojNocenja).postaviDoplatuJednokrevetna(doplataJednokrevetna)
        .postaviPrijevoz(prijevozTekst).postaviBrojDorucaka(brojDorucaka).postaviBrojRuckova(brojRuckova)
        .postaviBrojVecera(brojVecera).izgradi();

    for (RezervacijaMemento rm : rezervacije) {
      a.dodajRezervaciju(rm.restore());
    }

    if (otkazan) {
      a.postaviOtkazan();
    } else {
      int brojPrijava = izracunajPrijave(a);
      int brojAktivnih = izracunajAktivne(a);
      a.azurirajStanje(brojAktivnih, brojPrijava);
    }

    for (PretplataPodaci p : pretplate) {
      a.dodajPretplatnika(new OsobaPretplatnik(p.ime(), p.prezime()));
    }

    return a;
  }

  private int izracunajPrijave(Aranzman a) {
    int br = 0;
    for (Rezervacija r : a.getRezervacije()) {
      if (r != null && r.brojiSeUKvotu()) {
        br++;
      }
    }
    return br;
  }

  private int izracunajAktivne(Aranzman a) {
    int br = 0;
    for (Rezervacija r : a.getRezervacije()) {
      if (r != null && r.jeAktivna()) {
        br++;
      }
    }
    return br;
  }
}
