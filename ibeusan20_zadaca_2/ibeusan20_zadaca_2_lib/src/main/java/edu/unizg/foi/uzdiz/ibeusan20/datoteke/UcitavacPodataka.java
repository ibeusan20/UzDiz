package edu.unizg.foi.uzdiz.ibeusan20.datoteke;

import java.util.List;

/**
 * Generičko sučelje za učitavanje podataka iz izvora.
 *
 * @param <T> tip podataka koji se učitava
 */
public interface UcitavacPodataka<T> {
  
  /**
   * Učitava i vraća listu objekata tipa {@code T}.
   *
   * @param nazivDatoteke putanja do datoteke izvora
   * @return lista učitanih objekata
   */
  List<T> ucitaj(String nazivDatoteke);
}
