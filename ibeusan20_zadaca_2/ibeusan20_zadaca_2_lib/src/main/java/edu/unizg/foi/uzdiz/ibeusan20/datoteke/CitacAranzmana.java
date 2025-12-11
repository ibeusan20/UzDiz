package edu.unizg.foi.uzdiz.ibeusan20.datoteke;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.unizg.foi.uzdiz.ibeusan20.datoteke.model.AranzmanCsv;

public class CitacAranzmana implements UcitavacPodataka<AranzmanCsv> {

    @Override
    public List<AranzmanCsv> ucitaj(String nazivDatoteke) {
        List<AranzmanCsv> rezultat = new ArrayList<>();
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
                    AranzmanCsv a = new AranzmanCsv();

                    a.oznaka = uzmi(stupci, 0);
                    a.naziv = uzmi(stupci, 1);
                    a.program = uzmi(stupci, 2);
                    a.pocetniDatum = PomocnikDatum.procitajDatum(uzmi(stupci, 3));
                    a.zavrsniDatum = PomocnikDatum.procitajDatum(uzmi(stupci, 4));
                    a.vrijemeKretanja = PomocnikDatum.procitajVrijeme(uzmi(stupci, 5));
                    a.vrijemePovratka = PomocnikDatum.procitajVrijeme(uzmi(stupci, 6));
                    a.cijena = procitajFloat(uzmi(stupci, 7));
                    a.minPutnika = procitajInt(uzmi(stupci, 8));
                    a.maxPutnika = procitajInt(uzmi(stupci, 9));
                    a.brojNocenja = procitajInt(uzmi(stupci, 10));
                    a.doplataJednokrevetna = procitajFloat(uzmi(stupci, 11));

                    String prijevoz = uzmi(stupci, 12);
                    if (prijevoz != null && !prijevoz.isBlank())
                        a.prijevoz = Arrays.asList(prijevoz.split(";"));

                    a.brojDorucaka = procitajInt(uzmi(stupci, 13));
                    a.brojRuckova = procitajInt(uzmi(stupci, 14));
                    a.brojVecera = procitajInt(uzmi(stupci, 15));

                    rezultat.add(a);

                } catch (Exception e) {
                    System.err.println("Greška u retku " + redniBroj + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Greška pri čitanju datoteke: " + e.getMessage());
        }

        return rezultat;
    }

    private String uzmi(List<String> polja, int i) {
        if (i >= polja.size())
            return "";
        return polja.get(i);
    }

    private float procitajFloat(String tekst) {
        try {
            return Float.parseFloat(tekst.replace(",", "."));
        } catch (Exception e) {
            return 0;
        }
    }

    private int procitajInt(String tekst) {
        try {
            return Integer.parseInt(tekst.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
