package edu.unizg.foi.uzdiz.ibeusan20.logika;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.KontekstIspisa;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

// TODO: Auto-generated Javadoc
/**
 * Upravlja kolekcijom turističkih aranžmana.
 * 
 * - čuva sve aranžmane - omogućuje pretragu po oznaci - filtriranje po rasponu datuma - vraćanje
 * liste uz poštivanje IP (N/S) poretka
 */
public class UpraviteljAranzmanima {

  /** The aranzmani. */
  private final List<Aranzman> aranzmani = new ArrayList<>();

  /**
   * Instantiates a new upravitelj aranzmanima.
   *
   * @param pocetni the pocetni
   */
  public UpraviteljAranzmanima(List<Aranzman> pocetni) {
    if (pocetni != null) {
      aranzmani.addAll(pocetni);
    }
  }

  /**
   * Vraća sve aranžmane. Poštuje IP poredak (N – kronološki, S – obrnuto).
   *
   * @return the list
   */
  public List<Aranzman> svi() {
    List<Aranzman> kopija = new ArrayList<>(aranzmani);
    // ovdje bi po potrebi mogao sortirati po datumu početka,
    // ali ako već imaš željeni redoslijed, ne diramo
    if (KontekstIspisa.jeObrnuto()) {
      Collections.reverse(kopija);
    }
    return kopija;
  }

  /**
   * Broj aranžmana.
   *
   * @return the int
   */
  public int brojAranzmana() {
    return aranzmani.size();
  }

  /**
   * Pronalazi aranžman po oznaci.
   *
   * @param oznaka the oznaka
   * @return the aranzman
   */
  public Aranzman pronadiPoOznaci(String oznaka) {
    if (oznaka == null) {
      return null;
    }
    for (Aranzman a : aranzmani) {
      if (oznaka.equalsIgnoreCase(a.getOznaka())) {
        return a;
      }
    }
    return null;
  }

  /**
   * Filtrira aranžmane po rasponu datuma (uključivo). Poštuje IP poredak pri vraćanju liste.
   *
   * @param od the od
   * @param d0 the d 0
   * @return the list
   */
  public List<Aranzman> filtrirajPoRasponu(LocalDate od, LocalDate d0) {
    List<Aranzman> rezultat = new ArrayList<>();
    for (Aranzman a : aranzmani) {
      LocalDate poc = a.getPocetniDatum();
      LocalDate kraj = a.getZavrsniDatum();
      if (poc == null || kraj == null) {
        continue;
      }
      boolean nakonOd = (od == null) || !poc.isBefore(od);
      boolean prijeDo = (d0 == null) || !kraj.isAfter(d0);
      if (nakonOd && prijeDo) {
        rezultat.add(a);
      }
    }

    //if (KontekstIspisa.jeObrnuto()) {
    //  Collections.reverse(rezultat);
    //}
    return rezultat;
  }

  /**
   * Interna lista (ako ti negdje treba). Pazi da ne mijenjaš izvornu kolekciju izvana.
   *
   * @return the unutarnja lista
   */
  public List<Aranzman> getUnutarnjaLista() {
    return aranzmani;
  }

  /**
   * Dodaj.
   *
   * @param a the a
   */
  public void dodaj(Aranzman a) {
    if (a == null)
      return;
    if (pronadiPoOznaci(a.getOznaka()) != null) {
      // već postoji – možeš ili ignorirati ili baciti iznimku
      return;
    }
    this.aranzmani.add(a);
  }

  /**
   * Obrisi sve aranzmane fizicki.
   *
   * @return the int
   */
  public int obrisiSveAranzmaneFizicki() {
    int n = aranzmani.size();
    aranzmani.clear();
    return n;
  }
  
  /** The Constant PO_POCETKU. */
  private static final Comparator<Aranzman> PO_POCETKU = Comparator
      .comparing(UpraviteljAranzmanima::pocetakAranzmana, Comparator.nullsLast(Comparator.naturalOrder()))
      .thenComparing(a -> a.getOznaka() == null ? "" : a.getOznaka(), String.CASE_INSENSITIVE_ORDER);

  /**
   * Pocetak aranzmana.
   *
   * @param a the a
   * @return the local date time
   */
  private static LocalDateTime pocetakAranzmana(Aranzman a) {
    if (a == null || a.getPocetniDatum() == null) return null;
    LocalTime t = (a.getVrijemeKretanja() != null) ? a.getVrijemeKretanja() : LocalTime.MIN;
    return LocalDateTime.of(a.getPocetniDatum(), t);
  }

  /**
   * Sortiraj za ispis.
   *
   * @param ulaz the ulaz
   * @return the list
   */
  private List<Aranzman> sortirajZaIspis(List<Aranzman> ulaz) {
    List<Aranzman> lista = new ArrayList<>(ulaz == null ? List.of() : ulaz);
    lista.sort(PO_POCETKU);
    if (KontekstIspisa.jeObrnuto()) {
      Collections.reverse(lista);
    }
    return lista;
  }

  /**
   * Svi aranžmani, sortirani po početku + IP poredak.
   *
   * @return the list
   */
  public List<Aranzman> sviZaIspis() {
    return sortirajZaIspis(svi()); // koristi tvoju postojeću metodu
  }

  /**
   * Filtrirani aranžmani, sortirani po početku + IP poredak.
   *
   * @param od the od
   * @param d0 the d 0
   * @return the list
   */
  public List<Aranzman> filtrirajPoRasponuZaIspis(LocalDate od, LocalDate d0) {
    return sortirajZaIspis(filtrirajPoRasponu(od, d0)); // koristi tvoju postojeću metodu
  }


}
