package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.ws.WebSocketPartneri;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Klasa GlobalniPodaci. Služi za spremanje globalnih podataka i pristup istima.
 */
@ApplicationScoped
public class GlobalniPodaci {

  /** Broj obracuna. */
  private int brojObracuna = 0;
  
  /**  broj otvorenih narudzbi. */
  private final Map<Integer, Integer> brojOtvorenihNarudzbi = new ConcurrentHashMap<>();
  
  /** broj racuna. */
  private final Map<Integer, Integer> brojRacuna = new ConcurrentHashMap<>();
  
  /**  interna poruka. */
  private String internaPoruka = "";

  /**
   * Dohvaća broj obracuna.
   *
   * @return the broj obracuna
   */
  public int getBrojObracuna() {
    return brojObracuna;
  }

  /**
   * Postavlja broj obracuna.
   *
   * @param brojObracuna the new broj obracuna
   */
  public void setBrojObracuna(int brojObracuna) {
    this.brojObracuna = brojObracuna;
  }

  /**
   * Dohvaća broj otvorenih narudzbi.
   *
   * @return the broj otvorenih narudzbi
   */
  public Map<Integer, Integer> getBrojOtvorenihNarudzbi() {
    return brojOtvorenihNarudzbi;
  }

  /**
   * Dohvaća broj racuna.
   *
   * @return the broj racuna
   */
  public Map<Integer, Integer> getBrojRacuna() {
    return brojRacuna;
  }

  /**
   * Dohvaća internu poruku.
   *
   * @return the interna poruka
   */
  public String getInternaPoruka() {
    return internaPoruka;
  }

  /**
   * Postavlja internu poruku.
   *
   * @param internaPoruka the new interna poruka
   */
  public void setInternaPoruka(String internaPoruka) {
    this.internaPoruka = internaPoruka;
  }

  /**
   * Povećava broj obracuna.
   */
  public synchronized void povecajBrojObracuna() {
    brojObracuna++;
  }

  /**
   * Povećava broj otvorenih narudzbi.
   *
   * @param partnerId the partner id
   */
  public void povecajOtvoreneNarudzbe(int partnerId) {
    brojOtvorenihNarudzbi.merge(partnerId, 1, Integer::sum);
    WebSocketPartneri.posaljiPoruku(partnerId, this);
  }

  /**
   * Smanjuje broj otvorenih narudzbi.
   *
   * @param partnerId the partner id
   */
  public void smanjiOtvoreneNarudzbe(int partnerId) {
    brojOtvorenihNarudzbi.computeIfPresent(partnerId, (k, v) -> v > 0 ? v - 1 : 0);
    WebSocketPartneri.posaljiPoruku(partnerId, this);
  }

  /**
   * Povećava broj racuna.
   *
   * @param partnerId the partner id
   */
  public void povecajBrojRacuna(int partnerId) {
    brojRacuna.merge(partnerId, 1, Integer::sum);
    WebSocketPartneri.posaljiPoruku(partnerId, this);
  }


  /**
   * Daje broj otvorenih narudzbi.
   *
   * @param partnerId the partner id
   * @return the int
   */
  public int dajBrojOtvorenihNarudzbi(int partnerId) {
    return brojOtvorenihNarudzbi.getOrDefault(partnerId, 0);
  }

  /**
   * Daje broj racuna.
   *
   * @param partnerId the partner id
   * @return the int
   */
  public int dajBrojRacuna(int partnerId) {
    return brojRacuna.getOrDefault(partnerId, 0);
  }
  
  /** pauza partnera. */
  private final Map<Integer, Boolean> pauzaPartnera = new ConcurrentHashMap<>();

  /**
   * Provjera je li server u pauzi.
   *
   * @param partnerId the partner id
   * @return true, if successful
   */
  public boolean jeUPauzi(int partnerId) {
    return pauzaPartnera.getOrDefault(partnerId, false);
  }

  /**
   * Postavljanje pauze.
   *
   * @param partnerId the partner id
   * @param pauza the pauza
   */
  public void postaviPauzu(int partnerId, boolean pauza) {
    pauzaPartnera.put(partnerId, pauza);
    WebSocketPartneri.posaljiPoruku(partnerId, this);
  }

}
