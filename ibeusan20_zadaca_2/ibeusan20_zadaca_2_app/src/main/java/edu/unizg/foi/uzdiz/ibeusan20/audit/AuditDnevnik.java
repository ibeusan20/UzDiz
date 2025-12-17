package edu.unizg.foi.uzdiz.ibeusan20.audit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class AuditDnevnik.
 */
public final class AuditDnevnik {

  /** The Constant INSTANCA. */
  private static final AuditDnevnik INSTANCA = new AuditDnevnik();

  /** The stavke. */
  private final List<AuditStavka> stavke = new ArrayList<>();
  
  /** The brojac. */
  private int brojac = 0;

  /** The max zapisa. */
  // da ne raste beskonačno (bez fileova)
  private int maxZapisa = 2000;

  /**
   * Instantiates a new audit dnevnik.
   */
  private AuditDnevnik() {}

  /**
   * Instanca.
   *
   * @return the audit dnevnik
   */
  public static AuditDnevnik instanca() {
    return INSTANCA;
  }

  /**
   * Postavi max zapisa.
   *
   * @param max the max
   */
  public synchronized void postaviMaxZapisa(int max) {
    if (max > 0) this.maxZapisa = max;
  }

  /**
   * Dodaj.
   *
   * @param vrijeme the vrijeme
   * @param unos the unos
   * @param komandaNaziv the komanda naziv
   * @param status the status
   * @param trajanjeMs the trajanje ms
   * @param poruka the poruka
   */
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

  /**
   * Sve.
   *
   * @return the list
   */
  public synchronized List<AuditStavka> sve() {
    return Collections.unmodifiableList(new ArrayList<>(stavke));
  }

  /**
   * Zadnjih.
   *
   * @param n the n
   * @return the list
   */
  public synchronized List<AuditStavka> zadnjih(int n) {
    if (n <= 0) return List.of();
    if (n >= stavke.size()) return sve();
    return Collections.unmodifiableList(new ArrayList<>(stavke.subList(stavke.size() - n, stavke.size())));
  }

  /**
   * Obrisi sve.
   */
  public synchronized void obrisiSve() {
    stavke.clear();
  }

  /**
   * Broj.
   *
   * @return the int
   */
  public synchronized int broj() {
    return stavke.size();
  }
}
