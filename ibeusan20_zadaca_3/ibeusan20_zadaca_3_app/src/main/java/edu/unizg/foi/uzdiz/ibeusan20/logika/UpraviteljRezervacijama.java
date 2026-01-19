package edu.unizg.foi.uzdiz.ibeusan20.logika;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.KontekstIspisa;
import edu.unizg.foi.uzdiz.ibeusan20.logika.ogranicenja.StrategijaBezOgranicenja;
import edu.unizg.foi.uzdiz.ibeusan20.logika.ogranicenja.StrategijaOgranicenjaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.FormatDatuma;
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
 *
 * Obavijesti (PTAR/Observer):
 * - promjene se spremaju tijekom akcija (npr. ORTA/OTA) u pending buffer
 * - stvarno slanje obavijesti događa se isključivo pri rekalkulaciji (jedno mjesto)
 */
public class UpraviteljRezervacijama {

  private final UpraviteljAranzmanima upraviteljAranzmanima;
  private final StrategijaOgranicenjaRezervacija strategija;

  /** Zadnje poznato stanje aranžmana (da se uhvate i promjene prije rekalkulacije, npr. postaviOtkazan). */
  private final Map<String, String> zadnjeStanjeAranzmana = new HashMap<>();

  /** Promjene rezervacija po aranžmanu koje čekaju, tako da neke komande ne šalju hrpu obavijesti. */
  private final Map<String, List<String>> pendingPromjeneRez = new HashMap<>();

  public UpraviteljRezervacijama(UpraviteljAranzmanima upraviteljAranzmanima) {
    this(upraviteljAranzmanima, new StrategijaBezOgranicenja());
  }

  public UpraviteljRezervacijama(UpraviteljAranzmanima upraviteljAranzmanima,
      StrategijaOgranicenjaRezervacija strategija) {
    this.upraviteljAranzmanima = upraviteljAranzmanima;
    this.strategija = (strategija == null) ? new StrategijaBezOgranicenja() : strategija;
  }

  /** Dodaje početne rezervacije u pripadajuće aranžmane */
  public void dodajPocetne(List<Rezervacija> pocetne) {
    if (pocetne == null) return;
    for (Rezervacija r : pocetne) {
      Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(r.getOznakaAranzmana());
      if (a != null) a.dodajRezervaciju(r);
    }
  }

  /** Dodaje novu rezervaciju u odgovarajući aranžman */
  public void dodaj(Rezervacija r) {
    if (r == null) return;
    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(r.getOznakaAranzmana());
    if (a != null) a.dodajRezervaciju(r);
  }

  /**
   * Rekalkulacija svih aranžmana do stabilnog stanja.
   * Obavijesti se šalju jednom na kraju da nema dupliranja.
   */
  public void rekalkulirajSve() {
    // obavijest: zadnje poznato stanje aranžmana + trenutna stanja rezervacija
    Map<String, String> stanjaArPrije = snapshotStanjaAranzmanaZaObavijest();
    Map<Rezervacija, String> stanjaRezPrije = snapshotStanjaRezervacijaZaObavijest();

    // stabilizacija
    for (int iter = 0; iter < 10; iter++) {
      Map<Rezervacija, String> prije = snapshotStanjaZaStabilnost();

      for (Aranzman a : upraviteljAranzmanima.svi()) {
        rekalkulirajZaAranzmanCore(a, a.getMinPutnika(), a.getMaxPutnika());
      }

      // pravilo preklapanja po osobi / kvota po osobi itd.
      strategija.primijeni(upraviteljAranzmanima);

      Map<Rezervacija, String> poslije = snapshotStanjaZaStabilnost();
      if (jednako(prije, poslije)) break;
    }

    // jedna obavijest po aranžmanu (ako ima pretplata i ako ima promjena)
    for (Aranzman a : upraviteljAranzmanima.svi()) {
      posaljiObavijestZaAranzman(a, stanjaArPrije.get(a.getOznaka()), stanjaRezPrije);
    }
  }

  /**
   * Rekalkulacija pojedinog aranžmana OTA npr to zove.
   * Ovdje se također šalje obavijest i ovdje se “pokupi” pending buffer.
   */
  public void rekalkulirajZaAranzman(String oznaka, int minPutnika, int maxPutnika) {
    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznaka);
    if (a == null) return;

    String stanjePrijeZaObavijest = zadnjeStanjeAranzmana.getOrDefault(oznaka, a.nazivStanja());
    Map<Rezervacija, String> stanjaRezPrije = snapshotStanjaRezervacijaZaJedanAranzman(a);

    rekalkulirajZaAranzmanCore(a, minPutnika, maxPutnika);

    // obavijest
    posaljiObavijestZaAranzman(a, stanjePrijeZaObavijest, stanjaRezPrije);
  }

  //  rekalkulacije bez obavijesti
  private void rekalkulirajZaAranzmanCore(Aranzman a, int minPutnika, int maxPutnika) {
    if (a == null) return;

    // sortira kronološki
    List<Rezervacija> sve = new ArrayList<>(a.getRezervacije());
    sve.sort(Comparator.comparing(Rezervacija::getDatumVrijeme,
        Comparator.nullsLast(Comparator.naturalOrder())));

    // kandidati za kvotu = oni koji se broje u kvotu (primljena/aktivna/čekanje)
    List<Rezervacija> kandidati = new ArrayList<>();
    for (Rezervacija r : sve) {
      if (r == null) continue;
      if (!r.brojiSeUKvotu()) continue; // automatski izbaci otkazane/odgođene/nova
      kandidati.add(r);
    }

    int brojPrijava = kandidati.size();
    int brojAktivnih = 0;

    if (brojPrijava == 0) {
      brojAktivnih = 0;
    } else if (brojPrijava < minPutnika) {
      for (Rezervacija r : kandidati) {
        r.postaviStanje(StanjePrimljenaRezervacija.instanca());
      }
      brojAktivnih = 0;
    } else {
      int kvotaAktivnih = Math.min(brojPrijava, maxPutnika);
      for (int i = 0; i < kandidati.size(); i++) {
        Rezervacija r = kandidati.get(i);
        if (i < kvotaAktivnih) {
          r.postaviStanje(StanjeAktivnaRezervacija.instanca());
          brojAktivnih++;
        } else {
          r.postaviStanje(StanjeNaCekanjuRezervacija.instanca());
        }
      }
    }

    // ažurira stanje aranžmana ako nije otkazan
    a.azurirajStanje(brojAktivnih, brojPrijava);
  }

  // Obavijesti 
  private void posaljiObavijestZaAranzman(Aranzman a, String stanjePrijeZaObavijest,
      Map<Rezervacija, String> stanjaRezPrije) {

    if (a == null) return;

    String oznaka = a.getOznaka();
    String stanjePoslije = a.nazivStanja();

    String prije = (stanjePrijeZaObavijest == null) ? stanjePoslije : stanjePrijeZaObavijest;

    List<String> promjeneRez = dohvatiPromjeneRezervacija(a, stanjaRezPrije);
    List<String> pending = pendingPromjeneRez.remove(oznaka);

    String opis = sastaviOpisPromjene(prije, stanjePoslije, promjeneRez, pending);

    // update “zadnje poznato”
    zadnjeStanjeAranzmana.put(oznaka, stanjePoslije);

    // stvarno slanje ako ima pretplata i ako ima što za slati
    posaljiObavijest(a, opis);
  }

  private void posaljiObavijest(Aranzman a, String opisPromjene) {
    if (a == null) return;
    if (opisPromjene == null || opisPromjene.isBlank()) return;
    if (!a.imaPretplata()) return;
    a.obavijestiPretplatnike(opisPromjene);
  }

  private String sastaviOpisPromjene(String stanjePrije, String stanjePoslije,
      List<String> promjeneRez, List<String> pending) {

    List<String> linije = new ArrayList<>();

    if (stanjePrije != null && stanjePoslije != null && !stanjePrije.equals(stanjePoslije)) {
      linije.add("Stanje aranžmana: " + formatStanjeAranzmana(stanjePrije) + " -> "
          + formatStanjeAranzmana(stanjePoslije) + ".");
    }

    List<String> sveRezPromjene = new ArrayList<>();
    if (pending != null && !pending.isEmpty()) sveRezPromjene.addAll(pending);
    if (promjeneRez != null && !promjeneRez.isEmpty()) sveRezPromjene.addAll(promjeneRez);

    if (!sveRezPromjene.isEmpty()) {
      linije.add("Promjene statusa rezervacija: " + sveRezPromjene.size() + ".");
      linije.addAll(sveRezPromjene);
    }

    return String.join("\n", linije);
  }

  private List<String> dohvatiPromjeneRezervacija(Aranzman a, Map<Rezervacija, String> prije) {
    List<String> detalji = new ArrayList<>();
    if (a == null) return detalji;

    for (Rezervacija r : a.getRezervacije()) {
      if (r == null) continue;

      String staro = (prije == null) ? null : prije.get(r);
      String novo = r.nazivStanja();

      if (novo == null) continue;
      if (staro == null) staro = "nova";
      if (Objects.equals(staro, novo)) continue;

      detalji.add(formatPromjenuRezervacije(r, staro, novo));
    }

    return detalji;
  }

  private String formatPromjenuRezervacije(Rezervacija r, String staro, String novo) {
    String osoba = imePrezime(r);
    String dv = FormatDatuma.formatiraj(r.getDatumVrijeme());
    String suf = (dv == null || dv.isBlank()) ? "" : " (" + dv + ")";
    return osoba + suf + ": " + formatStanjeRezervacije(staro) + " -> " + formatStanjeRezervacije(novo);
  }

  private String formatStanjeRezervacije(String naziv) {
    if (naziv == null) return "";
    String u = naziv.trim().toUpperCase();

    if (u.contains("OTKAZ")) return "OTKAZANA";
    if (u.contains("ODGOĐ") || u.contains("ODGOD")) return "ODGOĐENA";
    if (u.contains("ČEKANJ") || u.contains("CEKANJ")) return "ČEKANJE";
    if (u.contains("AKTIV")) return "AKTIVNA";
    if (u.contains("PRIMLJEN")) return "PRIMLJENA";
    if (u.contains("NOVA")) return "NOVA";

    return u;
  }

  private String formatStanjeAranzmana(String naziv) {
    if (naziv == null) return "";
    String u = naziv.trim().toUpperCase();

    if (u.contains("OTKAZ")) return "OTKAZAN";
    if (u.contains("POPUNJ")) return "POPUNJEN";
    if (u.contains("AKTIV")) return "AKTIVAN";
    if (u.contains("PRIPREM")) return "U PRIPREMI";

    return u;
  }

  private void dodajPendingPromjenu(String oznakaAranzmana, String linija) {
    if (oznakaAranzmana == null || linija == null || linija.isBlank()) return;
    pendingPromjeneRez.computeIfAbsent(oznakaAranzmana, k -> new ArrayList<>()).add(linija);
  }

  // Snapshoti za stabilizaciju i obavijesti
  private Map<String, String> snapshotStanjaAranzmanaZaObavijest() {
    Map<String, String> m = new HashMap<>();
    for (Aranzman a : upraviteljAranzmanima.svi()) {
      String ozn = a.getOznaka();
      String zadnje = zadnjeStanjeAranzmana.get(ozn);
      m.put(ozn, (zadnje != null) ? zadnje : a.nazivStanja());
    }
    return m;
  }

  private Map<Rezervacija, String> snapshotStanjaRezervacijaZaObavijest() {
    Map<Rezervacija, String> m = new IdentityHashMap<>();
    for (Aranzman a : upraviteljAranzmanima.svi()) {
      for (Rezervacija r : a.getRezervacije()) {
        if (r != null) m.put(r, r.nazivStanja());
      }
    }
    return m;
  }

  private Map<Rezervacija, String> snapshotStanjaRezervacijaZaJedanAranzman(Aranzman a) {
    Map<Rezervacija, String> m = new IdentityHashMap<>();
    if (a == null) return m;
    for (Rezervacija r : a.getRezervacije()) {
      if (r != null) m.put(r, r.nazivStanja());
    }
    return m;
  }

  /** Snapshot koji se koristi samo za detekciju stabilnosti (iteracije). */
  private Map<Rezervacija, String> snapshotStanjaZaStabilnost() {
    Map<Rezervacija, String> m = new IdentityHashMap<>();
    for (Aranzman a : upraviteljAranzmanima.svi()) {
      for (Rezervacija r : a.getRezervacije()) {
        if (r != null) m.put(r, r.nazivStanja());
      }
    }
    return m;
  }

  private boolean jednako(Map<Rezervacija, String> a, Map<Rezervacija, String> b) {
    if (a.size() != b.size()) return false;
    for (Map.Entry<Rezervacija, String> e : a.entrySet()) {
      if (!Objects.equals(e.getValue(), b.get(e.getKey()))) return false;
    }
    return true;
  }


  /** Provjerava ima li osoba već AKTIVNU rezervaciju za zadani aranžman. */
  public boolean imaAktivnuZa(String ime, String prezime, String oznakaAranzmana) {
    if (ime == null || prezime == null || oznakaAranzmana == null) return false;

    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznakaAranzmana);
    if (a == null) return false;

    for (Rezervacija r : a.getRezervacije()) {
      if (ime.equalsIgnoreCase(r.getIme()) && prezime.equalsIgnoreCase(r.getPrezime())
          && oznakaAranzmana.equalsIgnoreCase(r.getOznakaAranzmana()) && r.jeAktivna()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Provjerava ima li osoba neku aktivnu rezervaciju čiji se period preklapa s aranžmanom zadane
   * oznake.
   */
  public boolean imaAktivnuUPeriodu(String ime, String prezime, String oznakaAranzmana,
      UpraviteljAranzmanima ignoriraj) {

    if (ime == null || prezime == null || oznakaAranzmana == null) return false;

    Aranzman ciljni = upraviteljAranzmanima.pronadiPoOznaci(oznakaAranzmana);
    if (ciljni == null) return false;

    LocalDate ciljniOd = ciljni.getPocetniDatum();
    LocalDate ciljniDo = ciljni.getZavrsniDatum();
    if (ciljniOd == null || ciljniDo == null) return false;

    for (Aranzman a : upraviteljAranzmanima.svi()) {
      LocalDate od = a.getPocetniDatum();
      LocalDate d0 = a.getZavrsniDatum();
      if (od == null || d0 == null) continue;

      if (!preklapaSe(ciljniOd, ciljniDo, od, d0)) continue;

      for (Rezervacija r : a.getRezervacije()) {
        if (ime.equalsIgnoreCase(r.getIme()) && prezime.equalsIgnoreCase(r.getPrezime())
            && r.jeAktivna()) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean preklapaSe(LocalDate od1, LocalDate do1, LocalDate od2, LocalDate do2) {
    if (do1.isBefore(od2) || do2.isBefore(od1)) return false;
    return true;
  }

  /**
   * Otkazuje rezervaciju osobe za zadani aranžman
   * Ovdje se samo zabilježi promjena u pending buffer (bez slanja obavijesti),
   * a obavijest se šalje kod rekalkulacije 
   */
  public boolean otkaziRezervaciju(String ime, String prezime, String oznakaAranzmana) {
    if (ime == null || prezime == null || oznakaAranzmana == null) return false;

    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznakaAranzmana);
    if (a == null) return false;

    List<Rezervacija> kandidati = dohvatiKandidateZaOtkazivanje(a, ime, prezime, oznakaAranzmana);
    if (kandidati.isEmpty()) return false;

    kandidati.sort(Comparator.comparing(Rezervacija::getDatumVrijeme,
        Comparator.nullsLast(Comparator.naturalOrder())));

    Rezervacija aktivna = null;
    Rezervacija primljena = null;
    Rezervacija cekanje = null;
    Rezervacija odgodena = null;

    for (Rezervacija r : kandidati) {
      if (r.getStanje() instanceof StanjeAktivnaRezervacija) {
        if (aktivna == null) aktivna = r;
      } else if (r.getStanje() instanceof StanjePrimljenaRezervacija) {
        if (primljena == null) primljena = r;
      } else if (r.getStanje() instanceof StanjeNaCekanjuRezervacija) {
        if (cekanje == null) cekanje = r;
      } else if (jeOdgodena(r)) {
        if (odgodena == null) odgodena = r;
      }
    }

    Rezervacija zaOtkazati = (aktivna != null) ? aktivna
        : (primljena != null) ? primljena
        : (cekanje != null) ? cekanje
        : (odgodena != null) ? odgodena
        : kandidati.get(0);

    String prije = zaOtkazati.nazivStanja();
    zaOtkazati.otkazi(LocalDateTime.now());
    String poslije = zaOtkazati.nazivStanja();

    // pending promjena (ispisat će se tek u rekalkulaciji)
    dodajPendingPromjenu(oznakaAranzmana, formatPromjenuRezervacije(zaOtkazati, prije, poslije));

    return true;
  }

  private List<Rezervacija> dohvatiKandidateZaOtkazivanje(Aranzman a, String ime, String prezime,
      String oznakaAranzmana) {

    List<Rezervacija> kandidati = new ArrayList<>();
    for (Rezervacija r : a.getRezervacije()) {
      if (r == null) continue;

      boolean isti = ime.equalsIgnoreCase(r.getIme())
          && prezime.equalsIgnoreCase(r.getPrezime())
          && oznakaAranzmana.equalsIgnoreCase(r.getOznakaAranzmana());

      if (!isti) continue;
      if (r.getStanje() instanceof StanjeOtkazanaRezervacija) continue;

      kandidati.add(r);
    }
    return kandidati;
  }

  private boolean jeOdgodena(Rezervacija r) {
    if (r == null) return false;
    String ns = r.nazivStanja();
    if (ns == null) return false;
    String u = ns.toUpperCase();
    return u.contains("ODGOĐ") || u.contains("ODGOD");
  }

  /** Vraća rezervacije za aranžman uz filtriranje po vrstama */
  public List<Rezervacija> dohvatiZaAranzmanIVrste(String oznaka, String vrste) {
    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznaka);
    if (a == null) return List.of();

    String filter = (vrste == null) ? "" : vrste.toUpperCase();

    boolean fP = filter.contains("P");
    boolean fA = filter.contains("A");
    boolean fC = filter.contains("Č") || filter.contains("C");
    boolean fO = filter.contains("O");
    boolean fD = filter.contains("OD") || filter.contains("D");

    boolean imaFiltera = !filter.isBlank();

    List<Rezervacija> rezultat = new ArrayList<>();
    for (Rezervacija r : a.getRezervacije()) {
      String ns = r.nazivStanja();
      String u = ns == null ? "" : ns.toUpperCase();

      if (!imaFiltera) {
        rezultat.add(r);
        continue;
      }

      boolean pripada = false;

      if (fP && u.contains("PRIMLJEN")) pripada = true;
      if (fA && u.contains("AKTIV")) pripada = true;
      if (fC && (u.contains("ČEKANJ") || u.contains("CEKANJ"))) pripada = true;
      if (fO && u.contains("OTKAZ")) pripada = true;
      if (fD && (u.contains("ODGOĐ") || u.contains("ODGOD"))) pripada = true;

      if (pripada) rezultat.add(r);
    }

    rezultat.sort(Comparator.comparing(Rezervacija::getDatumVrijeme,
        Comparator.nullsLast(Comparator.naturalOrder())));

    if (KontekstIspisa.jeObrnuto()) Collections.reverse(rezultat);

    return rezultat;
  }

  /** Vraća sve rezervacije za zadanu osobu, preko svih aranžmana. */
  public List<Rezervacija> dohvatiZaOsobu(String ime, String prezime) {
    List<Rezervacija> rezultat = new ArrayList<>();
    if (ime == null || prezime == null) return rezultat;

    for (Aranzman a : upraviteljAranzmanima.svi()) {
      for (Rezervacija r : a.getRezervacije()) {
        if (ime.equalsIgnoreCase(r.getIme()) && prezime.equalsIgnoreCase(r.getPrezime())) {
          rezultat.add(r);
        }
      }
    }

    rezultat.sort(Comparator.comparing(Rezervacija::getDatumVrijeme,
        Comparator.nullsLast(Comparator.naturalOrder())));

    if (KontekstIspisa.jeObrnuto()) Collections.reverse(rezultat);

    return rezultat;
  }

  /** Broj svih rezervacija preko svih aranžmana. */
  public int brojRezervacija() {
    int br = 0;
    for (Aranzman a : upraviteljAranzmanima.svi()) {
      br += a.getRezervacije().size();
    }
    return br;
  }

  public int obrisiSveRezervacijeFizicki() {
    int obrisano = 0;

    for (Aranzman a : upraviteljAranzmanima.svi()) {
      obrisano += a.obrisiSveRezervacijeFizicki();

      String st = a.nazivStanja();
      boolean otkazan = st != null && st.toUpperCase().contains("OTKAZ");

      if (!otkazan) {
        a.azurirajStanje(0, 0);
      }
    }

    return obrisano;
  }

  public boolean postojiIdenticna(String ime, String prezime, String oznakaAranzmana,
      LocalDateTime datumVrijeme) {

    if (ime == null || prezime == null || oznakaAranzmana == null || datumVrijeme == null) {
      return false;
    }

    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznakaAranzmana);
    if (a == null) return false;

    for (Rezervacija r : a.getRezervacije()) {
      if (r == null) continue;

      boolean isti = ime.equalsIgnoreCase(r.getIme())
          && prezime.equalsIgnoreCase(r.getPrezime())
          && oznakaAranzmana.equalsIgnoreCase(r.getOznakaAranzmana())
          && datumVrijeme.equals(r.getDatumVrijeme());

      if (isti) return true;
    }
    return false;
  }

  public boolean dodajAkoNePostoji(Rezervacija r) {
    if (r == null) return false;

    if (postojiIdenticna(r.getIme(), r.getPrezime(), r.getOznakaAranzmana(), r.getDatumVrijeme())) {
      return false;
    }

    dodaj(r);
    return true;
  }

  private String imePrezime(Rezervacija r) {
    if (r == null) return "";
    String i = (r.getIme() == null) ? "" : r.getIme().trim();
    String p = (r.getPrezime() == null) ? "" : r.getPrezime().trim();
    return (i + " " + p).trim();
  }
}
