package edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import edu.unizg.foi.nwtis.ibeusan20.vjezba_08_dz_3.ws.WebSocketPartneri;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GlobalniPodaci {

  private int brojObracuna = 0;
  private final Map<Integer, Integer> brojOtvorenihNarudzbi = new ConcurrentHashMap<>();
  private final Map<Integer, Integer> brojRacuna = new ConcurrentHashMap<>();
  private String internaPoruka = "";

  public int getBrojObracuna() {
    return brojObracuna;
  }

  public void setBrojObracuna(int brojObracuna) {
    this.brojObracuna = brojObracuna;
  }

  public Map<Integer, Integer> getBrojOtvorenihNarudzbi() {
    return brojOtvorenihNarudzbi;
  }

  public Map<Integer, Integer> getBrojRacuna() {
    return brojRacuna;
  }

  public String getInternaPoruka() {
    return internaPoruka;
  }

  public void setInternaPoruka(String internaPoruka) {
    this.internaPoruka = internaPoruka;
  }

  public synchronized void povecajBrojObracuna() {
    brojObracuna++;
  }

  public void povecajOtvoreneNarudzbe(int partnerId) {
    brojOtvorenihNarudzbi.merge(partnerId, 1, Integer::sum);
    WebSocketPartneri.posaljiPoruku(partnerId, this);
  }

  public void smanjiOtvoreneNarudzbe(int partnerId) {
    brojOtvorenihNarudzbi.computeIfPresent(partnerId, (k, v) -> v > 0 ? v - 1 : 0);
    WebSocketPartneri.posaljiPoruku(partnerId, this);
  }

  public void povecajBrojRacuna(int partnerId) {
    brojRacuna.merge(partnerId, 1, Integer::sum);
    WebSocketPartneri.posaljiPoruku(partnerId, this);
  }


  public int dajBrojOtvorenihNarudzbi(int partnerId) {
    return brojOtvorenihNarudzbi.getOrDefault(partnerId, 0);
  }

  public int dajBrojRacuna(int partnerId) {
    return brojRacuna.getOrDefault(partnerId, 0);
  }
  
  private final Map<Integer, Boolean> pauzaPartnera = new ConcurrentHashMap<>();

  public boolean jeUPauzi(int partnerId) {
    return pauzaPartnera.getOrDefault(partnerId, false);
  }

  public void postaviPauzu(int partnerId, boolean pauza) {
    pauzaPartnera.put(partnerId, pauza);
    WebSocketPartneri.posaljiPoruku(partnerId, this);
  }

}
