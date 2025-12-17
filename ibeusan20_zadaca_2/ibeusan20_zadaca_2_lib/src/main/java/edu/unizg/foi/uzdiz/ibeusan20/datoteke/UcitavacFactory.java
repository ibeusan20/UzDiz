package edu.unizg.foi.uzdiz.ibeusan20.datoteke;

import edu.unizg.foi.uzdiz.ibeusan20.datoteke.model.AranzmanCsv;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.model.RezervacijaCsv;

public class UcitavacFactory {

  public static UcitavacPodataka<AranzmanCsv> createAranzmanReader() {
    return new CitacAranzmana();
  }

  public static UcitavacPodataka<RezervacijaCsv> createRezervacijaReader() {
    return new CitacRezervacija();
  }
}
