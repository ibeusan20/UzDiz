package edu.unizg.foi.uzdiz.ibeusan20.logika.ogranicenja;

import java.time.LocalDateTime;
import java.time.LocalTime;
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

public class StrategijaJdr implements StrategijaOgranicenjaRezervacija {

  @Override
  public void primijeni(UpraviteljAranzmanima upraviteljAranzmanima) {
    if (upraviteljAranzmanima == null) {
      return;
    }

    Map<String, List<Rezervacija>> poOsobi = grupirajPoOsobi(upraviteljAranzmanima);

    for (List<Rezervacija> lista : poOsobi.values()) {
      primijeniZaOsobu(lista, upraviteljAranzmanima);
    }
  }

  private Map<String, List<Rezervacija>> grupirajPoOsobi(UpraviteljAranzmanima ua) {
    Map<String, List<Rezervacija>> poOsobi = new HashMap<>();

    for (Aranzman a : ua.svi()) {
      for (Rezervacija r : a.dohvatiSveRezervacije()) {
        String kljuc = kljucOsobe(r);
        poOsobi.computeIfAbsent(kljuc, k -> new ArrayList<>()).add(r);
      }
    }
    return poOsobi;
  }

  private void primijeniZaOsobu(List<Rezervacija> lista, UpraviteljAranzmanima ua) {
    List<Rezervacija> aktivne = filtrirajAktivne(lista);

    if (aktivne.isEmpty()) {
      oslobodiSveOdgodene(lista);
      return;
    }

    aktivne.sort(Comparator.comparing(Rezervacija::getDatumVrijeme,
        Comparator.nullsLast(Comparator.naturalOrder())));

    for (int i = 0; i < aktivne.size(); i++) {
      Rezervacija glavna = aktivne.get(i);
      Aranzman aGlavna = ua.pronadiPoOznaci(glavna.getOznakaAranzmana());
      if (aGlavna == null) {
        continue;
      }

      LocalDateTime gOd = pocetak(aGlavna);
      LocalDateTime gDo = kraj(aGlavna);

      for (int j = i + 1; j < aktivne.size(); j++) {
        Rezervacija druga = aktivne.get(j);
        Aranzman aDruga = ua.pronadiPoOznaci(druga.getOznakaAranzmana());
        if (aDruga == null) {
          continue;
        }

        if (preklapaSe(gOd, gDo, pocetak(aDruga), kraj(aDruga))) {
          druga.postaviStanje(StanjeOdgodenaRezervacija.instanca());
        }
      }
    }

    oslobodiOdgodeneKojeViseNePreklapaju(lista, aktivne, ua);
  }

  private List<Rezervacija> filtrirajAktivne(List<Rezervacija> lista) {
    List<Rezervacija> aktivne = new ArrayList<>();

    for (Rezervacija r : lista) {
      if (r == null) {
        continue;
      }
      if (r.getStanje() instanceof StanjeOtkazanaRezervacija) {
        continue;
      }
      if (r.jeAktivna()) {
        aktivne.add(r);
      }
    }
    return aktivne;
  }

  private void oslobodiSveOdgodene(List<Rezervacija> lista) {
    for (Rezervacija r : lista) {
      if (r != null && r.getStanje() instanceof StanjeOdgodenaRezervacija) {
        r.postaviStanje(StanjePrimljenaRezervacija.instanca());
      }
    }
  }

  private void oslobodiOdgodeneKojeViseNePreklapaju(List<Rezervacija> lista,
      List<Rezervacija> aktivne, UpraviteljAranzmanima ua) {

    for (Rezervacija r : lista) {
      if (r == null) {
        continue;
      }
      if (!(r.getStanje() instanceof StanjeOdgodenaRezervacija)) {
        continue;
      }

      Aranzman ar = ua.pronadiPoOznaci(r.getOznakaAranzmana());
      if (ar == null) {
        continue;
      }

      boolean preklapa = preklapaSeSAktivnom(ar, aktivne, ua);
      if (!preklapa) {
        r.postaviStanje(StanjePrimljenaRezervacija.instanca());
      }
    }
  }

  private boolean preklapaSeSAktivnom(Aranzman ar, List<Rezervacija> aktivne,
      UpraviteljAranzmanima ua) {

    LocalDateTime od = pocetak(ar);
    LocalDateTime d0 = kraj(ar);

    for (Rezervacija akt : aktivne) {
      Aranzman a = ua.pronadiPoOznaci(akt.getOznakaAranzmana());
      if (a == null) {
        continue;
      }
      if (preklapaSe(od, d0, pocetak(a), kraj(a))) {
        return true;
      }
    }
    return false;
  }

  private String kljucOsobe(Rezervacija r) {
    String ime = (r == null || r.getIme() == null) ? "" : r.getIme().trim();
    String prez = (r == null || r.getPrezime() == null) ? "" : r.getPrezime().trim();
    return (ime + "|" + prez).toLowerCase();
  }

  private LocalDateTime pocetak(Aranzman a) {
    if (a == null || a.getPocetniDatum() == null) {
      return LocalDateTime.MIN;
    }
    LocalTime t = (a.getVrijemeKretanja() != null) ? a.getVrijemeKretanja() : LocalTime.MIN;
    return LocalDateTime.of(a.getPocetniDatum(), t);
  }

  private LocalDateTime kraj(Aranzman a) {
    if (a == null || a.getZavrsniDatum() == null) {
      return LocalDateTime.MAX;
    }
    LocalTime t = (a.getVrijemePovratka() != null) ? a.getVrijemePovratka() : LocalTime.MAX;
    return LocalDateTime.of(a.getZavrsniDatum(), t);
  }

  private boolean preklapaSe(LocalDateTime od1, LocalDateTime do1, LocalDateTime od2,
      LocalDateTime do2) {

    if (do1.isBefore(od2) || do2.isBefore(od1)) {
      return false;
    }
    return true;
  }
}
