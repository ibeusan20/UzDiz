package edu.unizg.foi.uzdiz.ibeusan20.datoteke.facade;

import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.AranzmanPodaci;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.api.RezervacijaPodaci;

// TODO: Auto-generated Javadoc
/**
 * The Interface DatotekeFacade.
 */
public interface DatotekeFacade {

    /**
     * Ucitaj aranzmane.
     *
     * @param putanja the putanja
     * @return the list
     */
    List<AranzmanPodaci> ucitajAranzmane(String putanja);

    /**
     * Ucitaj rezervacije.
     *
     * @param putanja the putanja
     * @return the list
     */
    List<RezervacijaPodaci> ucitajRezervacije(String putanja);
}
