package edu.unizg.foi.uzdiz.ibeusan20.logika;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.KontekstIspisa;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeAktivnaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeNaCekanjuRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOdgodenaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOtkazanaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjePrimljenaRezervacija;

// TODO: Auto-generated Javadoc
/**
 * Upravlja rezervacijama u sklopu aranžmana (Composite).
 * 
 * - ne čuva vlastitu kolekciju, koristi Aranzman.getRezervacije() - implementira State logiku i
 * kvote - poštuje IP poredak pri vraćanju listi za ispis
 */
public class UpraviteljRezervacijama {

  /** The upravitelj aranzmanima. */
  private final UpraviteljAranzmanima upraviteljAranzmanima;

  /**
   * Instantiates a new upravitelj rezervacijama.
   *
   * @param upraviteljAranzmanima the upravitelj aranzmanima
   */
  public UpraviteljRezervacijama(UpraviteljAranzmanima upraviteljAranzmanima) {
    this.upraviteljAranzmanima = upraviteljAranzmanima;
  }

  /**
   * Dodaje početne rezervacije u pripadajuće aranžmane (Composite). Ne radi rekalkulaciju – to
   * radiš posebno u Aplikaciji.
   *
   * @param pocetne the pocetne
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
   * Dodaje novu rezervaciju u odgovarajući aranžman (Composite). State inicijalno postavlja
   * konstruktor Rezervacija (NOVA).
   *
   * @param r the r
   */
  public void dodaj(Rezervacija r) {
    if (r == null) {
      return;
    }
    // spriječi identične duplikate (npr. ponovno učitavanje istog CSV-a) // NENENENE
    //if (postojiIdenticna(r.getIme(), r.getPrezime(), r.getOznakaAranzmana(), r.getDatumVrijeme())) {
    //  return;
    //}
    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(r.getOznakaAranzmana());
    if (a != null) {
      a.dodajRezervaciju(r);
    }
  }

  /**
   * Glavna Z2 rekalkulacija: - po aranžmanu: PRIMLJENA/AKTIVNA/ČEKANJE po min/max - po osobi: ako
   * ima više AKTIVNIH u preklapanju → najranija ostaje AKTIVNA, ostale postaju ODGOĐENE - ponavlja
   * dok se stanja ne stabiliziraju.
   */
  public void rekalkulirajSve() {
    // sigurnosna ograda da ne upadnemo u beskonačnu petlju
    for (int iter = 0; iter < 10; iter++) {
      Map<Rezervacija, String> prije = snapshotStanja();

      // 1) kvote po aranžmanima (ne diramo OTKAZANE i ODGOĐENE)
      for (Aranzman a : upraviteljAranzmanima.svi()) {
        rekalkulirajZaAranzman(a.getOznaka(), a.getMinPutnika(), a.getMaxPutnika());
      }

      // 2) pravilo preklapanja po osobi (ODGOĐENE)
      primijeniPraviloPreklapanja();

      Map<Rezervacija, String> poslije = snapshotStanja();
      if (jednako(prije, poslije)) {
        break; // stabilno
      }
    }
  }

  /**
   * Snapshot stanja.
   *
   * @return the map
   */
  private Map<Rezervacija, String> snapshotStanja() {
    Map<Rezervacija, String> m = new IdentityHashMap<>();
    for (Aranzman a : upraviteljAranzmanima.svi()) {
      for (Rezervacija r : a.getRezervacije()) {
        m.put(r, r.nazivStanja());
      }
    }
    return m;
  }

  /**
   * Jednako.
   *
   * @param a the a
   * @param b the b
   * @return true, if successful
   */
  private boolean jednako(Map<Rezervacija, String> a, Map<Rezervacija, String> b) {
    if (a.size() != b.size())
      return false;
    for (Map.Entry<Rezervacija, String> e : a.entrySet()) {
      if (!Objects.equals(e.getValue(), b.get(e.getKey())))
        return false;
    }
    return true;
  }

  /**
   * Rekalkulira stanja rezervacija i aranžmana za zadanu oznaku.
   * 
   * Pravila (uskladivo sa zadaćom): - ignorira otkazane rezervacije (ne diraju se) - N prijava <
   * minPutnika → sve neotkazane = PRIMLJENA, aranžman U PRIPREMI - inače: * kronološki po datumu
   * rezervacije * prvih do maxPutnika = AKTIVNA * ostale neotkazane = NA ČEKANJU - poziva
   * Aranzman.azurirajStanje(brojAktivnih, brojPrijava)
   *
   * @param oznaka the oznaka
   * @param minPutnika the min putnika
   * @param maxPutnika the max putnika
   */
  public void rekalkulirajZaAranzman(String oznaka, int minPutnika, int maxPutnika) {
    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznaka);
    if (a == null) return;

    // sortiraj kronološki (N)
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
      a.azurirajStanje(0, 0);
      return;
    }

    // 1) nije dostignut minimum -> sve su PRIMLJENE
    if (brojPrijava < minPutnika) {
      for (Rezervacija r : kandidati) {
        r.postaviStanje(StanjePrimljenaRezervacija.instanca());
      }
      brojAktivnih = 0;

    // 2) minimum dostignut -> prvih max su AKTIVNE, ostale NA ČEKANJU
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

    // ažuriraj stanje aranžmana (sad će ti i "popunjen" raditi)
    a.azurirajStanje(brojAktivnih, brojPrijava);
  }


  /**
   * Provjerava ima li osoba već AKTIVNU rezervaciju za zadani aranžman.
   *
   * @param ime the ime
   * @param prezime the prezime
   * @param oznakaAranzmana the oznaka aranzmana
   * @return true, if successful
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
      if (ime.equalsIgnoreCase(r.getIme()) && prezime.equalsIgnoreCase(r.getPrezime())
          && oznakaAranzmana.equalsIgnoreCase(r.getOznakaAranzmana()) && r.jeAktivna()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Pravilo preklapanja: Ako osoba ima više AKTIVNIH rezervacija koje se preklapaju po periodu
   * aranžmana: - najranija (po datumu rezervacije) ostaje aktivna - ostale u presjeku idu u
   * ODGOĐENE
   *
   * Također: ako osoba NEMA više konflikt (nema aktivnu u tom preklapanju), ODGOĐENE rezervacije
   * koje se više ne preklapaju vraćamo u PRIMLJENU (da ponovno uđu u kvote).
   */
  private void primijeniPraviloPreklapanja() {
    // grupiraj sve rezervacije po osobi
    Map<String, List<Rezervacija>> poOsobi = new HashMap<>();

    for (Aranzman a : upraviteljAranzmanima.svi()) {
      for (Rezervacija r : a.getRezervacije()) {
        String key = (r.getIme() + "|" + r.getPrezime()).toLowerCase();
        poOsobi.computeIfAbsent(key, k -> new ArrayList<>()).add(r);
      }
    }

    for (List<Rezervacija> lista : poOsobi.values()) {
      // aktivne (neotkazane) rezervacije
      List<Rezervacija> aktivne = new ArrayList<>();
      for (Rezervacija r : lista) {
        if (r.getStanje() instanceof StanjeOtkazanaRezervacija)
          continue;
        if (r.jeAktivna())
          aktivne.add(r);
      }

      // ako nema aktivnih, sve odgođene oslobodi (postaju primljene) → scenarij 3 nakon otkaza
      if (aktivne.isEmpty()) {
        for (Rezervacija r : lista) {
          if (r.getStanje() instanceof StanjeOdgodenaRezervacija) {
            r.postaviStanje(StanjePrimljenaRezervacija.instanca());
          }
        }
        continue;
      }

      // sortiraj aktivne po datumu rezervacije (kronološki najranija ostaje aktivna)
      aktivne.sort(Comparator.comparing(Rezervacija::getDatumVrijeme,
          Comparator.nullsLast(Comparator.naturalOrder())));

      // za svaki konflikt preklapanja: samo prva ostaje aktivna u tom konfliktu
      for (int i = 0; i < aktivne.size(); i++) {
        Rezervacija rGlavna = aktivne.get(i);
        Aranzman aGlavna = upraviteljAranzmanima.pronadiPoOznaci(rGlavna.getOznakaAranzmana());
        if (aGlavna == null)
          continue;

        LocalDateTime gOd = pocetak(aGlavna);
        LocalDateTime gDo = kraj(aGlavna);

        for (int j = i + 1; j < aktivne.size(); j++) {
          Rezervacija rDruga = aktivne.get(j);
          Aranzman aDruga = upraviteljAranzmanima.pronadiPoOznaci(rDruga.getOznakaAranzmana());
          if (aDruga == null)
            continue;

          LocalDateTime dOd = pocetak(aDruga);
          LocalDateTime dDo = kraj(aDruga);
          if (preklapaSe(gOd, gDo, dOd, dDo)) {
            // kasnija aktivna ide u odgođenu
            rDruga.postaviStanje(StanjeOdgodenaRezervacija.instanca());
          }
        }
      }

      // dodatno: ako neka ODGOĐENA više ne preklapa nijednu aktivnu osobe → vrati u PRIMLJENU
      for (Rezervacija r : lista) {
        if (!(r.getStanje() instanceof StanjeOdgodenaRezervacija))
          continue;

        Aranzman ar = upraviteljAranzmanima.pronadiPoOznaci(r.getOznakaAranzmana());
        if (ar == null)
          continue;

        LocalDateTime od = pocetak(ar);
        LocalDateTime d0 = kraj(ar);

        boolean preklapaSAktivnom = false;
        for (Rezervacija akt : aktivne) {
          Aranzman aa = upraviteljAranzmanima.pronadiPoOznaci(akt.getOznakaAranzmana());
          if (aa == null)
            continue;
          if (preklapaSe(od, d0, pocetak(aa), kraj(aa))) {
            preklapaSAktivnom = true;
            break;
          }
        }

        if (!preklapaSAktivnom) {
          r.postaviStanje(StanjePrimljenaRezervacija.instanca());
        }
      }
    }
  }

  /**
   * Pocetak.
   *
   * @param a the a
   * @return the local date time
   */
  private LocalDateTime pocetak(Aranzman a) {
    LocalDate d = a.getPocetniDatum();
    LocalTime t = a.getVrijemeKretanja();
    if (d == null)
      return LocalDateTime.MIN;
    return LocalDateTime.of(d, t != null ? t : LocalTime.MIN);
  }

  /**
   * Kraj.
   *
   * @param a the a
   * @return the local date time
   */
  private LocalDateTime kraj(Aranzman a) {
    LocalDate d = a.getZavrsniDatum();
    LocalTime t = a.getVrijemePovratka();
    if (d == null)
      return LocalDateTime.MAX;
    return LocalDateTime.of(d, t != null ? t : LocalTime.MAX);
  }

  /**
   * Preklapa se.
   *
   * @param od1 the od 1
   * @param do1 the do 1
   * @param od2 the od 2
   * @param do2 the do 2
   * @return true, if successful
   */
  private boolean preklapaSe(LocalDateTime od1, LocalDateTime do1, LocalDateTime od2,
      LocalDateTime do2) {
    if (do1.isBefore(od2) || do2.isBefore(od1))
      return false;
    return true;
  }

  /**
   * Provjerava ima li osoba neku aktivnu rezervaciju čiji se period preklapa s aranžmanom zadane
   * oznake.
   * 
   * Parametar UpraviteljAranzmanima se ignorira (isti je kao interni), ostavljen je radi postojećeg
   * potpisa metode.
   *
   * @param ime the ime
   * @param prezime the prezime
   * @param oznakaAranzmana the oznaka aranzmana
   * @param ignoriraj the ignoriraj
   * @return true, if successful
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
        if (ime.equalsIgnoreCase(r.getIme()) && prezime.equalsIgnoreCase(r.getPrezime())
            && r.jeAktivna()) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Preklapa se.
   *
   * @param od1 the od 1
   * @param do1 the do 1
   * @param od2 the od 2
   * @param do2 the do 2
   * @return true, if successful
   */
  private boolean preklapaSe(LocalDate od1, LocalDate do1, LocalDate od2, LocalDate do2) {
    // nema preklapanja samo ako je jedan interval potpuno "prije" drugog
    if (do1.isBefore(od2) || do2.isBefore(od1)) {
      return false;
    }
    return true;
  }

  /**
   * Otkazuje rezervaciju osobe za zadani aranžman (ORTA). Koristi State: Rezervacija.otkazi(time).
   *
   * @param ime the ime
   * @param prezime the prezime
   * @param oznakaAranzmana the oznaka aranzmana
   * @return true, if successful
   */
  public boolean otkaziRezervaciju(String ime, String prezime, String oznakaAranzmana) {
    if (ime == null || prezime == null || oznakaAranzmana == null) {
      return false;
    }

    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznakaAranzmana);
    if (a == null) {
      return false;
    }

    // sve rezervacije te osobe za taj aranžman (ne-otkazane)
    List<Rezervacija> kandidati = new ArrayList<>();
    for (Rezervacija r : a.getRezervacije()) {
      if (ime.equalsIgnoreCase(r.getIme()) && prezime.equalsIgnoreCase(r.getPrezime())
          && oznakaAranzmana.equalsIgnoreCase(r.getOznakaAranzmana())
          && !(r.getStanje() instanceof StanjeOtkazanaRezervacija)) {
        kandidati.add(r);
      }
    }

    if (kandidati.isEmpty()) {
      return false;
    }

    // kronološki – najstarije prve
    kandidati.sort(Comparator.comparing(Rezervacija::getDatumVrijeme,
        Comparator.nullsLast(Comparator.naturalOrder())));

    Rezervacija aktivna = null;
    Rezervacija primljena = null;
    Rezervacija odgodena = null;

    for (Rezervacija r : kandidati) {
      if (r.getStanje() instanceof StanjeAktivnaRezervacija) {
        if (aktivna == null) {
          aktivna = r;
        }
      } else if (r.getStanje() instanceof StanjePrimljenaRezervacija) {
        if (primljena == null) {
          primljena = r;
        }
      } else if (jeOdgodena(r)) {
        if (odgodena == null) {
          odgodena = r;
        }
      }
    }

    LocalDateTime sada = LocalDateTime.now();

    // 3) AKTIVNA + ODGOĐENA za istu osobu i aranžman:
    // - otkazuje se AKTIVNA
    // - ODGOĐENA prelazi u AKTIVNU
    if (aktivna != null && odgodena != null) {
      aktivna.otkazi(sada); // stanje -> otkazana, upiše se datum otkaza
      odgodena.postaviStanje(StanjeAktivnaRezervacija.instanca());
      return true;
    }

    // 2) samo AKTIVNA (bez odgođenih za tu osobu):
    // - otkazuje se aktivna
    // - radi se rekalkulacija, netko s čekanja može “uskočiti”
    if (aktivna != null) {
      aktivna.otkazi(sada);
      rekalkulirajZaAranzman(a.getOznaka(), a.getMinPutnika(), a.getMaxPutnika());
      return true;
    }

    // 1) nema aktivne, ali ima PRIMLJENA:
    // - otkazuje se primljena (tzv. lista prijava)
    // - rekalkulacija (obično neće puno promijeniti aktivne, ali ostaje konzistentno)
    if (primljena != null) {
      primljena.otkazi(sada);
      rekalkulirajZaAranzman(a.getOznaka(), a.getMinPutnika(), a.getMaxPutnika());
      return true;
    }

    // sve ostalo (npr. postoji samo odgođena/otkazana) – nema što otkazati
    return false;
  }

  /**
   * Je odgodena.
   *
   * @param r the r
   * @return true, if successful
   */
  private boolean jeOdgodena(Rezervacija r) {
    if (r == null) {
      return false;
    }
    String ns = r.nazivStanja();
    if (ns == null) {
      return false;
    }
    String u = ns.toUpperCase();
    // pokriva "odgođena", "odgođena rezervacija", bez obzira na dijakritike
    return u.contains("ODGOĐ") || u.contains("ODGOD");
  }



  /**
   * Vraća rezervacije za aranžman uz filtriranje po "vrstama" (PAČO).
   * 
   * Vrste se sada mapiraju preko nazivStanja(), npr.: - 'P' → stanje s "PRIMLJEN" u nazivu - 'A' →
   * stanje s "AKTIV" u nazivu - 'Č'/'C' → stanje s "ČEKANJ" / "CEKANJ" u nazivu - 'O' → stanje s
   * "OTKAZ" u nazivu
   * 
   * IP (N/S) poredak se primjenjuje na kraju.
   *
   * @param oznaka the oznaka
   * @param vrste the vrste
   * @return the list
   */
  public List<Rezervacija> dohvatiZaAranzmanIVrste(String oznaka, String vrste) {
    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznaka);
    if (a == null) {
      return List.of();
    }

    String filter = (vrste == null) ? "" : vrste.toUpperCase();

    // P, A, Č/C, O, D (OD = kombinacija O + D)
    boolean fP = filter.contains("P");
    boolean fA = filter.contains("A");
    boolean fC = filter.contains("Č") || filter.contains("C");
    boolean fO = filter.contains("O");
    boolean fD = filter.contains("OD") || filter.contains("D"); // OD||D za ODGOĐENE

    // ako je string filtera ne-prazan → imamo filtere,
    // i kad su svi gore false (npr. "X"), tretiramo to kao "ništa se ne poklapa", a NE "bez
    // filtera"
    boolean imaFiltera = !filter.isBlank();

    List<Rezervacija> rezultat = new ArrayList<>();
    for (Rezervacija r : a.getRezervacije()) {
      String ns = r.nazivStanja();
      String u = ns == null ? "" : ns.toUpperCase();

      // bez filtera → vrati sve
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
      // ODGOĐENE – prema nazivu stanja
      if (fD && (u.contains("ODGOĐ") || u.contains("ODGOD"))) {
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
   * Vraća sve rezervacije za zadanu osobu (IRO), preko svih aranžmana. Poštuje IP poredak.
   *
   * @param ime the ime
   * @param prezime the prezime
   * @return the list
   */
  public List<Rezervacija> dohvatiZaOsobu(String ime, String prezime) {
    List<Rezervacija> rezultat = new ArrayList<>();
    if (ime == null || prezime == null) {
      return rezultat;
    }

    for (Aranzman a : upraviteljAranzmanima.svi()) {
      for (Rezervacija r : a.getRezervacije()) {
        if (ime.equalsIgnoreCase(r.getIme()) && prezime.equalsIgnoreCase(r.getPrezime())) {
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
   *
   * @return the int
   */
  public int brojRezervacija() {
    int br = 0;
    for (Aranzman a : upraviteljAranzmanima.svi()) {
      br += a.getRezervacije().size();
    }
    return br;
  }

  /**
   * Odgađa sve NEotkazane rezervacije za zadani aranžman. Korisno za scenarije gdje se aranžman
   * odgađa/otkazuje, a rezervacije prelaze u stanje ODGOĐENA.
   *
   * @param oznaka the oznaka
   * @param vrijeme the vrijeme
   * @return the int
   */
  public int odgodiSveRezervacijeZaAranzman(String oznaka, LocalDateTime vrijeme) {
    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznaka);
    if (a == null) {
      return 0;
    }
    int br = 0;
    for (Rezervacija r : a.getRezervacije()) {
      // ne diramo već otkazane/odgođene
      String s = r.nazivStanja();
      String u = s == null ? "" : s.toUpperCase();
      if (u.contains("OTKAZ") || u.contains("ODGOĐ") || u.contains("ODGOD")) {
        continue;
      }
      r.odgodi(vrijeme);
      br++;
    }
    // nakon toga kvote nema smisla (sve su odgođene/neaktivne),
    // ali možeš ako želiš pozvati rekalkulaciju s 0/0
    a.azurirajStanje(0, 0);
    return br;
  }

  /**
   * Odgađa jednu rezervaciju osobe za zadani aranžman.
   *
   * @param ime the ime
   * @param prezime the prezime
   * @param oznakaAranzmana the oznaka aranzmana
   * @param vrijemeOdgode the vrijeme odgode
   * @return true ako je pronađena i odgođena
   */
  public boolean odgodiRezervaciju(String ime, String prezime, String oznakaAranzmana,
      LocalDateTime vrijemeOdgode) {
    if (ime == null || prezime == null || oznakaAranzmana == null) {
      return false;
    }
    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznakaAranzmana);
    if (a == null) {
      return false;
    }

    for (Rezervacija r : a.getRezervacije()) {
      if (ime.equalsIgnoreCase(r.getIme()) && prezime.equalsIgnoreCase(r.getPrezime())
          && oznakaAranzmana.equalsIgnoreCase(r.getOznakaAranzmana())) {

        r.odgodi(vrijemeOdgode);
        return true;
      }
    }
    return false;
  }

  /**
   * Obrisi sve rezervacije fizicki.
   *
   * @return the int
   */
  public int obrisiSveRezervacijeFizicki() {
    int obrisano = 0;

    for (Aranzman a : upraviteljAranzmanima.svi()) {
      obrisano += a.obrisiSveRezervacijeFizicki();

      // nakon brisanja rezervacija, aranžman treba biti konzistentan
      // ako je aranžman otkazan, nemoj ga "oživljavati"
      String st = a.nazivStanja();
      boolean otkazan = st != null && st.toUpperCase().contains("OTKAZ");

      if (!otkazan) {
        // nema prijava -> u pripremi (ovisno o tvojoj implementaciji)
        a.azurirajStanje(0, 0);
      }
    }

    return obrisano;
  }

  /**
   * Postoji identicna.
   *
   * @param ime the ime
   * @param prezime the prezime
   * @param oznakaAranzmana the oznaka aranzmana
   * @param datumVrijeme the datum vrijeme
   * @return true, if successful
   */
  public boolean postojiIdenticna(String ime, String prezime, String oznakaAranzmana,
      LocalDateTime datumVrijeme) {

    if (ime == null || prezime == null || oznakaAranzmana == null || datumVrijeme == null) {
      return false;
    }

    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznakaAranzmana);
    if (a == null)
      return false;

    for (Rezervacija r : a.getRezervacije()) {
      if (r == null)
        continue;

      boolean isti = ime.equalsIgnoreCase(r.getIme()) && prezime.equalsIgnoreCase(r.getPrezime())
          && oznakaAranzmana.equalsIgnoreCase(r.getOznakaAranzmana())
          && datumVrijeme.equals(r.getDatumVrijeme());

      if (isti)
        return true;
    }
    return false;
  }

  /**
   * Dodaj ako ne postoji.
   *
   * @param r the r
   * @return true, if successful
   */
  public boolean dodajAkoNePostoji(Rezervacija r) {
    if (r == null)
      return false;

    if (postojiIdenticna(r.getIme(), r.getPrezime(), r.getOznakaAranzmana(), r.getDatumVrijeme())) {
      return false;
    }

    dodaj(r); // tvoja postojeća metoda koja dodaje u Aranzman
    return true;
  }



}
