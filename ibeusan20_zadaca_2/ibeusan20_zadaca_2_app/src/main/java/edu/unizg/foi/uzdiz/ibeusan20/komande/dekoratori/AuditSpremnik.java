package edu.unizg.foi.uzdiz.ibeusan20.komande.dekoratori;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List; 
import java.util.Map;

public final class AuditSpremnik {
  public static final class Zapis {
    public final LocalDateTime vrijeme;
    public final String unos;
    public final long trajanjeMs;
    public final boolean nastavi;
    public final String greska;

    public Zapis(LocalDateTime vrijeme, String unos, long trajanjeMs, boolean nastavi, String greska) {
      this.vrijeme = vrijeme;
      this.unos = unos;
      this.trajanjeMs = trajanjeMs;
      this.nastavi = nastavi;
      this.greska = greska;
    }
  }

  private static final AuditSpremnik INSTANCA = new AuditSpremnik();
  private static final int MAX_ZAPISA = 1000;

  private final List<Zapis> zapisi = new ArrayList<>();
  private final Map<String, Integer> brojacPoKomandi = new HashMap<>();
  private final Map<String, Long> ukupnoTrajanjePoKomandi = new HashMap<>();

  private AuditSpremnik() {}

  public static AuditSpremnik instanca() {
    return INSTANCA;
  }

  public synchronized void dodaj(String nazivKomande, String unos, long trajanjeMs, boolean nastavi, String greska) {
    if (nazivKomande == null) nazivKomande = "?";
    brojacPoKomandi.merge(nazivKomande, 1, Integer::sum);
    ukupnoTrajanjePoKomandi.merge(nazivKomande, trajanjeMs, Long::sum);

    zapisi.add(new Zapis(LocalDateTime.now(), unos, trajanjeMs, nastavi, greska));
    if (zapisi.size() > MAX_ZAPISA) {
      zapisi.remove(0);
    }
  }

  public synchronized List<Zapis> zadnjih(int n) {
    if (n <= 0) return List.of();
    int from = Math.max(0, zapisi.size() - n);
    return Collections.unmodifiableList(new ArrayList<>(zapisi.subList(from, zapisi.size())));
  }

  public synchronized Map<String, Integer> brojacPoKomandi() {
    return Map.copyOf(brojacPoKomandi);
  }

  public synchronized Map<String, Long> ukupnoTrajanjePoKomandi() {
    return Map.copyOf(ukupnoTrajanjePoKomandi);
  }

  public synchronized int ukupnoZapisa() {
    return zapisi.size();
  }

  public synchronized void reset() {
    zapisi.clear();
    brojacPoKomandi.clear();
    ukupnoTrajanjePoKomandi.clear();
  }
}
