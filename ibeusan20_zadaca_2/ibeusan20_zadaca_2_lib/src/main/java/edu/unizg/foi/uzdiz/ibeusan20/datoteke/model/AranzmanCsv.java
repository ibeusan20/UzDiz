package edu.unizg.foi.uzdiz.ibeusan20.datoteke.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.AranzmanPodaci;

public class AranzmanCsv implements AranzmanPodaci {
  public String oznaka;
  public String naziv;
  public String program;
  public LocalDate pocetniDatum;
  public LocalDate zavrsniDatum;
  public LocalTime vrijemeKretanja;
  public LocalTime vrijemePovratka;
  public float cijena;
  public int minPutnika;
  public int maxPutnika;
  public int brojNocenja;
  public float doplataJednokrevetna;
  public List<String> prijevoz;
  public int brojDorucaka;
  public int brojRuckova;
  public int brojVecera;

  @Override public String getOznaka() { return oznaka; }
  @Override public String getNaziv() { return naziv; }
  @Override public String getProgram() { return program; }
  @Override public LocalDate getPocetniDatum() { return pocetniDatum; }
  @Override public LocalDate getZavrsniDatum() { return zavrsniDatum; }
  @Override public LocalTime getVrijemeKretanja() { return vrijemeKretanja; }
  @Override public LocalTime getVrijemePovratka() { return vrijemePovratka; }
  @Override public float getCijena() { return cijena; }
  @Override public int getMinPutnika() { return minPutnika; }
  @Override public int getMaxPutnika() { return maxPutnika; }
  @Override public int getBrojNocenja() { return brojNocenja; }
  @Override public float getDoplataJednokrevetna() { return doplataJednokrevetna; }
  @Override public List<String> getPrijevoz() { return prijevoz; }
  @Override public int getBrojDorucaka() { return brojDorucaka; }
  @Override public int getBrojRuckova() { return brojRuckova; }
  @Override public int getBrojVecera() { return brojVecera; }
}
