package edu.unizg.foi.uzdiz.ibeusan20.logika.ogranicenja;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.unizg.foi.uzdiz.ibeusan20.logika.UpraviteljAranzmanima;
import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOdgodenaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOtkazanaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjePrimljenaRezervacija;

public class StrategijaVdr implements StrategijaOgranicenjaRezervacija {

  @Override
  public void primijeni(UpraviteljAranzmanima upraviteljAranzmanima) {
    if (upraviteljAranzmanima == null) {
      return;
    }

    for (Aranzman a : upraviteljAranzmanima.svi()) {
      primijeniZaAranzman(a);
    }
  }

  private void primijeniZaAranzman(Aranzman a) {
    if (a == null) {
      return;
    }

    int limit = izracunajLimit(a.getMaxPutnika());
    Map<String, List<Rezervacija>> poOsobi = grupirajPoOsobi(a.dohvatiSveRezervacije());

    for (List<Rezervacija> lista : poOsobi.values()) {
      primijeniLimit(lista, limit);
    }
  }

  private int izracunajLimit(int maxPutnika) {
    int limit = maxPutnika / 4;
    return Math.max(1, limit);
  }

  private Map<String, List<Rezervacija>> grupirajPoOsobi(List<Rezervacija> sve) {
    Map<String, List<Rezervacija>> poOsobi = new HashMap<>();

    for (Rezervacija r : sve) {
      if (r == null) {
        continue;
      }
      String kljuc = kljucOsobe(r);
      poOsobi.computeIfAbsent(kljuc, k -> new ArrayList<>()).add(r);
    }
    return poOsobi;
  }

  private void primijeniLimit(List<Rezervacija> lista, int limit) {
    List<Rezervacija> kandidati = filtrirajNeOtkazane(lista);

    kandidati.sort(Comparator.comparing(Rezervacija::getDatumVrijeme,
        Comparator.nullsLast(Comparator.naturalOrder())));

    for (int i = 0; i < kandidati.size(); i++) {
      Rezervacija r = kandidati.get(i);

      if (i < limit) {
        oslobodiAkoJeOdgodena(r);
      } else {
        r.postaviStanje(StanjeOdgodenaRezervacija.instanca());
      }
    }
  }

  private List<Rezervacija> filtrirajNeOtkazane(List<Rezervacija> lista) {
    List<Rezervacija> kandidati = new ArrayList<>();

    for (Rezervacija r : lista) {
      if (r == null) {
        continue;
      }
      if (r.getStanje() instanceof StanjeOtkazanaRezervacija) {
        continue;
      }
      kandidati.add(r);
    }
    return kandidati;
  }

  private void oslobodiAkoJeOdgodena(Rezervacija r) {
    if (r == null) {
      return;
    }
    if (r.getStanje() instanceof StanjeOdgodenaRezervacija) {
      r.postaviStanje(StanjePrimljenaRezervacija.instanca());
    }
  }

  private String kljucOsobe(Rezervacija r) {
    String ime = (r.getIme() == null) ? "" : r.getIme().trim();
    String prez = (r.getPrezime() == null) ? "" : r.getPrezime().trim();
    return (ime + "|" + prez).toLowerCase();
  }
}
