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
    // 1) skupi sve NEOTKAZANE za ovaj aranžman
    List<Rezervacija> lista = new ArrayList<>();
    for (Rezervacija r : rezervacije) {
      if (r.getOznakaAranzmana().equalsIgnoreCase(oznaka) && !r.getVrsta().equalsIgnoreCase("O")) {
        lista.add(r);
      }
    }

    // 2) sortiraj po datumu
    lista.sort(Comparator.comparing(Rezervacija::getDatumVrijeme));

    int broj = lista.size();

    // helper za "osoba vec ima aktivnu"
    Set<String> osobeSAktivnom = new HashSet<>();

    if (broj < min) {
      // sve su primljene, ali NEMA aktivnih
      for (Rezervacija r : lista) {
        r.setVrsta("PA");
        r.setAktivna(false);
      }
      return;
    }

    // imamo barem min
    // gornja granica koja ide u PA
    int granica = Math.min(broj, max);

    for (int i = 0; i < granica; i++) {
      Rezervacija r = lista.get(i);
      String kljucOsobe = (r.getIme() + "|" + r.getPrezime()).toLowerCase();

      if (!osobeSAktivnom.contains(kljucOsobe)) {
        // prvi put ta osoba → aktivna = true
        r.setVrsta("PA");
        r.setAktivna(true);
        osobeSAktivnom.add(kljucOsobe);
      } else {
        // ista osoba drugi put → i dalje primljena, ali NE aktivna → Č
        r.setVrsta("Č");
        r.setAktivna(false);
      }
    }


    // 4) svi iza max idu u cekanje
    for (int i = granica; i < broj; i++) {
      Rezervacija r = lista.get(i);
      r.setVrsta("Č");
      r.setAktivna(false);
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
      return true;
    }

    return false;
  }


}
