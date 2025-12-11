package edu.unizg.foi.uzdiz.ibeusan20.logika;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;

/**
 * Upravljanje rezervacijama za turističke aranžmane.
 * <p>
 * Omogućuje dodavanje, dohvat, otkazivanje i automatsku rekalkulaciju statusa rezervacija prema
 * ograničenjima aranžmana (min/max broj putnika).
 * </p>
 */
public class UpraviteljRezervacijama {
  private final List<Rezervacija> rezervacije = new ArrayList<>();

  /**
   * Inicijalizira upravitelja s postojećim rezervacijama. Automatski ih sortira po datumu unosa.
   *
   * @param pocetne lista postojećih rezervacija
   */
  public UpraviteljRezervacijama(List<Rezervacija> pocetne) {
    if (pocetne != null) {
      rezervacije.addAll(pocetne);
      sortirajPoDatumu();
    }
  }

  /** @return broj svih rezervacija */
  public int brojRezervacija() {
    return rezervacije.size();
  }

  /** @return nova lista svih rezervacija (kopija interne kolekcije) */
  public List<Rezervacija> sve() {
    return new ArrayList<>(rezervacije);
  }

  /**
   * Dodaje novu rezervaciju i održava redoslijed po datumu.
   *
   * @param r rezervacija koja se dodaje
   */
  public void dodaj(Rezervacija r) {
    if (r != null) {
      rezervacije.add(r);
      sortirajPoDatumu();
    }
  }

  /**
   * Dohvaća sve rezervacije za određeni aranžman.
   */
  public List<Rezervacija> dohvatiZaAranzman(String oznaka) {
    List<Rezervacija> rezultat = new ArrayList<>();
    for (Rezervacija r : rezervacije) {
      if (r.getOznakaAranzmana().equalsIgnoreCase(oznaka)) {
        rezultat.add(r);
      }
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
    List<Rezervacija> rezultat = new ArrayList<>();
    for (Rezervacija r : rezervacije) {
      if (r.getOznakaAranzmana().equalsIgnoreCase(oznaka)) {
        if (vrste == null || vrste.isEmpty() || vrste.contains(r.getVrsta())) {
          rezultat.add(r);
        }
      }
    }
    return rezultat;
  }

  /**
   * Dohvati sve rezervacije određene osobe.
   *
   * @param ime the ime
   * @param prezime the prezime
   * @return the list
   */
  public List<Rezervacija> dohvatiZaOsobu(String ime, String prezime) {
    List<Rezervacija> rezultat = new ArrayList<>();
    for (Rezervacija r : rezervacije) {
      if (r.getIme().equalsIgnoreCase(ime) && r.getPrezime().equalsIgnoreCase(prezime)) {
        rezultat.add(r);
      }
    }
    return rezultat;
  }

  /**
   * Provjerava postoji li aktivna ili primljena rezervacija osobe za zadani aranžman.
   *
   * @param ime the ime
   * @param prezime the prezime
   * @param oznaka the oznaka
   * @return true, if successful
   */
  public boolean postojiRezervacija(String ime, String prezime, String oznaka) {
    for (Rezervacija r : rezervacije) {
      if (r.getIme().equalsIgnoreCase(ime) && r.getPrezime().equalsIgnoreCase(prezime)
          && r.getOznakaAranzmana().equalsIgnoreCase(oznaka) && !r.getVrsta().equals("O")) {
        return true;
      }
    }
    return false;
  }

  /**
   * Provjerava ima li osoba aktivnu rezervaciju za isti aranžman.
   * 
   * @param ime the ime
   * @param prezime the prezime
   * @param oznaka the oznaka
   * @return {@code true} ako se periodi preklapaju
   */
  public boolean imaAktivnuZa(String ime, String prezime, String oznaka) {
    for (Rezervacija r : rezervacije) {
      if (r.getIme().equalsIgnoreCase(ime) && r.getPrezime().equalsIgnoreCase(prezime)
          && r.getOznakaAranzmana().equalsIgnoreCase(oznaka) && r.isAktivna()
          && !r.getVrsta().equalsIgnoreCase("O")) {
        return true;
      }
    }
    return false;
  }

  /**
   * Provjerava postoji li aktivna rezervacija osobe unutar zadanog vremenskog raspona nekog drugog
   * aranžmana.
   *
   * @param ime the ime
   * @param prezime the prezime
   * @param oznakaNovog the oznaka novog
   * @param upraviteljAranzmanima the upravitelj aranzmanima
   * @return {@code true} ako se periodi preklapaju
   */
  public boolean imaAktivnuUPeriodu(String ime, String prezime, String oznakaNovog,
      UpraviteljAranzmanima upraviteljAranzmanima) {
    LocalDate[] rasponNovog = upraviteljAranzmanima.dohvatiRasponZaOznaku(oznakaNovog);
    if (rasponNovog == null)
      return false;

    LocalDate pocetakNovog = rasponNovog[0];
    LocalDate krajNovog = rasponNovog[1];

    for (Rezervacija r : rezervacije) {
      if (r.getIme().equalsIgnoreCase(ime) && r.getPrezime().equalsIgnoreCase(prezime)
          && r.isAktivna() && !r.getVrsta().equalsIgnoreCase("O")) {

        LocalDate[] rasponPostojeceg =
            upraviteljAranzmanima.dohvatiRasponZaOznaku(r.getOznakaAranzmana());
        if (rasponPostojeceg == null)
          continue;

        LocalDate pocetak = rasponPostojeceg[0];
        LocalDate kraj = rasponPostojeceg[1];

        boolean preklapaSe = !(kraj.isBefore(pocetakNovog) || pocetak.isAfter(krajNovog));
        if (preklapaSe) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * ✅ (opcionalno) dohvat svih rezervacija jedne osobe za aranžman.
   *
   * @param ime the ime
   * @param prezime the prezime
   * @param oznaka the oznaka
   * @return the list
   */
  public List<Rezervacija> dohvatiZaOsobuIAranzman(String ime, String prezime, String oznaka) {
    List<Rezervacija> rezultat = new ArrayList<>();
    for (Rezervacija r : rezervacije) {
      if (r.getIme().equalsIgnoreCase(ime) && r.getPrezime().equalsIgnoreCase(prezime)
          && r.getOznakaAranzmana().equalsIgnoreCase(oznaka)) {
        rezultat.add(r);
      }
    }
    return rezultat;
  }

  /**
   * Rekalkulira sve rezervacije aranžmana prema minimalnom i maksimalnom broju putnika.
   *
   * @param oznaka the oznaka
   * @param min the min
   * @param max the max
   */
  public void rekalkulirajZaAranzman(String oznaka, int min, int max) {
    // dohvaća neotkazane rezervacije
    List<Rezervacija> lista = new ArrayList<>();
    for (Rezervacija r : rezervacije) {
      if (r.getOznakaAranzmana().equalsIgnoreCase(oznaka) && !r.getVrsta().equalsIgnoreCase("O")) {
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
      String kljuc = (r.getIme() + "|" + r.getPrezime()).toLowerCase();

      if (aktivni < max && !osobeSAktivnom.contains(kljuc)) {
        r.setVrsta("PA");
        r.setAktivna(true);
        osobeSAktivnom.add(kljuc);
        aktivni++;
      }
    }

    if (aktivni < min) { // min - min prag
      for (Rezervacija r : lista) {
        if (r.getVrsta().equals("PA")) {
          r.setAktivna(false);
        }
      }
    }
  }

  /**
   * Otkazuje rezervaciju korisnika bez rekalkulacije. Rekalkulaciju treba naknadno pozvati vanjski
   * sloj (npr. komanda).
   *
   * @param ime the ime
   * @param prezime the prezime
   * @param oznaka the oznaka
   * @return {@code true} ako je rezervacija uspješno otkazana
   */
  public boolean otkaziRezervaciju(String ime, String prezime, String oznaka) {
    Rezervacija najranija = null;

    for (Rezervacija r : rezervacije) {
      if (r.getOznakaAranzmana().equalsIgnoreCase(oznaka) && r.getIme().equalsIgnoreCase(ime)
          && r.getPrezime().equalsIgnoreCase(prezime) && !r.getVrsta().equalsIgnoreCase("O")) {

        if (najranija == null || r.getDatumVrijeme().isBefore(najranija.getDatumVrijeme())) {
          najranija = r;
        }
      }
    }

    if (najranija != null) {
      najranija.setVrsta("O");
      najranija.setAktivna(false);
      najranija.setDatumVrijemeOtkaza(LocalDateTime.now());
      sortirajPoDatumu();
      return true;
    }
    return false;
  }

  /** Interno sortira rezervacije po datumu. */
  private void sortirajPoDatumu() {
    rezervacije.sort(Comparator.comparing(Rezervacija::getDatumVrijeme));
  }
}
