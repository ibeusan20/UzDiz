package edu.unizg.foi.uzdiz.ibeusan20.logika;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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
    // ovdje bi po potrebi mogao sortirati po datumu početka,
    // ali ako već imaš željeni redoslijed, ne diramo
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
   * Filtrira aranžmane po rasponu datuma (uključivo). Poštuje IP poredak pri vraćanju liste.
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

    if (KontekstIspisa.jeObrnuto()) {
      Collections.reverse(rezultat);
    }
    return rezultat;
  }

  /**
   * Interna lista (ako ti negdje treba). Pazi da ne mijenjaš izvornu kolekciju izvana.
   */
  public List<Aranzman> getUnutarnjaLista() {
    return aranzmani;
  }

  public void dodaj(Aranzman a) {
    if (a == null)
      return;
    if (pronadiPoOznaci(a.getOznaka()) != null) {
      // već postoji – možeš ili ignorirati ili baciti iznimku
      return;
    }
    this.aranzmani.add(a);
  }

  public int obrisiSveAranzmaneFizicki() {
    int n = aranzmani.size();
    aranzmani.clear();
    return n;
  }

}
