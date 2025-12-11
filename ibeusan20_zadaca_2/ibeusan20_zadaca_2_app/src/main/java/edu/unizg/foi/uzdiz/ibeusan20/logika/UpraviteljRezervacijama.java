package edu.unizg.foi.uzdiz.ibeusan20.logika;

import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeAktivnaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeNaCekanjuRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeNovaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOtkazanaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjePrimljenaRezervacija;

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
 * Drži globalnu listu rezervacija, ali svaku rezervaciju dodaje i u pripadni
 * aranžman (Composite). Stanja rezervacija se održavaju uzorcima State.
 * </p>
 */
public class UpraviteljRezervacijama {

  private final List<Rezervacija> rezervacije = new ArrayList<>();
  private final UpraviteljAranzmanima upraviteljAranzmana;

  /**
   * Konstruktor koji prima početne rezervacije i upravitelja aranžmana.
   * Rezervacije se sortiraju po datumu i povezuju s odgovarajućim aranžmanom.
   */
  public UpraviteljRezervacijama(List<Rezervacija> pocetne,
      UpraviteljAranzmanima upraviteljAranzmana) {
    this.upraviteljAranzmana = upraviteljAranzmana;
    if (pocetne != null) {
      for (Rezervacija r : pocetne) {
        rezervacije.add(r);
        poveziSArazmanom(r);
      }
      sortirajPoDatumu();
    }
  }

  private void poveziSArazmanom(Rezervacija r) {
    Aranzman a =
        upraviteljAranzmana.pronadiPoOznaci(r.getOznakaAranzmana());
    if (a != null) {
      a.dodajRezervaciju(r);
    }
  }

  public int brojRezervacija() {
    return rezervacije.size();
  }

  public List<Rezervacija> sve() {
    return new ArrayList<>(rezervacije);
  }

  /**
   * Dodaje novu rezervaciju i odmah je povezuje s odgovarajućim aranžmanom.
   * Po defaultu je u stanju "nova".
   */
  public void dodaj(Rezervacija r) {
    if (r == null) {
      return;
    }
    r.postaviStanje(StanjeNovaRezervacija.instanca());
    rezervacije.add(r);
    poveziSArazmanom(r);
    sortirajPoDatumu();
  }

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
   * Filtrira rezervacije za zadani aranžman po "vrstama" stanja
   * (PA = primljena/aktivna, Č = na čekanju, O = otkazana, OD = odgođena).
   */
  public List<Rezervacija> dohvatiZaAranzmanIVrste(String oznaka,
      String vrste) {
    List<Rezervacija> sveZaAranzman = dohvatiZaAranzman(oznaka);
    if (vrste == null || vrste.isBlank()) {
      return sveZaAranzman;
    }

    String v = vrste.toUpperCase();
    boolean zeliPA = v.contains("PA");
    boolean zeliC = v.contains("Č") || v.contains("C");
    boolean zeliO = v.contains("O");
    boolean zeliOD = v.contains("OD");

    List<Rezervacija> rezultat = new ArrayList<>();
    for (Rezervacija r : sveZaAranzman) {
      String naziv = r.nazivStanja();
      if (jePA(naziv) && zeliPA) {
        rezultat.add(r);
      } else if ("na čekanju".equals(naziv) && zeliC) {
        rezultat.add(r);
      } else if ("otkazana".equals(naziv) && zeliO) {
        rezultat.add(r);
      } else if ("odgođena".equals(naziv) && zeliOD) {
        rezultat.add(r);
      }
    }
    return rezultat;
  }

  private boolean jePA(String naziv) {
    return "primljena".equals(naziv) || "aktivna".equals(naziv);
  }

  public List<Rezervacija> dohvatiZaOsobu(String ime, String prezime) {
    List<Rezervacija> rezultat = new ArrayList<>();
    for (Rezervacija r : rezervacije) {
      if (r.getIme().equalsIgnoreCase(ime)
          && r.getPrezime().equalsIgnoreCase(prezime)) {
        rezultat.add(r);
      }
    }
    return rezultat;
  }

  /**
   * Provjerava ima li osoba već aktivnu rezervaciju za isti aranžman.
   * Koristi State (jeAktivna + nije otkazana).
   */
  public boolean imaAktivnuZa(String ime, String prezime, String oznaka) {
    for (Rezervacija r : rezervacije) {
      if (r.getIme().equalsIgnoreCase(ime)
          && r.getPrezime().equalsIgnoreCase(prezime)
          && r.getOznakaAranzmana().equalsIgnoreCase(oznaka)
          && r.jeAktivna()
          && !(r.getStanje() instanceof StanjeOtkazanaRezervacija)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Provjerava postoji li aktivna rezervacija osobe u vremenskom periodu
   * nekog drugog aranžmana.
   */
  public boolean imaAktivnuUPeriodu(String ime, String prezime,
      String oznakaNovog, UpraviteljAranzmanima upraviteljAranzmanima) {

    LocalDate[] rasponNovog =
        upraviteljAranzmanima.dohvatiRasponZaOznaku(oznakaNovog);
    if (rasponNovog == null) {
      return false;
    }

    LocalDate pocetakNovog = rasponNovog[0];
    LocalDate krajNovog = rasponNovog[1];

    for (Rezervacija r : rezervacije) {
      if (!r.getIme().equalsIgnoreCase(ime)
          || !r.getPrezime().equalsIgnoreCase(prezime)) {
        continue;
      }
      if (!r.jeAktivna()
          || (r.getStanje() instanceof StanjeOtkazanaRezervacija)) {
        continue;
      }

      LocalDate[] rasponPostojeceg =
          upraviteljAranzmanima.dohvatiRasponZaOznaku(
              r.getOznakaAranzmana());
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
   * Otkazuje najraniju (po datumu unosa) neotkazanu rezervaciju osobe za
   * zadani aranžman. Rekalkulacija se radi izvana.
   */
  public boolean otkaziRezervaciju(String ime, String prezime,
      String oznaka) {
    Rezervacija najranija = null;

    for (Rezervacija r : rezervacije) {
      if (r.getOznakaAranzmana().equalsIgnoreCase(oznaka)
          && r.getIme().equalsIgnoreCase(ime)
          && r.getPrezime().equalsIgnoreCase(prezime)
          && !(r.getStanje()
              instanceof StanjeOtkazanaRezervacija)) {

        if (najranija == null
            || r.getDatumVrijeme().isBefore(
                najranija.getDatumVrijeme())) {
          najranija = r;
        }
      }
    }

    if (najranija != null) {
      najranija.otkazi(LocalDateTime.now());
      sortirajPoDatumu();
      return true;
    }
    return false;
  }

  /**
   * Rekalkulira stanja rezervacija za jedan aranžman i osvježava
   * stanje samog aranžmana (State + Composite).
   */
  public void rekalkulirajZaAranzman(String oznaka, int min, int max) {
    List<Rezervacija> lista = new ArrayList<>();
    for (Rezervacija r : rezervacije) {
      if (r.getOznakaAranzmana().equalsIgnoreCase(oznaka)
          && !(r.getStanje()
              instanceof StanjeOtkazanaRezervacija)) {
        lista.add(r);
      }
    }

    lista.sort(Comparator.comparing(Rezervacija::getDatumVrijeme));

    int brojPrijava = oznaciPrimljene(lista, min);
    int brojAktivnih = oznaciAktivneINaCekanju(lista, min, max);

    Aranzman a = upraviteljAranzmana.pronadiPoOznaci(oznaka);
    if (a != null) {
      a.azurirajStanje(brojAktivnih, brojPrijava);
    }
  }

  /**
   * Sve "nove" rezervacije prelaze u "primljena".
   * Broji se koliko ih ulazi u kvotu prijava.
   */
  private int oznaciPrimljene(List<Rezervacija> lista, int min) {
    int prijave = 0;
    for (Rezervacija r : lista) {
      if (r.getStanje() instanceof StanjeNovaRezervacija) {
        r.postaviStanje(StanjePrimljenaRezervacija.instanca());
      }
      if (r.brojiSeUKvotu()) {
        prijave++;
      }
    }
    // ako nema dovoljno prijava, sve ostaju samo "primljene"
    if (prijave < min) {
      for (Rezervacija r : lista) {
        if (r.getStanje()
            instanceof StanjePrimljenaRezervacija) {
          r.postaviStanje(StanjePrimljenaRezervacija.instanca());
        }
      }
    }
    return prijave;
  }

  /**
   * Dijeli primljene rezervacije na aktivne (do max) i na čekanju.
   * Ako nakon toga nema barem min aktivnih, sve se vraća na "primljena".
   */
  private int oznaciAktivneINaCekanju(List<Rezervacija> lista, int min,
      int max) {
    int aktivni = 0;
    Set<String> osobeSAktivnom = new HashSet<>();

    for (Rezervacija r : lista) {
      if (!r.brojiSeUKvotu()) {
        continue;
      }

      String kljuc =
          (r.getIme() + "|" + r.getPrezime()).toLowerCase();

      if (aktivni < max && !osobeSAktivnom.contains(kljuc)) {
        if (r.getStanje()
            instanceof StanjePrimljenaRezervacija) {
          r.postaviStanje(StanjeAktivnaRezervacija.instanca());
        }
        if (r.jeAktivna()) {
          aktivni++;
          osobeSAktivnom.add(kljuc);
        }
      } else {
        if (!(r.getStanje()
            instanceof StanjeOtkazanaRezervacija)) {
          r.postaviStanje(
              StanjeNaCekanjuRezervacija.instanca());
        }
      }
    }

    if (aktivni < min) {
      for (Rezervacija r : lista) {
        if (r.getStanje()
            instanceof StanjeAktivnaRezervacija) {
          r.postaviStanje(
              StanjePrimljenaRezervacija.instanca());
        }
      }
      aktivni = 0;
    }
    return aktivni;
  }

  private void sortirajPoDatumu() {
    rezervacije.sort(
        Comparator.comparing(Rezervacija::getDatumVrijeme));
  }
}
