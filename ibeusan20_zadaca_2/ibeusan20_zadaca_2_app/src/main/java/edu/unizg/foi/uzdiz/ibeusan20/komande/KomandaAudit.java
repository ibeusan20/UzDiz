package edu.unizg.foi.uzdiz.ibeusan20.komande;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import edu.unizg.foi.uzdiz.ibeusan20.audit.AuditDnevnik;
import edu.unizg.foi.uzdiz.ibeusan20.audit.AuditStavka;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisAuditAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisAuditZbrojAdapter;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.IspisniRed;
import edu.unizg.foi.uzdiz.ibeusan20.ispisi.TablicniFormat;

// TODO: Auto-generated Javadoc
/**
 * The Class KomandaAudit.
 */
public class KomandaAudit implements Komanda {

  /** The argumenti. */
  private final String[] argumenti;

  /**
   * Instantiates a new komanda audit.
   *
   * @param argumenti the argumenti
   */
  public KomandaAudit(String... argumenti) {
    this.argumenti = argumenti == null ? new String[0] : argumenti;
  }

  /**
   * Izvrsi.
   *
   * @return true, if successful
   */
  @Override
  public boolean izvrsi() {
    String komandaTekst = "AUDIT";
    TablicniFormat tab = new TablicniFormat();

    if (argumenti.length >= 1) {
      String a0 = argumenti[0].trim();

      if (a0.equalsIgnoreCase("CLEAR")) {
        AuditDnevnik.instanca().obrisiSve();
        tab.ispisi("AUDIT CLEAR");
        tab.ispisi("Audit dnevnik je obrisan.\n");
        return true;
      }

      // AUDIT N
      try {
        int n = Integer.parseInt(a0);
        komandaTekst = "AUDIT " + n;

        List<AuditStavka> zadnjih = AuditDnevnik.instanca().zadnjih(n);
        ispisiTablicu(tab, komandaTekst, zadnjih);
        return true;
      } catch (NumberFormatException e) {
        tab.ispisi("Sintaksa: AUDIT [N|CLEAR]\n");
        return true;
      }
    }

    // AUDIT (sve)
    List<AuditStavka> sve = AuditDnevnik.instanca().sve();
    ispisiTablicu(tab, komandaTekst, sve);
    return true;
  }

  /**
   * Ispisi tablicu.
   *
   * @param tab the tab
   * @param komandaTekst the komanda tekst
   * @param stavke the stavke
   */
  private void ispisiTablicu(TablicniFormat tab, String komandaTekst, List<AuditStavka> stavke) {
    String nazivTablice = "Dnevnik izvršavanja komandi";

    if (stavke == null || stavke.isEmpty()) {
      tab.ispisi(komandaTekst);
      tab.ispisi(nazivTablice);
      tab.ispisi("Nema audit zapisa.\n");
      return;
    }

    // 1) tablica zapisa (kao do sad)
    List<IspisniRed> redovi = new ArrayList<>();
    for (AuditStavka s : stavke) redovi.add(new IspisAuditAdapter(s));
    tab.ispisiTablicu(komandaTekst, nazivTablice, redovi);

    // 2) sažetak po komandi (ukupno u dnevniku, ne samo prikazani)
    List<AuditStavka> sve = AuditDnevnik.instanca().sve();
    ispisiZbroj(tab, "AUDIT - Sažetak po komandi (ukupno)", sve);
  }

  /**
   * Ispisi zbroj.
   *
   * @param tab the tab
   * @param naslov the naslov
   * @param stavke the stavke
   */
  private void ispisiZbroj(TablicniFormat tab, String naslov, List<AuditStavka> stavke) {
    if (stavke == null || stavke.isEmpty()) {
      tab.ispisi(naslov);
      tab.ispisi("Nema podataka.\n");
      return;
    }

    Map<String, Integer> brojac = new java.util.HashMap<>();

    for (AuditStavka s : stavke) {
      String unos = (s == null || s.unos() == null) ? "" : s.unos().trim();
      String kljuc = izvuciKomandu(unos);
      brojac.put(kljuc, brojac.getOrDefault(kljuc, 0) + 1);
    }

    // sortiraj po nazivu komande
    List<String> kljucevi = new ArrayList<>(brojac.keySet());
    kljucevi.sort(String.CASE_INSENSITIVE_ORDER);

    List<IspisniRed> redovi = new ArrayList<>();
    for (String k : kljucevi) {
      redovi.add(new IspisAuditZbrojAdapter(k, brojac.get(k)));
    }

    tab.ispisiTablicu("", naslov, redovi);
  }

  /**
   * Izvuci komandu.
   *
   * @param unos the unos
   * @return the string
   */
  private String izvuciKomandu(String unos) {
    if (unos == null) return "?";
    String t = unos.trim();
    if (t.isEmpty()) return "?";
    String[] dijelovi = t.split("\\s+");
    return (dijelovi.length == 0) ? "?" : dijelovi[0].toUpperCase();
  }

}
