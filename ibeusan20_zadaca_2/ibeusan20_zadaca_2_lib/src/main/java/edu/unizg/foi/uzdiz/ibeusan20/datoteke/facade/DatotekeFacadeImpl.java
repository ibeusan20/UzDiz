package edu.unizg.foi.uzdiz.ibeusan20.datoteke.facade;

import java.util.List;

import edu.unizg.foi.uzdiz.ibeusan20.datoteke.CitacAranzmana;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.CitacRezervacija;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.UcitavacPodataka;
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
    public List<AranzmanCsv> ucitajAranzmane(String putanja) {
        return citacAranzmana.ucitaj(putanja);
    }

    @Override
    public List<RezervacijaCsv> ucitajRezervacije(String putanja) {
        return citacRezervacija.ucitaj(putanja);
    }
}

