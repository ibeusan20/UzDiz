package edu.unizg.foi.uzdiz.ibeusan20.logika;

import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Upravljanje rezervacijama za turističke aranžmane.
 * <p>
 * Rezervacije se nalaze unutar svojih aranžmana (Composite). Ovaj razred
 * nudi metode za dohvat, provjere i rekalkulaciju statusa.
 * </p>
 */
public class UpraviteljRezervacijama {

  private final UpraviteljAranzmanima upraviteljAranzmana;

  /**
   * Inicijalizira upravitelja referencom na upravitelja aranžmana.
   *
   * @param upraviteljAranzmana upravitelj aranžmana
   */
  public UpraviteljRezervacijama(UpraviteljAranzmanima upraviteljAranzmana) {
    this.upraviteljAranzmana = upraviteljAranzmana;
  }

  // ------------------- pomoćne metode -------------------

  /** @return nova lista svih rezervacija sa svih aranžmana */
  private List<Rezervacija> sveRezervacije() {
    List<Rezervacija> rezultat = new ArrayList<>();
    for (Aranzman a : upraviteljAranzmana.svi()) {
      rezultat.addAll(a.getRezervacije());
    }
    return rezultat;
  }

  // ------------------- osnovni dohvat -------------------

  /** @return broj svih rezervacija */
  public int brojRezervacija() {
    return sveRezervacije().size();
  }

  /** @return nova lista svih rezervacija */
  public List<Rezervacija> sve() {
    return sveRezervacije();
  }

  /**
   * Dodaje novu rezervaciju u odgovarajući aranžman.
   *
   * @param r rezervacija koja se dodaje
   */
  public void dodaj(Rezervacija r) {
    if (r == null) {
      return;
    }
    Aranzman a = upraviteljAranzmana.pronadiPoOznaci(r.getOznakaAranzmana());
    if (a != null) {
      a.dodajRezervaciju(r);
    }
  }

  /**
   * Dodaje početne (učitane) rezervacije.
   *
   * @param pocetne lista postojećih rezervacija
   */
  public void dodajPocetne(List<Rezervacija> pocetne) {
    if (pocetne == null) {
      return;
    }
    for (Rezervacija r : pocetne) {
      dodaj(r);
    }
  }

  /**
   * Dohvaća sve rezervacije za određeni aranžman.
   */
  public List<Rezervacija> dohvatiZaAranzman(String oznaka) {
    Aranzman a = upraviteljAranzmana.pronadiPoOznaci(oznaka);
    List<Rezervacija> rezultat = new ArrayList<>();
    if (a != null) {
      rezultat.addAll(a.getRezervacije());
      rezultat.sort(Comparator.comparing(Rezervacija::getDatumVrijeme));
    }
    return rezultat;
  }

  /**
   * Dohvaća rezervacije po oznaci aranžmana i tipu statusa.
   *
   * @param oznaka oznaka aranžmana
   * @param vrste skup slova statusa koje treba uključiti
   */
  public List<Rezervacija> dohvatiZaAranzmanIVrste(String oznaka, String vrste) {
    List<Rezervacija> sveZaAranzman = dohvatiZaAranzman(oznaka);
    if (vrste == null || vrste.isEmpty()) {
      return sveZaAranzman;
    }
    List<Rezervacija> rezultat = new ArrayList<>();
    for (Rezervacija r : sveZaAranzman) {
      if (vrste.contains(r.getVrsta())) {
        rezultat.add(r);
      }
    }
    return rezultat;
  }

  /**
   * Dohvati sve rezervacije određene osobe.
   */
  public List<Rezervacija> dohvatiZaOsobu(String ime, String prezime) {
    List<Rezervacija> rezultat = new ArrayList<>();
    for (Rezervacija r : sveRezervacije()) {
      if (r.getIme().equalsIgnoreCase(ime)
          && r.getPrezime().equalsIgnoreCase(prezime)) {
        rezultat.add(r);
      }
    }
    rezultat.sort(Comparator.comparing(Rezervacija::getDatumVrijeme));
    return rezultat;
  }

  // ------------------- provjere -------------------

  /**
   * Provjerava postoji li aktivna ili primljena rezervacija osobe
   * za zadani aranžman.
   */
  public boolean postojiRezervacija(String ime, String prezime, String oznaka) {
    for (Rezervacija r : sveRezervacije()) {
      if (r.getIme().equalsIgnoreCase(ime)
          && r.getPrezime().equalsIgnoreCase(prezime)
          && r.getOznakaAranzmana().equalsIgnoreCase(oznaka)
          && !r.getVrsta().equals("O")) {
        return true;
      }
    }
    return false;
  }

  /**
   * Provjerava ima li osoba aktivnu rezervaciju za isti aranžman.
   */
  public boolean imaAktivnuZa(String ime, String prezime, String oznaka) {
    for (Rezervacija r : sveRezervacije()) {
      if (r.getIme().equalsIgnoreCase(ime)
          && r.getPrezime().equalsIgnoreCase(prezime)
          && r.getOznakaAranzmana().equalsIgnoreCase(oznaka)
          && r.isAktivna()
          && !r.getVrsta().equalsIgnoreCase("O")) {
        return true;
      }
    }
    return false;
  }

  /**
   * Provjerava postoji li aktivna rezervacija osobe unutar zadanog
   * vremenskog raspona nekog drugog aranžmana.
   */
  public boolean imaAktivnuUPeriodu(String ime, String prezime,
      String oznakaNovog, UpraviteljAranzmanima upraviteljAranzmana) {

    LocalDate[] rasponNovog =
        upraviteljAranzmana.dohvatiRasponZaOznaku(oznakaNovog);
    if (rasponNovog == null) {
      return false;
    }

    LocalDate pocetakNovog = rasponNovog[0];
    LocalDate krajNovog = rasponNovog[1];

    for (Rezervacija r : sveRezervacije()) {
      if (!r.getIme().equalsIgnoreCase(ime)
          || !r.getPrezime().equalsIgnoreCase(prezime)
          || !r.isAktivna()
          || r.getVrsta().equalsIgnoreCase("O")) {
        continue;
      }

      LocalDate[] rasponPostojeceg =
          upraviteljAranzmana.dohvatiRasponZaOznaku(r.getOznakaAranzmana());
      if (rasponPostojeceg == null) {
        continue;
      }

      LocalDate pocetak = rasponPostojeceg[0];
      LocalDate kraj = rasponPostojeceg[1];

      boolean preklapaSe =
          !(kraj.isBefore(pocetakNovog) || pocetak.isAfter(krajNovog));
      if (preklapaSe) {
        return true;
      }
    }
    return false;
  }

  /**
   * (Opcionalno) dohvat svih rezervacija jedne osobe za aranžman.
   */
  public List<Rezervacija> dohvatiZaOsobuIAranzman(
      String ime, String prezime, String oznaka) {

    List<Rezervacija> rezultat = new ArrayList<>();
    for (Rezervacija r : dohvatiZaAranzman(oznaka)) {
      if (r.getIme().equalsIgnoreCase(ime)
          && r.getPrezime().equalsIgnoreCase(prezime)) {
        rezultat.add(r);
      }
    }
    return rezultat;
  }

  // ------------------- rekalkulacija -------------------

  /**
   * Rekalkulira sve rezervacije aranžmana prema minimalnom i
   * maksimalnom broju putnika.
   */
  public void rekalkulirajZaAranzman(String oznaka, int min, int max) {
    List<Rezervacija> lista = new ArrayList<>();
    for (Rezervacija r : dohvatiZaAranzman(oznaka)) {
      if (!r.getVrsta().equalsIgnoreCase("O")) {
        lista.add(r);
      }
    }

    lista.sort(Comparator.comparing(Rezervacija::getDatumVrijeme));
    int ukupno = lista.size();
    int aktivni = 0;
    Set<String> osobeSAktivnom = new HashSet<>();

    if (ukupno < min) {
      for (Rezervacija r : lista) {
        r.setVrsta("PA");
        r.setAktivna(false);
      }
      return;
    }

    for (Rezervacija r : lista) {
      r.setVrsta("Č");
      r.setAktivna(false);
    }

    for (Rezervacija r : lista) {
      String kljuc =
          (r.getIme() + "|" + r.getPrezime()).toLowerCase();

      if (aktivni < max && !osobeSAktivnom.contains(kljuc)) {
        r.setVrsta("PA");
        r.setAktivna(true);
        osobeSAktivnom.add(kljuc);
        aktivni++;
      }
    }

    if (aktivni < min) {
      for (Rezervacija r : lista) {
        if (r.getVrsta().equals("PA")) {
          r.setAktivna(false);
        }
      }
    }
  }

  /**
   * Otkazuje rezervaciju korisnika bez rekalkulacije.
   * Rekalkulaciju treba naknadno pozvati vanjski sloj (komanda).
   */
  public boolean otkaziRezervaciju(String ime, String prezime, String oznaka) {
    List<Rezervacija> zaAranzman = dohvatiZaAranzman(oznaka);
    Rezervacija najranija = null;

    for (Rezervacija r : zaAranzman) {
      if (!r.getIme().equalsIgnoreCase(ime)
          || !r.getPrezime().equalsIgnoreCase(prezime)
          || r.getVrsta().equalsIgnoreCase("O")) {
        continue;
      }
      if (najranija == null
          || r.getDatumVrijeme().isBefore(najranija.getDatumVrijeme())) {
        najranija = r;
      }
    }

    if (najranija != null) {
      najranija.setVrsta("O");
      najranija.setAktivna(false);
      najranija.setDatumVrijemeOtkaza(LocalDateTime.now());
      return true;
    }
    return false;
  }
}
