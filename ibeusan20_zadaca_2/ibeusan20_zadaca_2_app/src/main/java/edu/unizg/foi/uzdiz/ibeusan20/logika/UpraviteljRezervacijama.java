package edu.unizg.foi.uzdiz.ibeusan20.logika;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeAktivnaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeNaCekanjuRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeNovaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOtkazanaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOdgodenaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjePrimljenaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeRezervacije;

/**
 * Upravlja rezervacijama koristeći Composite (rezervacije su unutar aranžmana)
 * i uzorak State za statuse rezervacija.
 * <p>
 * Ne čuva vlastitu kolekciju rezervacija – sve dohvaća preko
 * {@link UpraviteljAranzmanima} i {@link Aranzman#getRezervacije()}.
 * </p>
 */
public class UpraviteljRezervacijama {

  private final UpraviteljAranzmanima upravljacAranzmana;

  /**
   * Instancira novi upravitelj rezervacijama.
   *
   * @param upravljacAranzmana upravitelj aranžmanima
   */
  public UpraviteljRezervacijama(UpraviteljAranzmanima upravljacAranzmana) {
    this.upravljacAranzmana = upravljacAranzmana;
  }

  // ---------------------------------------------------------------------------
  // Inicijalno punjenje Composite strukture
  // ---------------------------------------------------------------------------

  /**
   * Dodaje početne rezervacije (učitane iz CSV-a) na odgovarajuće aranžmane
   * i radi kompletnu rekalkulaciju stanja.
   *
   * @param pocetne lista domenskih rezervacija
   */
  public void dodajPocetne(List<Rezervacija> pocetne) {
    if (pocetne == null) {
      return;
    }
    for (Rezervacija r : pocetne) {
      Aranzman a = upravljacAranzmana.pronadiPoOznaci(r.getOznakaAranzmana());
      if (a == null) {
        continue;
      }
      a.dodajRezervaciju(r);
    }
    rekalkulirajSve();
  }

  /**
   * Dodaje jednu novu rezervaciju u odgovarajući aranžman i radi rekalkulaciju.
   *
   * @param r nova rezervacija
   */
  public void dodaj(Rezervacija r) {
    if (r == null) {
      return;
    }
    Aranzman a = upravljacAranzmana.pronadiPoOznaci(r.getOznakaAranzmana());
    if (a == null) {
      return;
    }
    a.dodajRezervaciju(r);
    rekalkulirajSve();
  }

  /**
   * Rekalkulacija za jedan aranžman – zbog međuovisnosti rezervacija
   * po osobi i preklapanja termina, ovdje se poziva globalna rekalkulacija.
   *
   * @param oznaka oznaka aranžmana
   * @param min minimalan broj putnika (ignorira se, čita se iz aranžmana)
   * @param max maksimalan broj putnika (ignorira se, čita se iz aranžmana)
   */
  public void rekalkulirajZaAranzman(String oznaka, int min, int max) {
    // zbog pravila o odgođenim rezervacijama i preklapanju termina
    // potrebno je uvijek gledati sve aranžmane zajedno
    rekalkulirajSve();
  }

  // ---------------------------------------------------------------------------
  // Upiti koje koriste komande
  // ---------------------------------------------------------------------------

  /**
   * Broj svih rezervacija (uključujući otkazane).
   *
   * @return broj rezervacija
   */
  public int brojRezervacija() {
    return sveRezervacije().size();
  }

  /**
   * Vraća sve rezervacije za zadanu osobu.
   *
   * @param ime ime osobe
   * @param prezime prezime osobe
   * @return lista rezervacija osobe, sortirana po datumu i vremenu
   */
  public List<Rezervacija> dohvatiZaOsobu(String ime, String prezime) {
    List<Rezervacija> rezultat = new ArrayList<>();
    for (Rezervacija r : sveRezervacije()) {
      if (ime.equalsIgnoreCase(r.getIme())
          && prezime.equalsIgnoreCase(r.getPrezime())) {
        rezultat.add(r);
      }
    }
    rezultat.sort(Comparator.comparing(Rezervacija::getDatumVrijeme));
    return rezultat;
  }

  /**
   * Vraća rezervacije za zadani aranžman filtrirane po vrsti
   * (PA, Č, O, OD) kako je zadano u opisu zadaće.
   *
   * @param oznaka oznaka aranžmana
   * @param vrste string s kombinacijom oznaka PA, Č, O, OD
   * @return filtrirana lista rezervacija
   */
  public List<Rezervacija> dohvatiZaAranzmanIVrste(String oznaka, String vrste) {
    Aranzman a = upravljacAranzmana.pronadiPoOznaci(oznaka);
    if (a == null) {
      return Collections.emptyList();
    }

    String filter = (vrste == null) ? "" : vrste.toUpperCase();
    boolean ukljPA = filter.contains("PA");
    boolean ukljCekanje = filter.contains("Č");
    boolean ukljOtkazane = filter.contains("O");
    boolean ukljOdgodene = filter.contains("OD");

    List<Rezervacija> rezultat = new ArrayList<>();
    for (Rezervacija r : a.getRezervacije()) {
      StanjeRezervacije st = r.getStanje();
      if (st instanceof StanjeOtkazanaRezervacija) {
        if (ukljOtkazane) {
          rezultat.add(r);
        }
      } else if (st instanceof StanjeOdgodenaRezervacija) {
        if (ukljOdgodene) {
          rezultat.add(r);
        }
      } else if (st instanceof StanjeNaCekanjuRezervacija) {
        if (ukljCekanje) {
          rezultat.add(r);
        }
      } else if (st instanceof StanjePrimljenaRezervacija
          || st instanceof StanjeAktivnaRezervacija) {
        if (ukljPA) {
          rezultat.add(r);
        }
      }
    }

    rezultat.sort(Comparator.comparing(Rezervacija::getDatumVrijeme));
    return rezultat;
  }

  /**
   * Provjerava ima li osoba već aktivnu rezervaciju za zadani aranžman.
   */
  public boolean imaAktivnuZa(String ime, String prezime, String oznakaAranzmana) {
    Aranzman a = upravljacAranzmana.pronadiPoOznaci(oznakaAranzmana);
    if (a == null) {
      return false;
    }
    for (Rezervacija r : a.getRezervacije()) {
      if (ime.equalsIgnoreCase(r.getIme())
          && prezime.equalsIgnoreCase(r.getPrezime())
          && r.jeAktivna()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Provjerava ima li osoba već aktivnu rezervaciju za neki drugi aranžman
   * koji se vremenski preklapa s aranžmanom zadane oznake.
   */
  public boolean imaAktivnuUPeriodu(String ime, String prezime, String oznaka,
      UpraviteljAranzmanima ua) {

    Aranzman ciljni = upravljacAranzmana.pronadiPoOznaci(oznaka);
    if (ciljni == null) {
      return false;
    }

    for (Aranzman a : upravljacAranzmana.svi()) {
      for (Rezervacija r : a.getRezervacije()) {
        if (!r.jeAktivna()) {
          continue;
        }
        if (!ime.equalsIgnoreCase(r.getIme())
            || !prezime.equalsIgnoreCase(r.getPrezime())) {
          continue;
        }
        if (preklapajuSe(ciljni, a)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Otkazuje rezervaciju osobe za zadani aranžman.
   * <p>
   * Vraća {@code true} ako je rezervacija pronađena i otkazana.
   * Nakon otkazivanja radi se globalna rekalkulacija stanja
   * (zadovoljava tri scenarija iz opisa zadaće).
   * </p>
   */
  public boolean otkaziRezervaciju(String ime, String prezime, String oznaka) {
    Aranzman a = upravljacAranzmana.pronadiPoOznaci(oznaka);
    if (a == null) {
      return false;
    }

    Rezervacija trazena = null;
    for (Rezervacija r : a.getRezervacije()) {
      if (ime.equalsIgnoreCase(r.getIme())
          && prezime.equalsIgnoreCase(r.getPrezime())
          && !(r.getStanje() instanceof StanjeOtkazanaRezervacija)) {
        trazena = r;
        break;
      }
    }

    if (trazena == null) {
      return false;
    }

    trazena.otkazi(LocalDateTime.now());
    rekalkulirajSve();
    return true;
  }

  // ---------------------------------------------------------------------------
  // Centralna rekalkulacija stanja rezervacija i aranžmana
  // ---------------------------------------------------------------------------

  /**
   * Globalna rekalkulacija svih rezervacija i aranžmana:
   * <ol>
   *   <li>resetira sva ne-otkazana stanja na "nova"</li>
   *   <li>po aranžmanu dodjeljuje primljena/aktivna/na čekanju
   *       prema min/maks i kronologiji</li>
   *   <li>po osobi dodjeljuje odgođene rezervacije u slučaju
   *       preklapanja termina aktivnih aranžmana</li>
   *   <li>ažurira stanje aranžmana (u pripremi, aktivan, popunjen, otkazan)</li>
   * </ol>
   */
  private void rekalkulirajSve() {
    // 1) reset svih ne-otkazanih na NOVA
    for (Aranzman a : upravljacAranzmana.svi()) {
      for (Rezervacija r : a.getRezervacije()) {
        if (!(r.getStanje() instanceof StanjeOtkazanaRezervacija)) {
          r.postaviStanje(StanjeNovaRezervacija.instanca());
        }
      }
    }

    // 2) po aranžmanu: primljena / aktivna / na čekanju
    for (Aranzman a : upravljacAranzmana.svi()) {
      List<Rezervacija> kandidati = new ArrayList<>();
      for (Rezervacija r : a.getRezervacije()) {
        if (!(r.getStanje() instanceof StanjeOtkazanaRezervacija)) {
          kandidati.add(r);
        }
      }
      kandidati.sort(Comparator.comparing(Rezervacija::getDatumVrijeme));

      int ukupno = kandidati.size();
      if (ukupno == 0) {
        a.azurirajStanje(0, 0);
        continue;
      }

      int min = a.getMinPutnika();
      int max = a.getMaxPutnika();

      if (ukupno < min) {
        for (Rezervacija r : kandidati) {
          r.postaviStanje(StanjePrimljenaRezervacija.instanca());
        }
      } else {
        for (int i = 0; i < kandidati.size(); i++) {
          Rezervacija r = kandidati.get(i);
          if (i < max) {
            r.postaviStanje(StanjeAktivnaRezervacija.instanca());
          } else {
            r.postaviStanje(StanjeNaCekanjuRezervacija.instanca());
          }
        }
      }

      int brojAktivnih = 0;
      for (Rezervacija r : kandidati) {
        if (r.jeAktivna()) {
          brojAktivnih++;
        }
      }
      a.azurirajStanje(brojAktivnih, ukupno);
    }

    // 3) po osobi: odgođene rezervacije (preklapanje termina aktivnih aranžmana)
    Map<String, List<Rezervacija>> poOsobi = new HashMap<>();
    for (Aranzman a : upravljacAranzmana.svi()) {
      for (Rezervacija r : a.getRezervacije()) {
        if (r.getStanje() instanceof StanjeOtkazanaRezervacija) {
          continue;
        }
        String kljuc = (r.getIme() + "|" + r.getPrezime()).toUpperCase();
        poOsobi.computeIfAbsent(kljuc, k -> new ArrayList<>()).add(r);
      }
    }

    for (List<Rezervacija> lista : poOsobi.values()) {
      lista.sort(Comparator.comparing(Rezervacija::getDatumVrijeme));

      List<Rezervacija> aktivne = new ArrayList<>();

      for (Rezervacija r : lista) {
        if (!r.jeAktivna()) {
          continue;
        }
        Aranzman a = upravljacAranzmana.pronadiPoOznaci(r.getOznakaAranzmana());
        if (a == null) {
          continue;
        }

        boolean konflikt = false;
        for (Rezervacija prije : aktivne) {
          Aranzman aPrije =
              upravljacAranzmana.pronadiPoOznaci(prije.getOznakaAranzmana());
          if (aPrije == null) {
            continue;
          }
          if (preklapajuSe(a, aPrije)) {
            konflikt = true;
            break;
          }
        }

        if (!konflikt) {
          aktivne.add(r);
        } else {
          r.postaviStanje(StanjeOdgodenaRezervacija.instanca());
        }
      }
    }

    // 4) nakon odgođenih ponovno osvježiti stanje aranžmana (broj aktivnih / prijava)
    for (Aranzman a : upravljacAranzmana.svi()) {
      int ukupno = 0;
      int aktivne = 0;
      for (Rezervacija r : a.getRezervacije()) {
        if (r.getStanje() instanceof StanjeOtkazanaRezervacija) {
          continue;
        }
        ukupno++;
        if (r.jeAktivna()) {
          aktivne++;
        }
      }
      a.azurirajStanje(aktivne, ukupno);
    }
  }

  // ---------------------------------------------------------------------------
  // Pomoćne metode
  // ---------------------------------------------------------------------------

  private List<Rezervacija> sveRezervacije() {
    List<Rezervacija> rezultat = new ArrayList<>();
    for (Aranzman a : upravljacAranzmana.svi()) {
      rezultat.addAll(a.getRezervacije());
    }
    return rezultat;
  }

  private boolean preklapajuSe(Aranzman a1, Aranzman a2) {
    LocalDateTime p1 = pocetak(a1);
    LocalDateTime k1 = kraj(a1);
    LocalDateTime p2 = pocetak(a2);
    LocalDateTime k2 = kraj(a2);

    return !(k1.isBefore(p2) || k2.isBefore(p1));
  }

  private LocalDateTime pocetak(Aranzman a) {
    LocalDate d = (a.getPocetniDatum() != null)
        ? a.getPocetniDatum()
        : (a.getZavrsniDatum() != null ? a.getZavrsniDatum() : LocalDate.now());
    LocalTime t = (a.getVrijemeKretanja() != null)
        ? a.getVrijemeKretanja()
        : LocalTime.MIN;
    return LocalDateTime.of(d, t);
  }

  private LocalDateTime kraj(Aranzman a) {
    LocalDate d = (a.getZavrsniDatum() != null)
        ? a.getZavrsniDatum()
        : (a.getPocetniDatum() != null ? a.getPocetniDatum() : LocalDate.now());
    LocalTime t = (a.getVrijemePovratka() != null)
        ? a.getVrijemePovratka()
        : LocalTime.MAX;
    return LocalDateTime.of(d, t);
  }
}
