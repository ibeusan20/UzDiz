package edu.unizg.foi.uzdiz.ibeusan20.logika;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.KontekstIspisa;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeAktivnaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeNaCekanjuRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOtkazanaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjePrimljenaRezervacija;

/**
 * Upravlja rezervacijama u sklopu aranžmana (Composite).
 * 
 * - ne čuva vlastitu kolekciju, koristi Aranzman.getRezervacije()
 * - implementira State logiku i kvote
 * - poštuje IP poredak pri vraćanju listi za ispis
 */
public class UpraviteljRezervacijama {

  private final UpraviteljAranzmanima upraviteljAranzmanima;

  public UpraviteljRezervacijama(UpraviteljAranzmanima upraviteljAranzmanima) {
    this.upraviteljAranzmanima = upraviteljAranzmanima;
  }

  /**
   * Dodaje početne rezervacije u pripadajuće aranžmane (Composite).
   * Ne radi rekalkulaciju – to radiš posebno u Aplikaciji.
   */
  public void dodajPocetne(List<Rezervacija> pocetne) {
    if (pocetne == null) {
      return;
    }
    for (Rezervacija r : pocetne) {
      Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(r.getOznakaAranzmana());
      if (a != null) {
        a.dodajRezervaciju(r);
      }
    }
  }

  /**
   * Dodaje novu rezervaciju u odgovarajući aranžman (Composite).
   * State inicijalno postavlja konstruktor Rezervacija (NOVA).
   */
  public void dodaj(Rezervacija r) {
    if (r == null) {
      return;
    }
    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(r.getOznakaAranzmana());
    if (a != null) {
      a.dodajRezervaciju(r);
    }
  }

  /**
   * Rekalkulira stanja rezervacija i aranžmana za zadanu oznaku.
   * 
   * Pravila (uskladivo sa zadaćom):
   * - ignorira otkazane rezervacije (ne diraju se)
   * - N prijava < minPutnika → sve neotkazane = PRIMLJENA, aranžman U PRIPREMI
   * - inače:
   *   * kronološki po datumu rezervacije
   *   * prvih do maxPutnika = AKTIVNA
   *   * ostale neotkazane = NA ČEKANJU
   * - poziva Aranzman.azurirajStanje(brojAktivnih, brojPrijava)
   */
  public void rekalkulirajZaAranzman(String oznaka, int minPutnika, int maxPutnika) {
    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznaka);
    if (a == null) {
      return;
    }

    List<Rezervacija> sve = new ArrayList<>(a.getRezervacije());
    // sortiraj kronološki (N), IP poredak će se riješiti pri ispisu
    sve.sort(Comparator.comparing(Rezervacija::getDatumVrijeme,
        Comparator.nullsLast(Comparator.naturalOrder())));

    // kandidati = sve neotkazane
    List<Rezervacija> kandidati = new ArrayList<>();
    for (Rezervacija r : sve) {
      if (r.getStanje() instanceof StanjeOtkazanaRezervacija) {
        continue;
      }
      kandidati.add(r);
    }

    int brojPrijava = kandidati.size();
    int brojAktivnih = 0;

    if (brojPrijava == 0) {
      a.azurirajStanje(0, 0);
      return;
    }

    if (brojPrijava < minPutnika) {
      // sve neotkazane → PRIMLJENA
      for (Rezervacija r : kandidati) {
        r.postaviStanje(StanjePrimljenaRezervacija.instanca());
      }
      brojAktivnih = 0;
    } else {
      int kvotaAktivnih = Math.min(brojPrijava, maxPutnika);
      int index = 0;
      for (Rezervacija r : kandidati) {
        if (index < kvotaAktivnih) {
          r.postaviStanje(StanjeAktivnaRezervacija.instanca());
          brojAktivnih++;
        } else {
          r.postaviStanje(StanjeNaCekanjuRezervacija.instanca());
        }
        index++;
      }
    }

    a.azurirajStanje(brojAktivnih, brojPrijava);
  }

  /**
   * Provjerava ima li osoba već AKTIVNU rezervaciju za zadani aranžman.
   */
  public boolean imaAktivnuZa(String ime, String prezime, String oznakaAranzmana) {
    if (ime == null || prezime == null || oznakaAranzmana == null) {
      return false;
    }
    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznakaAranzmana);
    if (a == null) {
      return false;
    }
    for (Rezervacija r : a.getRezervacije()) {
      if (ime.equalsIgnoreCase(r.getIme())
          && prezime.equalsIgnoreCase(r.getPrezime())
          && oznakaAranzmana.equalsIgnoreCase(r.getOznakaAranzmana())
          && r.jeAktivna()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Provjerava ima li osoba neku aktivnu rezervaciju čiji se period preklapa
   * s aranžmanom zadane oznake.
   * 
   * Parametar UpraviteljAranzmanima se ignorira (isti je kao interni),
   * ostavljen je radi postojećeg potpisa metode.
   */
  public boolean imaAktivnuUPeriodu(String ime, String prezime, String oznakaAranzmana,
      UpraviteljAranzmanima ignoriraj) {

    if (ime == null || prezime == null || oznakaAranzmana == null) {
      return false;
    }

    Aranzman ciljni = upraviteljAranzmanima.pronadiPoOznaci(oznakaAranzmana);
    if (ciljni == null) {
      return false;
    }

    LocalDate ciljniOd = ciljni.getPocetniDatum();
    LocalDate ciljniDo = ciljni.getZavrsniDatum();
    if (ciljniOd == null || ciljniDo == null) {
      return false;
    }

    for (Aranzman a : upraviteljAranzmanima.svi()) {
      LocalDate od = a.getPocetniDatum();
      LocalDate d0 = a.getZavrsniDatum();
      if (od == null || d0 == null) {
        continue;
      }

      if (!preklapaSe(ciljniOd, ciljniDo, od, d0)) {
        continue;
      }

      for (Rezervacija r : a.getRezervacije()) {
        if (ime.equalsIgnoreCase(r.getIme())
            && prezime.equalsIgnoreCase(r.getPrezime())
            && r.jeAktivna()) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean preklapaSe(LocalDate od1, LocalDate do1,
      LocalDate od2, LocalDate do2) {
    // nema preklapanja samo ako je jedan interval potpuno "prije" drugog
    if (do1.isBefore(od2) || do2.isBefore(od1)) {
      return false;
    }
    return true;
  }

  /**
   * Otkazuje rezervaciju osobe za zadani aranžman (ORTA).
   * Koristi State: Rezervacija.otkazi(time).
   */
  public boolean otkaziRezervaciju(String ime, String prezime, String oznakaAranzmana) {
    if (ime == null || prezime == null || oznakaAranzmana == null) {
      return false;
    }
    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznakaAranzmana);
    if (a == null) {
      return false;
    }

    for (Rezervacija r : a.getRezervacije()) {
      if (ime.equalsIgnoreCase(r.getIme())
          && prezime.equalsIgnoreCase(r.getPrezime())
          && oznakaAranzmana.equalsIgnoreCase(r.getOznakaAranzmana())) {

        r.otkazi(LocalDateTime.now());
        return true;
      }
    }
    return false;
  }

  /**
   * Vraća rezervacije za aranžman uz filtriranje po "vrstama" (PAČO).
   * 
   * Vrste se sada mapiraju preko nazivStanja(), npr.:
   * - 'P' → stanje s "PRIMLJEN" u nazivu
   * - 'A' → stanje s "AKTIV" u nazivu
   * - 'Č'/'C' → stanje s "ČEKANJ" / "CEKANJ" u nazivu
   * - 'O' → stanje s "OTKAZ" u nazivu
   * 
   * IP (N/S) poredak se primjenjuje na kraju.
   */
  public List<Rezervacija> dohvatiZaAranzmanIVrste(String oznaka, String vrste) {
    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznaka);
    if (a == null) {
      return List.of();
    }

    String filter = (vrste == null) ? "" : vrste.toUpperCase();
    boolean fP = filter.contains("P");
    boolean fA = filter.contains("A");
    boolean fC = filter.contains("Č") || filter.contains("C");
    boolean fO = filter.contains("O");

    boolean imaFiltera = fP || fA || fC || fO;

    List<Rezervacija> rezultat = new ArrayList<>();
    for (Rezervacija r : a.getRezervacije()) {
      String ns = r.nazivStanja();
      String u = ns == null ? "" : ns.toUpperCase();

      if (!imaFiltera) {
        rezultat.add(r);
        continue;
      }

      boolean pripada = false;
      if (fP && u.contains("PRIMLJEN")) {
        pripada = true;
      }
      if (fA && u.contains("AKTIV")) {
        pripada = true;
      }
      if (fC && (u.contains("ČEKANJ") || u.contains("CEKANJ"))) {
        pripada = true;
      }
      if (fO && u.contains("OTKAZ")) {
        pripada = true;
      }

      if (pripada) {
        rezultat.add(r);
      }
    }

    // sortiraj kronološki (N)
    rezultat.sort(Comparator.comparing(Rezervacija::getDatumVrijeme,
        Comparator.nullsLast(Comparator.naturalOrder())));

    // IP poredak – S obrće
    if (KontekstIspisa.jeObrnuto()) {
      Collections.reverse(rezultat);
    }

    return rezultat;
  }

  /**
   * Vraća sve rezervacije za zadanu osobu (IRO), preko svih aranžmana.
   * Poštuje IP poredak.
   */
  public List<Rezervacija> dohvatiZaOsobu(String ime, String prezime) {
    List<Rezervacija> rezultat = new ArrayList<>();
    if (ime == null || prezime == null) {
      return rezultat;
    }

    for (Aranzman a : upraviteljAranzmanima.svi()) {
      for (Rezervacija r : a.getRezervacije()) {
        if (ime.equalsIgnoreCase(r.getIme())
            && prezime.equalsIgnoreCase(r.getPrezime())) {
          rezultat.add(r);
        }
      }
    }

    rezultat.sort(Comparator.comparing(Rezervacija::getDatumVrijeme,
        Comparator.nullsLast(Comparator.naturalOrder())));

    if (KontekstIspisa.jeObrnuto()) {
      Collections.reverse(rezultat);
    }

    return rezultat;
  }

  /**
   * Broj svih rezervacija (preko svih aranžmana).
   */
  public int brojRezervacija() {
    int br = 0;
    for (Aranzman a : upraviteljAranzmanima.svi()) {
      br += a.getRezervacije().size();
    }
    return br;
  }
}
