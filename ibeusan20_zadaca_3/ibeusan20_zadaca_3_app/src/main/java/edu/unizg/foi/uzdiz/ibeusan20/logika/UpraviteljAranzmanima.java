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

/**
 * Upravlja kolekcijom turističkih aranžmana.
 * 
 * - čuva sve aranžmane - omogućuje pretragu po oznaci - filtriranje po rasponu datuma - vraćanje
 * liste uz poštivanje IP (N/S) poretka
 */
public class UpraviteljAranzmanima {

  private final List<Aranzman> aranzmani = new ArrayList<>();

  public UpraviteljAranzmanima(List<Aranzman> pocetni) {
    if (pocetni != null) {
      aranzmani.addAll(pocetni);
    }
  }

  /**
   * Vraća sve aranžmane. Poštuje IP poredak (N – kronološki, S – obrnuto).
   */
  public List<Aranzman> svi() {
    List<Aranzman> kopija = new ArrayList<>(aranzmani);
    if (KontekstIspisa.jeObrnuto()) {
      Collections.reverse(kopija);
    }
    return kopija;
  }

  /**
   * Broj aranžmana.
   */
  public int brojAranzmana() {
    return aranzmani.size();
  }

  /**
   * Pronalazi aranžman po oznaci.
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
   * Filtrira aranžmane po rasponu datuma. Poštuje IP poredak pri vraćanju liste.
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

    return rezultat;
  }

  public List<Aranzman> getUnutarnjaLista() {
    return aranzmani;
  }

  public void dodaj(Aranzman a) {
    if (a == null)
      return;
    if (pronadiPoOznaci(a.getOznaka()) != null) {
      return;
    }
    this.aranzmani.add(a);
  }

  public int obrisiSveAranzmaneFizicki() {
    int n = aranzmani.size();
    aranzmani.clear();
    return n;
  }

  private static final Comparator<Aranzman> PO_POCETKU =
      Comparator.comparing(UpraviteljAranzmanima::pocetakAranzmana,
          Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(
              a -> a.getOznaka() == null ? "" : a.getOznaka(), String.CASE_INSENSITIVE_ORDER);

  private static LocalDateTime pocetakAranzmana(Aranzman a) {
    if (a == null || a.getPocetniDatum() == null)
      return null;
    LocalTime t = (a.getVrijemeKretanja() != null) ? a.getVrijemeKretanja() : LocalTime.MIN;
    return LocalDateTime.of(a.getPocetniDatum(), t);
  }

  private List<Aranzman> sortirajZaIspis(List<Aranzman> ulaz) {
    List<Aranzman> lista = new ArrayList<>(ulaz == null ? List.of() : ulaz);
    lista.sort(PO_POCETKU);
    if (KontekstIspisa.jeObrnuto()) {
      Collections.reverse(lista);
    }
    return lista;
  }

  /** Svi aranžmani, sortirani po početku + IP poredak. */
  public List<Aranzman> sviZaIspis() {
    return sortirajZaIspis(svi());
  }

  /** Filtrirani aranžmani, sortirani po početku + IP poredak. */
  public List<Aranzman> filtrirajPoRasponuZaIspis(LocalDate od, LocalDate d0) {
    return sortirajZaIspis(filtrirajPoRasponu(od, d0));
  }

  private int indexOfOznaka(String oznaka) {
    if (oznaka == null)
      return -1;
    for (int i = 0; i < aranzmani.size(); i++) {
      Aranzman a = aranzmani.get(i);
      if (a != null && oznaka.equalsIgnoreCase(a.getOznaka())) {
        return i;
      }
    }
    return -1;
  }

  /** Uklanja aranžman po oznaci, ako postoji. */
  public boolean ukloniPoOznaci(String oznaka) {
    int idx = indexOfOznaka(oznaka);
    if (idx < 0)
      return false;
    aranzmani.remove(idx);
    return true;
  }

  /** Dodaje novi aranžman ili zamjenjuje postojeći s istom oznakom. */
  public void dodajIliZamijeni(Aranzman novi) {
    if (novi == null)
      return;
    int idx = indexOfOznaka(novi.getOznaka());
    if (idx >= 0) {
      aranzmani.set(idx, novi);
    } else {
      aranzmani.add(novi);
    }
  }
}
