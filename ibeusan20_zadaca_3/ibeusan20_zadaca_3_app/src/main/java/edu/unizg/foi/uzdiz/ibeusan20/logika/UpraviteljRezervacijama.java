package edu.unizg.foi.uzdiz.ibeusan20.logika;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
 * - ne čuva vlastitu kolekciju, koristi Aranzman.getRezervacije() - implementira State logiku i
 * kvote - poštuje IP poredak pri vraćanju listi za ispis
 */
public class UpraviteljRezervacijama {

  private final UpraviteljAranzmanima upraviteljAranzmanima;
  private final StrategijaOgranicenjaRezervacija strategija;

  public UpraviteljRezervacijama(UpraviteljAranzmanima upraviteljAranzmanima) {
    this(upraviteljAranzmanima, new StrategijaBezOgranicenja());
  }

  public UpraviteljRezervacijama(UpraviteljAranzmanima upraviteljAranzmanima,
      StrategijaOgranicenjaRezervacija strategija) {

    this.upraviteljAranzmanima = upraviteljAranzmanima;
    this.strategija = (strategija == null) ? new StrategijaBezOgranicenja() : strategija;
  }



  /**
   * Dodaje početne rezervacije u pripadajuće aranžmane
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
   * Dodaje novu rezervaciju u odgovarajući aranžman
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

  public void rekalkulirajSve() {
    for (int iter = 0; iter < 10; iter++) {
      Map<Rezervacija, String> prije = snapshotStanja();

      for (Aranzman a : upraviteljAranzmanima.svi()) {
        rekalkulirajZaAranzman(a.getOznaka(), a.getMinPutnika(), a.getMaxPutnika());
      }

      // pravilo preklapanja po osobi (ODGOĐENE)
      // primijeniPraviloPreklapanja();
      strategija.primijeni(upraviteljAranzmanima);

      Map<Rezervacija, String> poslije = snapshotStanja();
      if (jednako(prije, poslije)) {
        break; // stabilno
      }
    }
  }

  private Map<Rezervacija, String> snapshotStanja() {
    Map<Rezervacija, String> m = new IdentityHashMap<>();
    for (Aranzman a : upraviteljAranzmanima.svi()) {
      for (Rezervacija r : a.getRezervacije()) {
        m.put(r, r.nazivStanja());
      }
    }
    return m;
  }

  private boolean jednako(Map<Rezervacija, String> a, Map<Rezervacija, String> b) {
    if (a.size() != b.size())
      return false;
    for (Map.Entry<Rezervacija, String> e : a.entrySet()) {
      if (!Objects.equals(e.getValue(), b.get(e.getKey())))
        return false;
    }
    return true;
  }

  public void rekalkulirajZaAranzman(String oznaka, int minPutnika, int maxPutnika) {
    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznaka);
    if (a == null)
      return;

    String stanjePrije = a.nazivStanja();
    IdentityHashMap<Rezervacija, String> stanjaRezPrije = snimiStanja(a);

    // sortiraj kronološki (N)
    List<Rezervacija> sve = new ArrayList<>(a.getRezervacije());
    sve.sort(Comparator.comparing(Rezervacija::getDatumVrijeme,
        Comparator.nullsLast(Comparator.naturalOrder())));

    // kandidati za kvotu = oni koji se broje u kvotu (primljena/aktivna/čekanje)
    List<Rezervacija> kandidati = new ArrayList<>();
    for (Rezervacija r : sve) {
      if (r == null)
        continue;
      if (!r.brojiSeUKvotu())
        continue; // automatski izbaci otkazane/odgođene/nova
      kandidati.add(r);
    }

    int brojPrijava = kandidati.size();
    int brojAktivnih = 0;

    if (brojPrijava == 0) {
      // nema rezervacija koje se broje u kvotu
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

    // jednom ažurira stanje aranžmana
    a.azurirajStanje(brojAktivnih, brojPrijava);

    // obavijest!
    if (a.imaPretplata()) {
      String stanjePoslije = a.nazivStanja();

      List<String> detaljiRez = dohvatiDetaljePromjenaRezervacija(a, stanjaRezPrije);
      String opis = opisPromjeneStanjaDetaljno(stanjePrije, stanjePoslije, detaljiRez);

      if (opis != null && !opis.isBlank()) {
        a.obavijestiPretplatnike(opis);
      }
    }
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
      if (ime.equalsIgnoreCase(r.getIme()) && prezime.equalsIgnoreCase(r.getPrezime())
          && oznakaAranzmana.equalsIgnoreCase(r.getOznakaAranzmana()) && r.jeAktivna()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Pravilo preklapanja: Ako osoba ima više AKTIVNIH rezervacija koje se preklapaju po periodu
   * aranžmana.
   */
  // private void primijeniPraviloPreklapanja() { // u zadaći 3 se više ne koristi !!!!!!!
  // // grupira sve rezervacije po osobi
  // Map<String, List<Rezervacija>> poOsobi = new HashMap<>();
  //
  // for (Aranzman a : upraviteljAranzmanima.svi()) {
  // for (Rezervacija r : a.getRezervacije()) {
  // String key = (r.getIme() + "|" + r.getPrezime()).toLowerCase();
  // poOsobi.computeIfAbsent(key, k -> new ArrayList<>()).add(r);
  // }
  // }
  //
  // for (List<Rezervacija> lista : poOsobi.values()) {
  // // aktivne (neotkazane) rezervacije
  // List<Rezervacija> aktivne = new ArrayList<>();
  // for (Rezervacija r : lista) {
  // if (r.getStanje() instanceof StanjeOtkazanaRezervacija)
  // continue;
  // if (r.jeAktivna())
  // aktivne.add(r);
  // }
  //
  // // ako nema aktivnih, sve odgođene oslobađa (postaju primljene)
  // if (aktivne.isEmpty()) {
  // for (Rezervacija r : lista) {
  // if (r.getStanje() instanceof StanjeOdgodenaRezervacija) {
  // r.postaviStanje(StanjePrimljenaRezervacija.instanca());
  // }
  // }
  // continue;
  // }
  //
  // // sortira aktivne po datumu rezervacije (kronološki najranija ostaje aktivna)
  // aktivne.sort(Comparator.comparing(Rezervacija::getDatumVrijeme,
  // Comparator.nullsLast(Comparator.naturalOrder())));
  //
  // // za svaki konflikt preklapanja samo prva ostaje aktivna u tom konfliktu
  // for (int i = 0; i < aktivne.size(); i++) {
  // Rezervacija rGlavna = aktivne.get(i);
  // Aranzman aGlavna = upraviteljAranzmanima.pronadiPoOznaci(rGlavna.getOznakaAranzmana());
  // if (aGlavna == null)
  // continue;
  //
  // LocalDateTime gOd = pocetak(aGlavna);
  // LocalDateTime gDo = kraj(aGlavna);
  //
  // for (int j = i + 1; j < aktivne.size(); j++) {
  // Rezervacija rDruga = aktivne.get(j);
  // Aranzman aDruga = upraviteljAranzmanima.pronadiPoOznaci(rDruga.getOznakaAranzmana());
  // if (aDruga == null)
  // continue;
  //
  // LocalDateTime dOd = pocetak(aDruga);
  // LocalDateTime dDo = kraj(aDruga);
  // if (preklapaSe(gOd, gDo, dOd, dDo)) {
  // // kasnija aktivna ide u odgođenu
  // rDruga.postaviStanje(StanjeOdgodenaRezervacija.instanca());
  // }
  // }
  // }
  //
  // // dodatno: ako neka ODGOĐENA više ne preklapa nijednu aktivnu osobe onda ju vrati u PRIMLJENU
  // for (Rezervacija r : lista) {
  // if (!(r.getStanje() instanceof StanjeOdgodenaRezervacija))
  // continue;
  //
  // Aranzman ar = upraviteljAranzmanima.pronadiPoOznaci(r.getOznakaAranzmana());
  // if (ar == null)
  // continue;
  //
  // LocalDateTime od = pocetak(ar);
  // LocalDateTime d0 = kraj(ar);
  //
  // boolean preklapaSAktivnom = false;
  // for (Rezervacija akt : aktivne) {
  // Aranzman aa = upraviteljAranzmanima.pronadiPoOznaci(akt.getOznakaAranzmana());
  // if (aa == null)
  // continue;
  // if (preklapaSe(od, d0, pocetak(aa), kraj(aa))) {
  // preklapaSAktivnom = true;
  // break;
  // }
  // }
  //
  // if (!preklapaSAktivnom) {
  // r.postaviStanje(StanjePrimljenaRezervacija.instanca());
  // }
  // }
  // }
  // }

  // private LocalDateTime pocetak(Aranzman a) {
  // LocalDate d = a.getPocetniDatum();
  // LocalTime t = a.getVrijemeKretanja();
  // if (d == null)
  // return LocalDateTime.MIN;
  // return LocalDateTime.of(d, t != null ? t : LocalTime.MIN);
  // }
  //
  // private LocalDateTime kraj(Aranzman a) {
  // LocalDate d = a.getZavrsniDatum();
  // LocalTime t = a.getVrijemePovratka();
  // if (d == null)
  // return LocalDateTime.MAX;
  // return LocalDateTime.of(d, t != null ? t : LocalTime.MAX);
  // }
  //
  // private boolean preklapaSe(LocalDateTime od1, LocalDateTime do1, LocalDateTime od2,
  // LocalDateTime do2) {
  // if (do1.isBefore(od2) || do2.isBefore(od1))
  // return false;
  // return true;
  // }

  /**
   * Provjerava ima li osoba neku aktivnu rezervaciju čiji se period preklapa s aranžmanom zadane
   * oznake.
   * 
   * Parametar UpraviteljAranzmanima se ignorira (isti je kao interni), ostavljen je radi postojećeg
   * potpisa metode.
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

  private boolean preklapaSe(LocalDate od1, LocalDate do1, LocalDate od2, LocalDate do2) {
    // nema preklapanja samo ako je jedan interval potpuno prije drugog
    if (do1.isBefore(od2) || do2.isBefore(od1)) {
      return false;
    }
    return true;
  }

  /**
   * Otkazuje rezervaciju osobe za zadani aranžman (ORTA).
   */
  public boolean otkaziRezervaciju(String ime, String prezime, String oznakaAranzmana) {
    if (ime == null || prezime == null || oznakaAranzmana == null) {
      return false;
    }

    Aranzman a = upraviteljAranzmanima.pronadiPoOznaci(oznakaAranzmana);
    if (a == null) {
      return false;
    }

    List<Rezervacija> kandidati = dohvatiKandidateZaOtkazivanje(a, ime, prezime, oznakaAranzmana);
    if (kandidati.isEmpty()) {
      return false;
    }

    kandidati.sort(Comparator.comparing(Rezervacija::getDatumVrijeme,
        Comparator.nullsLast(Comparator.naturalOrder())));

    Rezervacija aktivna = null;
    Rezervacija primljena = null;
    Rezervacija cekanje = null;
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
      } else if (r.getStanje() instanceof StanjeNaCekanjuRezervacija) {
        if (cekanje == null) {
          cekanje = r;
        }
      } else if (jeOdgodena(r)) {
        if (odgodena == null) {
          odgodena = r;
        }
      }
    }

    LocalDateTime sada = LocalDateTime.now();

    if (aktivna != null && odgodena != null) {
      String prijeAkt = aktivna.nazivStanja();
      aktivna.otkazi(sada);
      obavijestiPretplatnikeAkoTreba(a,
          "Rezervacija osobe " + imePrezime(aktivna) + ": " + prijeAkt + " -> otkazana");

      // odgođena prelazi u aktivnu
      String prijeOdg = odgodena.nazivStanja();
      odgodena.postaviStanje(StanjeAktivnaRezervacija.instanca());
      obavijestiPretplatnikeAkoTreba(a, "Rezervacija osobe " + imePrezime(odgodena) + ": "
          + prijeOdg + " -> " + odgodena.nazivStanja());

      rekalkulirajZaAranzman(a.getOznaka(), a.getMinPutnika(), a.getMaxPutnika());
      return true;
    }


    if (aktivna != null) {
      String prije = aktivna.nazivStanja();
      aktivna.otkazi(sada);
      obavijestiPretplatnikeAkoTreba(a,
          "Rezervacija osobe " + imePrezime(aktivna) + ": " + prije + " -> otkazana");

      rekalkulirajZaAranzman(a.getOznaka(), a.getMinPutnika(), a.getMaxPutnika());
      return true;
    }


    if (primljena != null) {
      String prije = primljena.nazivStanja();
      primljena.otkazi(sada);
      obavijestiPretplatnikeAkoTreba(a,
          "Rezervacija osobe " + imePrezime(primljena) + ": " + prije + " -> otkazana");

      rekalkulirajZaAranzman(a.getOznaka(), a.getMinPutnika(), a.getMaxPutnika());
      return true;
    }


    if (cekanje != null) {
      String prije = cekanje.nazivStanja();
      cekanje.otkazi(sada);
      obavijestiPretplatnikeAkoTreba(a,
          "Rezervacija osobe " + imePrezime(cekanje) + ": " + prije + " -> otkazana");

      rekalkulirajZaAranzman(a.getOznaka(), a.getMinPutnika(), a.getMaxPutnika());
      return true;
    }
    return false;
  }

  private List<Rezervacija> dohvatiKandidateZaOtkazivanje(Aranzman a, String ime, String prezime,
      String oznakaAranzmana) {

    List<Rezervacija> kandidati = new ArrayList<>();

    for (Rezervacija r : a.getRezervacije()) {
      if (r == null) {
        continue;
      }

      boolean isti = ime.equalsIgnoreCase(r.getIme());
      isti = isti && prezime.equalsIgnoreCase(r.getPrezime());
      isti = isti && oznakaAranzmana.equalsIgnoreCase(r.getOznakaAranzmana());

      if (!isti) {
        continue;
      }

      if (r.getStanje() instanceof StanjeOtkazanaRezervacija) {
        continue;
      }

      kandidati.add(r);
    }

    return kandidati;
  }

  private boolean jeOdgodena(Rezervacija r) {
    if (r == null) {
      return false;
    }
    String ns = r.nazivStanja();
    if (ns == null) {
      return false;
    }
    String u = ns.toUpperCase();
    return u.contains("ODGOĐ") || u.contains("ODGOD");
  }


  /**
   * Vraća rezervacije za aranžman uz filtriranje po "vrstama" (PAČO).
   * 
   * IP (N/S) poredak se primjenjuje na kraju.
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
    boolean fD = filter.contains("OD") || filter.contains("D");

    // ako je string filtera ne-prazan onda ima filtere
    // i kad su svi gore false (npr. "X") to se tretira kao "ništa se ne poklapa", a NE "bez
    // filtera"
    boolean imaFiltera = !filter.isBlank();

    List<Rezervacija> rezultat = new ArrayList<>();
    for (Rezervacija r : a.getRezervacije()) {
      String ns = r.nazivStanja();
      String u = ns == null ? "" : ns.toUpperCase();

      // bez filtera - vrati sve
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
   */
  public int brojRezervacija() {
    int br = 0;
    for (Aranzman a : upraviteljAranzmanima.svi()) {
      br += a.getRezervacije().size();
    }
    return br;
  }

  /**
   * Odgađa sve neotkazane rezervacije za zadani aranžman.
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
    a.azurirajStanje(0, 0);
    return br;
  }

  /**
   * Odgađa jednu rezervaciju osobe za zadani aranžman.
   *
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

  public boolean dodajAkoNePostoji(Rezervacija r) {
    if (r == null)
      return false;

    if (postojiIdenticna(r.getIme(), r.getPrezime(), r.getOznakaAranzmana(), r.getDatumVrijeme())) {
      return false;
    }

    dodaj(r);
    return true;
  }

  private IdentityHashMap<Rezervacija, String> snimiStanja(Aranzman a) {
    IdentityHashMap<Rezervacija, String> m = new IdentityHashMap<>();
    for (Rezervacija r : a.getRezervacije()) {
      if (r != null) {
        m.put(r, r.nazivStanja());
      }
    }
    return m;
  }

  private List<String> dohvatiDetaljePromjenaRezervacija(Aranzman a,
      IdentityHashMap<Rezervacija, String> prije) {

    List<String> detalji = new ArrayList<>();
    if (a == null || prije == null) {
      return detalji;
    }

    for (Rezervacija r : a.getRezervacije()) {
      if (r == null)
        continue;

      String staro = prije.get(r);
      String novo = r.nazivStanja();

      if (staro == null || novo == null)
        continue;
      if (staro.equals(novo))
        continue;

      String osoba = ((r.getIme() == null) ? "" : r.getIme()) + " "
          + ((r.getPrezime() == null) ? "" : r.getPrezime());
      osoba = osoba.trim();

      String dv = FormatDatuma.formatiraj(r.getDatumVrijeme());

      String dio = "Rezervacija osobe " + osoba + (dv.isEmpty() ? "" : " (" + dv + ")") + ": "
          + staro + " -> " + novo;

      detalji.add(dio);
    }

    return detalji;
  }

  private String opisPromjeneStanjaDetaljno(String prije, String poslije, List<String> detaljiRez) {
    StringBuilder sb = new StringBuilder();

    if (prije != null && poslije != null && !prije.equals(poslije)) {
      sb.append("Stanje aranžmana: '").append(prije).append("' -> '").append(poslije).append("'");
    }

    if (detaljiRez != null && !detaljiRez.isEmpty()) {
      if (sb.length() > 0)
        sb.append("; ");
      sb.append("Promjene statusa rezervacija: ").append(detaljiRez.size());
      sb.append(" [").append(String.join("; ", detaljiRez)).append("]");
    }

    return sb.toString();
  }


  private void obavijestiPretplatnikeAkoTreba(Aranzman a, String opisPromjene) {
    if (a == null)
      return;
    if (opisPromjene == null || opisPromjene.isBlank())
      return;

    if (a.imaPretplata()) {
      a.obavijestiPretplatnike(opisPromjene);
    }
  }

  private String imePrezime(Rezervacija r) {
    if (r == null)
      return "";
    String i = (r.getIme() == null) ? "" : r.getIme().trim();
    String p = (r.getPrezime() == null) ? "" : r.getPrezime().trim();
    return (i + " " + p).trim();
  }

}
