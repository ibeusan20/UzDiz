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

public class DatotekeFacadeImpl implements DatotekeFacade {

  private static DatotekeFacadeImpl instanca;

  private final UcitavacPodataka<AranzmanCsv> citacAranzmana;
  private final UcitavacPodataka<RezervacijaCsv> citacRezervacija;

  private DatotekeFacadeImpl() {
    citacAranzmana = new CitacAranzmana();
    citacRezervacija = new CitacRezervacija();
  }

  public static DatotekeFacadeImpl getInstance() {
    if (instanca == null) {
      instanca = new DatotekeFacadeImpl();
    }
    return instanca;
  }

  @Override
  public List<AranzmanPodaci> ucitajAranzmane(String putanja) {
    return new ArrayList<>(citacAranzmana.ucitaj(putanja));
  }


  @Override
  public List<RezervacijaPodaci> ucitajRezervacije(String putanja) {
    return new ArrayList<>(citacRezervacija.ucitaj(putanja));
  }
}

