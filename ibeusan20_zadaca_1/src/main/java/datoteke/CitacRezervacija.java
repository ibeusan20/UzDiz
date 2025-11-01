package datoteke;

import model.Rezervacija;
import model.PomocnikDatum;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CitacRezervacija implements UcitavacPodataka<Rezervacija> {

    @Override
    public List<Rezervacija> ucitaj(String nazivDatoteke) {
        List<Rezervacija> rezultat = new ArrayList<>();
        int redniBroj = 0;

        try (BufferedReader br = CsvParser.otvoriUtf8(nazivDatoteke)) {
            String redak;
            boolean prvi = true;

            while ((redak = br.readLine()) != null) {
                redniBroj++;

                if (prvi) { // preskoči zaglavlje
                    prvi = false;
                    continue;
                }

                if (redak.isBlank() || redak.trim().startsWith("#")) {
                    continue;
                }

                try {
                    List<String> stupci = CsvParser.procitajZapis(redak, br);
                    // pretpostavljeni raspored:
                    // 0: ime
                    // 1: prezime
                    // 2: oznaka aranžmana
                    // 3: datum i vrijeme (dd.MM.yyyy. HH:mm:ss)

                    String ime = uzmi(stupci, 0);
                    String prezime = uzmi(stupci, 1);
                    String oznaka = uzmi(stupci, 2);
                    LocalDateTime datumVrijeme =
                            PomocnikDatum.procitajDatumIVrijeme(uzmi(stupci, 3));

                    Rezervacija r = new Rezervacija(ime, prezime, oznaka, datumVrijeme);
                    rezultat.add(r);

                } catch (Exception e) {
                    System.err.println("Greška u retku rezervacije " + redniBroj + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Greška pri čitanju rezervacija: " + e.getMessage());
        }

        return rezultat;
    }

    private String uzmi(List<String> polja, int i) {
        if (i >= polja.size()) {
            return "";
        }
        return polja.get(i);
    }
}
