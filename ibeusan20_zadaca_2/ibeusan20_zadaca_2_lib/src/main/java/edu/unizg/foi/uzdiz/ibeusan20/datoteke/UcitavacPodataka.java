package edu.unizg.foi.uzdiz.ibeusan20.datoteke;

import java.util.List;

/**
 * Generičko sučelje za učitavanje podataka iz izvora (npr. CSV) u listu DTO objekata.
 * <p>
 * Ovdje se koristi Java Generics kako bi isti ugovor (interface) vrijedio za različite tipove
 * zapisa (npr. AranzmanCsv, RezervacijaCsv) uz provjeru tipova u vrijeme prevođenja.
 * </p>
 *
 * @param <T> tip zapisa koji se učitava
 *
 * @see <a https://docs.oracle.com/javase/tutorial/java/generics/why.html</a>
 * @see <a https://stackoverflow.com/questions/39386586/c-sharp-generic-interface-and-factory-pattern</a>
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
