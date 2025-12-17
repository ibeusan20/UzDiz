package edu.unizg.foi.uzdiz.ibeusan20.datoteke.facade;

import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.AranzmanPodaci;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.RezervacijaPodaci;

public interface DatotekeFacade {

    List<AranzmanPodaci> ucitajAranzmane(String putanja);

    List<RezervacijaPodaci> ucitajRezervacije(String putanja);
}
