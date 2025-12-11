package edu.unizg.foi.uzdiz.ibeusan20.datoteke.facade;

import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.model.AranzmanCsv;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.model.RezervacijaCsv;

public interface DatotekeFacade {

    List<AranzmanCsv> ucitajAranzmane(String putanja);

    List<RezervacijaCsv> ucitajRezervacije(String putanja);
}
