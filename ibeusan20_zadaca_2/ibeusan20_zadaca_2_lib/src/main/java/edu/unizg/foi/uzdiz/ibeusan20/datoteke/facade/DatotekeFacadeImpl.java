package edu.unizg.foi.uzdiz.ibeusan20.datoteke.facade;

import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.CitacAranzmana;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.CitacRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.UcitavacPodataka;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.AranzmanPodaci;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.RezervacijaPodaci;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.model.AranzmanCsv;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.model.RezervacijaCsv;

// TODO: Auto-generated Javadoc
/**
 * The Class DatotekeFacadeImpl.
 */
public class DatotekeFacadeImpl implements DatotekeFacade {

  /** The instanca. */
  private static DatotekeFacadeImpl instanca;

  /** The citac aranzmana. */
  private final UcitavacPodataka<AranzmanCsv> citacAranzmana;
  
  /** The citac rezervacija. */
  private final UcitavacPodataka<RezervacijaCsv> citacRezervacija;

  /**
   * Instantiates a new datoteke facade impl.
   */
  private DatotekeFacadeImpl() {
    citacAranzmana = new CitacAranzmana();
    citacRezervacija = new CitacRezervacija();
  }

  /**
   * Gets the single instance of DatotekeFacadeImpl.
   *
   * @return single instance of DatotekeFacadeImpl
   */
  public static DatotekeFacadeImpl getInstance() {
    if (instanca == null) {
      instanca = new DatotekeFacadeImpl();
    }
    return instanca;
  }

  /**
   * Ucitaj aranzmane.
   *
   * @param putanja the putanja
   * @return the list
   */
  @Override
  public List<AranzmanPodaci> ucitajAranzmane(String putanja) {
    return new ArrayList<>(citacAranzmana.ucitaj(putanja));
  }


  /**
   * Ucitaj rezervacije.
   *
   * @param putanja the putanja
   * @return the list
   */
  @Override
  public List<RezervacijaPodaci> ucitajRezervacije(String putanja) {
    return new ArrayList<>(citacRezervacija.ucitaj(putanja));
  }
}

