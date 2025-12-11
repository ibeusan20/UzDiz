package edu.unizg.foi.uzdiz.ibeusan20.datoteke;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.model.RezervacijaCsv;

public class CitacRezervacija implements UcitavacPodataka<RezervacijaCsv> {

    @Override
    public List<RezervacijaCsv> ucitaj(String nazivDatoteke) {
        List<RezervacijaCsv> rezultat = new ArrayList<>();
        int redniBroj = 0;

        try (BufferedReader br = CsvParser.otvoriUtf8(nazivDatoteke)) {
            String redak;
            boolean prvi = true;

            while ((redak = br.readLine()) != null) {
                redniBroj++;

                if (prvi) {
                    prvi = false;
                    continue;
                }
                if (redak.isBlank() || redak.startsWith("#"))
                    continue;

                try {
                    List<String> stupci = CsvParser.procitajZapis(redak, br);
                    RezervacijaCsv r = new RezervacijaCsv();

                    r.ime = uzmi(stupci, 0);
                    r.prezime = uzmi(stupci, 1);
                    r.oznakaAranzmana = uzmi(stupci, 2);
                    r.datumVrijeme = PomocnikDatum.procitajDatumIVrijeme(uzmi(stupci, 3));

                    rezultat.add(r);

                } catch (Exception e) {
                    System.err.println("Greška u retku " + redniBroj + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Greška pri čitanju datoteke rezervacija: " + e.getMessage());
        }

        return rezultat;
    }

    private String uzmi(List<String> polja, int i) {
        if (i >= polja.size())
            return "";
        return polja.get(i);
    }
}
