package edu.unizg.foi.uzdiz.ibeusan20.logika;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;

/**
 * Upravlja turističkim aranžmanima.
 * <p>
 * Ne zna ništa o rezervacijama (Composite se nalazi u klasi {@link Aranzman}),
 * ovdje je samo pretraga i filtriranje.
 * </p>
 */
public class UpraviteljAranzmanima {

  private final List<Aranzman> aranzmani = new ArrayList<>();

  /**
   * Instancira upravitelja s početnim popisom aranžmana.
   *
   * @param pocetniAranzmani lista aranžmana
   */
  public UpraviteljAranzmanima(List<Aranzman> pocetniAranzmani) {
    if (pocetniAranzmani != null) {
      this.aranzmani.addAll(pocetniAranzmani);
    }
  }

  /**
   * Broj svih aranžmana.
   *
   * @return broj aranžmana
   */
  public int brojAranzmana() {
    return aranzmani.size();
  }

  /**
   * Vraća sve aranžmane (nepromjenjiva lista).
   *
   * @return lista svih aranžmana
   */
  public List<Aranzman> svi() {
    return Collections.unmodifiableList(aranzmani);
  }

  /**
   * Pronalazi aranžman po oznaci.
   *
   * @param oznaka oznaka aranžmana
   * @return pronađeni aranžman ili {@code null}
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
   * Filtrira aranžmane po rasponu datuma.
   *
   * @param datumOd početni datum (može biti {@code null})
   * @param datumDo završni datum (može biti {@code null})
   * @return filtrirana lista aranžmana
   */
  public List<Aranzman> filtrirajPoRasponu(LocalDate datumOd, LocalDate datumDo) {
    if (datumOd == null && datumDo == null) {
      return svi();
    }
    List<Aranzman> rezultat = new ArrayList<>();
    for (Aranzman a : aranzmani) {
      if (jeUnutarRaspona(a, datumOd, datumDo)) {
        rezultat.add(a);
      }
    }
    return rezultat;
  }

  private boolean jeUnutarRaspona(Aranzman a, LocalDate od, LocalDate d0) {
    LocalDate pocetak = a.getPocetniDatum();
    LocalDate kraj = a.getZavrsniDatum();

    if (od != null && (pocetak == null || pocetak.isBefore(od))) {
      return false;
    }
    if (d0 != null && (kraj == null || kraj.isAfter(d0))) {
      return false;
    }
    return true;
  }
}
