package edu.unizg.foi.uzdiz.ibeusan20.spremiste;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.Rezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeAktivnaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeNaCekanjuRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeNovaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOdgodenaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeOtkazanaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjePrimljenaRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.model.stanja.StanjeRezervacije;

public final class SpremisteAranzmana {

  private static final SpremisteAranzmana INSTANCA = new SpremisteAranzmana();

  // po oznaci aranžmana drži se stog mementa
  private final Map<String, Deque<AranzmanMemento>> spremiste = new HashMap<>();

  private SpremisteAranzmana() {}

  public static SpremisteAranzmana instanca() {
    return INSTANCA;
  }

  public synchronized int brojSpremljenih(String oznaka) {
    if (oznaka == null) return 0;
    Deque<AranzmanMemento> st = spremiste.get(oznaka.trim().toUpperCase());
    return st == null ? 0 : st.size();
  }

  public synchronized void spremi(Aranzman a) {
    if (a == null || a.getOznaka() == null) return;

    String key = a.getOznaka().trim().toUpperCase();

    List<RezervacijaSnapshot> snap = new ArrayList<>();
    for (Rezervacija r : a.getRezervacije()) {
      if (r == null) continue;
      snap.add(new RezervacijaSnapshot(
          r.getIme(),
          r.getPrezime(),
          r.getOznakaAranzmana(),
          r.getDatumVrijeme(),
          r.nazivStanja(),
          r.getDatumVrijemeOtkaza()
      ));
    }

    AranzmanMemento m = new AranzmanMemento(
        key,
        a.nazivStanja(),
        LocalDateTime.now(),
        List.copyOf(snap)
    );

    spremiste.computeIfAbsent(key, k -> new ArrayDeque<>()).push(m);
  }

  /** Uzmi i ukloni zadnje spremljeno stanje. Vraća null ako nema ništa. */
  public synchronized AranzmanMemento uzmiZadnji(String oznaka) {
    if (oznaka == null) return null;
    String key = oznaka.trim().toUpperCase();
    Deque<AranzmanMemento> st = spremiste.get(key);
    if (st == null || st.isEmpty()) return null;
    AranzmanMemento m = st.pop();
    if (st.isEmpty()) spremiste.remove(key);
    return m;
  }

  /** Primijeni memento na postojeći aranžman (rezervacije + stanje). */
  public void vratiStanje(Aranzman a, AranzmanMemento m) {
    if (a == null || m == null) return;

    a.obrisiSveRezervacijeFizicki();

    // vrati rezervacije
    if (m.rezervacije() != null) {
      for (RezervacijaSnapshot rs : m.rezervacije()) {
        if (rs == null) continue;

        StanjeRezervacije st = mapirajStanjeRezervacije(rs.stanjeNaziv());
        Rezervacija r = new Rezervacija(
            rs.ime(),
            rs.prezime(),
            rs.oznakaAranzmana(),
            rs.datumVrijeme(),
            st,
            rs.datumVrijemeOtkaza()
        );
        a.dodajRezervaciju(r);
      }
    }

    // vrati stanje aranžmana
    String stAr = (m.stanjeAranzmana() == null) ? "" : m.stanjeAranzmana().trim().toLowerCase();
    if (stAr.contains("otkazan")) {
      a.postaviOtkazan();
      return;
    }

    // brojanje aktivnih/prijava
    int prijave = 0;
    int aktivne = 0;

    for (Rezervacija r : a.getRezervacije()) {
      if (r == null) continue;
      if (r.brojiSeUKvotu()) prijave++;
      if (r.jeAktivna()) aktivne++;
    }

    a.azurirajStanje(aktivne, prijave);
  }

  private StanjeRezervacije mapirajStanjeRezervacije(String naziv) {
    String n = (naziv == null) ? "" : naziv.trim().toLowerCase();

    if (n.contains("nova")) return StanjeNovaRezervacija.instanca();
    if (n.contains("primlj")) return StanjePrimljenaRezervacija.instanca();
    if (n.contains("aktiv")) return StanjeAktivnaRezervacija.instanca();
    if (n.contains("ček") || n.contains("cek")) return StanjeNaCekanjuRezervacija.instanca();
    if (n.contains("odgo") || n.contains("odg")) return StanjeOdgodenaRezervacija.instanca();
    if (n.contains("otkaz")) return StanjeOtkazanaRezervacija.instanca();

    // fallback
    return StanjePrimljenaRezervacija.instanca();
  }
}
