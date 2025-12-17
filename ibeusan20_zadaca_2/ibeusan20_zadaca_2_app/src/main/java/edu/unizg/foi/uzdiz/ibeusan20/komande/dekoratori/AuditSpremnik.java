package edu.unizg.foi.uzdiz.ibeusan20.komande.dekoratori;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List; 
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class AuditSpremnik.
 */
public final class AuditSpremnik {
  
  /**
   * The Class Zapis.
   */
  public static final class Zapis {
    
    /** The vrijeme. */
    public final LocalDateTime vrijeme;
    
    /** The unos. */
    public final String unos;
    
    /** The trajanje ms. */
    public final long trajanjeMs;
    
    /** The nastavi. */
    public final boolean nastavi;
    
    /** The greska. */
    public final String greska; // null ako nema

    /**
     * Instantiates a new zapis.
     *
     * @param vrijeme the vrijeme
     * @param unos the unos
     * @param trajanjeMs the trajanje ms
     * @param nastavi the nastavi
     * @param greska the greska
     */
    public Zapis(LocalDateTime vrijeme, String unos, long trajanjeMs, boolean nastavi, String greska) {
      this.vrijeme = vrijeme;
      this.unos = unos;
      this.trajanjeMs = trajanjeMs;
      this.nastavi = nastavi;
      this.greska = greska;
    }
  }

  /** The Constant INSTANCA. */
  private static final AuditSpremnik INSTANCA = new AuditSpremnik();
  
  /** The Constant MAX_ZAPISA. */
  private static final int MAX_ZAPISA = 1000;

  /** The zapisi. */
  private final List<Zapis> zapisi = new ArrayList<>();
  
  /** The brojac po komandi. */
  private final Map<String, Integer> brojacPoKomandi = new HashMap<>();
  
  /** The ukupno trajanje po komandi. */
  private final Map<String, Long> ukupnoTrajanjePoKomandi = new HashMap<>();

  /**
   * Instantiates a new audit spremnik.
   */
  private AuditSpremnik() {}

  /**
   * Instanca.
   *
   * @return the audit spremnik
   */
  public static AuditSpremnik instanca() {
    return INSTANCA;
  }

  /**
   * Dodaj.
   *
   * @param nazivKomande the naziv komande
   * @param unos the unos
   * @param trajanjeMs the trajanje ms
   * @param nastavi the nastavi
   * @param greska the greska
   */
  public synchronized void dodaj(String nazivKomande, String unos, long trajanjeMs, boolean nastavi, String greska) {
    if (nazivKomande == null) nazivKomande = "?";
    brojacPoKomandi.merge(nazivKomande, 1, Integer::sum);
    ukupnoTrajanjePoKomandi.merge(nazivKomande, trajanjeMs, Long::sum);

    zapisi.add(new Zapis(LocalDateTime.now(), unos, trajanjeMs, nastavi, greska));
    if (zapisi.size() > MAX_ZAPISA) {
      zapisi.remove(0);
    }
  }

  /**
   * Zadnjih.
   *
   * @param n the n
   * @return the list
   */
  public synchronized List<Zapis> zadnjih(int n) {
    if (n <= 0) return List.of();
    int from = Math.max(0, zapisi.size() - n);
    return Collections.unmodifiableList(new ArrayList<>(zapisi.subList(from, zapisi.size())));
  }

  /**
   * Brojac po komandi.
   *
   * @return the map
   */
  public synchronized Map<String, Integer> brojacPoKomandi() {
    return Map.copyOf(brojacPoKomandi);
  }

  /**
   * Ukupno trajanje po komandi.
   *
   * @return the map
   */
  public synchronized Map<String, Long> ukupnoTrajanjePoKomandi() {
    return Map.copyOf(ukupnoTrajanjePoKomandi);
  }

  /**
   * Ukupno zapisa.
   *
   * @return the int
   */
  public synchronized int ukupnoZapisa() {
    return zapisi.size();
  }

  /**
   * Reset.
   */
  public synchronized void reset() {
    zapisi.clear();
    brojacPoKomandi.clear();
    ukupnoTrajanjePoKomandi.clear();
  }
}
