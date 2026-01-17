package edu.unizg.foi.uzdiz.ibeusan20.memento;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.AranzmanBuilder;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

public final class AranzmanMemento {

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
  private final int brojPrijavaUKvoti;
  private final int brojAktivnih;

  private final List<RezervacijaMemento> rezervacije;

  private AranzmanMemento(String oznaka, String naziv, String program, LocalDate pocetniDatum,
      LocalDate zavrsniDatum, LocalTime vrijemeKretanja, LocalTime vrijemePovratka, float cijena,
      int minPutnika, int maxPutnika, int brojNocenja, float doplataJednokrevetna,
      List<String> prijevoz, int brojDorucaka, int brojRuckova, int brojVecera, boolean otkazan,
      int brojPrijavaUKvoti, int brojAktivnih, List<RezervacijaMemento> rezervacije) {
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
    this.prijevoz = (prijevoz == null) ? List.of() : List.copyOf(prijevoz);
    this.brojDorucaka = brojDorucaka;
    this.brojRuckova = brojRuckova;
    this.brojVecera = brojVecera;

    this.otkazan = otkazan;
    this.brojPrijavaUKvoti = brojPrijavaUKvoti;
    this.brojAktivnih = brojAktivnih;

    this.rezervacije = (rezervacije == null) ? List.of() : List.copyOf(rezervacije);
  }

  public static AranzmanMemento from(Aranzman a) {
    if (a == null)
      return null;

    int prijave = 0;
    int aktivne = 0;
    List<RezervacijaMemento> rez = new ArrayList<>();

    for (Rezervacija r : a.getRezervacije()) {
      if (r == null)
        continue;
      rez.add(RezervacijaMemento.from(r));
      if (r.brojiSeUKvotu())
        prijave++;
      if (r.jeAktivna())
        aktivne++;
    }

    return new AranzmanMemento(a.getOznaka(), a.getNaziv(), a.getProgram(), a.getPocetniDatum(),
        a.getZavrsniDatum(), a.getVrijemeKretanja(), a.getVrijemePovratka(), a.getCijena(),
        a.getMinPutnika(), a.getMaxPutnika(), a.getBrojNocenja(), a.getDoplataJednokrevetna(),
        a.getPrijevoz(), a.getBrojDorucaka(), a.getBrojRuckova(), a.getBrojVecera(), a.jeOtkazan(),
        prijave, aktivne, rez);
  }

  public Aranzman restore() {
    AranzmanBuilder b = new AranzmanBuilder().postaviOznaku(oznaka).postaviNaziv(naziv)
        .postaviProgram(program).postaviPocetniDatum(pocetniDatum).postaviZavrsniDatum(zavrsniDatum)
        .postaviCijenu(cijena).postaviMinPutnika(minPutnika).postaviMaxPutnika(maxPutnika)
        .postaviBrojNocenja(brojNocenja).postaviDoplatuJednokrevetna(doplataJednokrevetna)
        .postaviBrojDorucaka(brojDorucaka).postaviBrojRuckova(brojRuckova)
        .postaviBrojVecera(brojVecera);

    if (vrijemeKretanja != null)
      b.postaviVrijemeKretanja(vrijemeKretanja);
    if (vrijemePovratka != null)
      b.postaviVrijemePovratka(vrijemePovratka);

    if (prijevoz != null && !prijevoz.isEmpty()) {
      b.postaviPrijevoz(String.join(";", prijevoz));
    }

    Aranzman a = b.izgradi();

    // dodaje rezervacije, aranžman još nije otkazan da ne baci iznimku kod dodavanja
    for (RezervacijaMemento rm : rezervacije) {
      if (rm == null)
        continue;
      a.dodajRezervaciju(rm.restore());
    }

    // vrati stanje aranžmana
    if (otkazan) {
      a.postaviOtkazan();
    } else {
      a.azurirajStanje(brojAktivnih, brojPrijavaUKvoti);
    }

    return a;
  }
}
