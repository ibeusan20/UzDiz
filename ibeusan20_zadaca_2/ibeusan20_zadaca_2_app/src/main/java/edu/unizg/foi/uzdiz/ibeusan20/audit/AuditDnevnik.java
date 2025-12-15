package edu.unizg.foi.uzdiz.ibeusan20.audit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AuditDnevnik {

  private static final AuditDnevnik INSTANCA = new AuditDnevnik();

  private final List<AuditStavka> stavke = new ArrayList<>();
  private int brojac = 0;

  // da ne raste beskonačno (bez fileova)
  private int maxZapisa = 2000;

  private AuditDnevnik() {}

  public static AuditDnevnik instanca() {
    return INSTANCA;
  }

  public synchronized void postaviMaxZapisa(int max) {
    if (max > 0) this.maxZapisa = max;
  }

  public synchronized void dodaj(LocalDateTime vrijeme, String unos, String komandaNaziv,
      String status, long trajanjeMs, String poruka) {

    brojac++;
    AuditStavka s = new AuditStavka(brojac, vrijeme, unos, komandaNaziv, status, trajanjeMs, poruka);
    stavke.add(s);

    if (stavke.size() > maxZapisa) {
      int visak = stavke.size() - maxZapisa;
      for (int i = 0; i < visak; i++) stavke.remove(0);
    }
  }

  public synchronized List<AuditStavka> sve() {
    return Collections.unmodifiableList(new ArrayList<>(stavke));
  }

  public synchronized List<AuditStavka> zadnjih(int n) {
    if (n <= 0) return List.of();
    if (n >= stavke.size()) return sve();
    return Collections.unmodifiableList(new ArrayList<>(stavke.subList(stavke.size() - n, stavke.size())));
  }

  public synchronized void obrisiSve() {
    stavke.clear();
  }

  public synchronized int broj() {
    return stavke.size();
  }
}
