package edu.unizg.foi.uzdiz.ibeusan20.logika;

import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Upravljanje kolekcijom turističkih aranžmana.
 * <p>
 * Omogućuje dohvat, filtriranje i pretraživanje aranžmana prema oznaci ili
 * vremenskom rasponu.
 * </p>
 */
public class UpraviteljAranzmanima {
  private final List<Aranzman> aranzmani = new ArrayList<>();

  /**
   * Inicijalizira upravitelja s početnim popisom aranžmana.
   *
   * @param pocetni početni popis aranžmana (može biti {@code null})
   */
  public UpraviteljAranzmanima(List<Aranzman> pocetni) {
    if (pocetni != null) {
      aranzmani.addAll(pocetni);
    }
  }

  /** @return broj svih učitanih aranžmana */
  public int brojAranzmana() {
    return aranzmani.size();
  }

  /**
   * Dodaje nove aranžmane u postojeću kolekciju.
   *
   * @param novi lista novih aranžmana
   */
  public void dodajSve(List<Aranzman> novi) {
    if (novi != null) {
      aranzmani.addAll(novi);
    }
  }

  /**
   * Pronalazi aranžman prema oznaci (bez obzira na velika/mala slova).
   *
   * @param oznaka oznaka aranžmana
   * @return pronađeni aranžman ili {@code null} ako ne postoji
   */
  public Aranzman pronadiPoOznaci(String oznaka) {
    if (oznaka == null) {
      return null;
    }
    for (Aranzman a : aranzmani) {
      if (a.getOznaka() != null && a.getOznaka().equalsIgnoreCase(oznaka)) {
        return a;
      }
    }
    return null;
  }

  /** @return nova lista svih aranžmana */
  public List<Aranzman> svi() {
    return new ArrayList<>(aranzmani);
  }

  /**
   * Filtrira aranžmane prema početnom datumu unutar zadanog raspona.
   *
   * @param od početni datum
   * @param do_ završni datum
   * @return lista aranžmana čiji je početni datum unutar raspona
   */
  public List<Aranzman> filtrirajPoRasponu(LocalDate od, LocalDate do_) {
    List<Aranzman> rezultat = new ArrayList<>();
    for (Aranzman a : aranzmani) {
      LocalDate poc = a.getPocetniDatum();
      if (poc == null) {
        continue;
      }
      boolean unutar =
          (poc.isEqual(od) || poc.isAfter(od)) && (poc.isEqual(do_) || poc.isBefore(do_));
      if (unutar) {
        rezultat.add(a);
      }
    }
    return rezultat;
  }

  /**
   * Dohvaća početni i završni datum aranžmana prema oznaci.
   *
   * @param oznaka oznaka aranžmana
   * @return polje [pocetak, kraj] ili {@code null} ako aranžman ne postoji
   */
  public LocalDate[] dohvatiRasponZaOznaku(String oznaka) {
    Aranzman a = pronadiPoOznaci(oznaka);
    if (a == null) {
      return null;
    }
    return new LocalDate[] {a.getPocetniDatum(), a.getZavrsniDatum()};
  }
}
