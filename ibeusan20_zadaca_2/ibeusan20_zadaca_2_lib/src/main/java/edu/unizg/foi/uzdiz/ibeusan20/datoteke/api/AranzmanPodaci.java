package edu.unizg.foi.uzdiz.ibeusan20.datoteke.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AranzmanPodaci {
  String getOznaka();

  String getNaziv();

  String getProgram();

  LocalDate getPocetniDatum();

  LocalDate getZavrsniDatum();

  LocalTime getVrijemeKretanja();

  LocalTime getVrijemePovratka();

  float getCijena();

  int getMinPutnika();

  int getMaxPutnika();

  int getBrojNocenja();

  float getDoplataJednokrevetna();

  List<String> getPrijevoz();

  int getBrojDorucaka();

  int getBrojRuckova();

  int getBrojVecera();
}
