package logika;

import model.Rezervacija;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UpraviteljRezervacijama {

  private final List<Rezervacija> rezervacije = new ArrayList<>();

  public UpraviteljRezervacijama(List<Rezervacija> pocetne) {
    if (pocetne != null) {
      rezervacije.addAll(pocetne);
      sortirajPoDatumu();
    }
  }

  public int brojRezervacija() {
    return rezervacije.size();
  }

  public List<Rezervacija> sve() {
    return new ArrayList<>(rezervacije);
  }

  public void dodaj(Rezervacija r) {
    if (r != null) {
      rezervacije.add(r);
      sortirajPoDatumu();
    }
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

  public List<Rezervacija> dohvatiZaOsobu(String ime, String prezime) {
    List<Rezervacija> rezultat = new ArrayList<>();
    for (Rezervacija r : rezervacije) {
      if (r.getIme().equalsIgnoreCase(ime) && r.getPrezime().equalsIgnoreCase(prezime)) {
        rezultat.add(r);
      }
    }
    return rezultat;
  }

  public boolean postojiRezervacija(String ime, String prezime, String oznaka) {
    for (Rezervacija r : rezervacije) {
      if (r.getIme().equalsIgnoreCase(ime) && r.getPrezime().equalsIgnoreCase(prezime)
          && r.getOznakaAranzmana().equalsIgnoreCase(oznaka) && !r.getVrsta().equals("O")) {
        return true;
      }
    }
    return false;
  }

  /** ✅ NOVO — provjera ima li korisnik već aktivnu rezervaciju za isti aranžman */
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

  /** ✅ (opcionalno) dohvat svih rezervacija jedne osobe za aranžman */
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

  public void rekalkulirajZaAranzman(String oznaka, int min, int max) {
    // 1) Skupi sve NEOTKAZANE rezervacije za ovaj aranžman
    List<Rezervacija> lista = new ArrayList<>();
    for (Rezervacija r : rezervacije) {
      if (r.getOznakaAranzmana().equalsIgnoreCase(oznaka) && !r.getVrsta().equalsIgnoreCase("O")) {
        lista.add(r);
      }
    }

    // 2) Sortiraj po datumu (najranije prve)
    lista.sort(Comparator.comparing(Rezervacija::getDatumVrijeme));

    // 3) Pomoćne varijable
    int ukupno = lista.size();
    int aktivni = 0;
    Set<String> osobeSAktivnom = new HashSet<>();

    // 4) Ako je manje od minimalnog broja — sve su "primljene" (PA, ali ne aktivne)
    if (ukupno < min) {
      for (Rezervacija r : lista) {
        r.setVrsta("PA");
        r.setAktivna(false);
      }
      return;
    }

    // 5) Prvo resetiraj sve na čekanje
    for (Rezervacija r : lista) {
      r.setVrsta("Č");
      r.setAktivna(false);
    }

    // 6) Popuni aktivne do max, ali pazi da osoba nema više od jedne aktivne
    for (Rezervacija r : lista) {
      String kljuc = (r.getIme() + "|" + r.getPrezime()).toLowerCase();

      if (aktivni < max && !osobeSAktivnom.contains(kljuc)) {
        r.setVrsta("PA");
        r.setAktivna(true);
        osobeSAktivnom.add(kljuc);
        aktivni++;
      }
    }

    // 7) Ako nakon toga broj aktivnih padne ispod minimalnog — sve vraćamo u "primljene"
    if (aktivni < min) {
      for (Rezervacija r : lista) {
        if (r.getVrsta().equals("PA")) {
          r.setAktivna(false);
        }
      }
    }
  }


  /**
   * Otkaz rezervacije – BEZ rekalkulacije. Rekalkulaciju treba pozvati onaj tko zna min/max
   * (komanda).
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

  private void sortirajPoDatumu() {
    rezervacije.sort(Comparator.comparing(Rezervacija::getDatumVrijeme));
  }



}
