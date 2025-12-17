package edu.unizg.foi.uzdiz.ibeusan20.datoteke;

import edu.unizg.foi.uzdiz.ibeusan20.datoteke.model.AranzmanCsv;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.model.RezervacijaCsv;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating Ucitavac objects.
 */
public class UcitavacFactory {

    /**
     * Creates a new Ucitavac object.
     *
     * @return the ucitavac podataka< aranzman csv>
     */
    public static UcitavacPodataka<AranzmanCsv> createAranzmanReader() {
        return new CitacAranzmana();
    }

    /**
     * Creates a new Ucitavac object.
     *
     * @return the ucitavac podataka< rezervacija csv>
     */
    public static UcitavacPodataka<RezervacijaCsv> createRezervacijaReader() {
        return new CitacRezervacija();
    }
}
