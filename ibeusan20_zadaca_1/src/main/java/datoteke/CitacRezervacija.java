package datoteke;

import model.Rezervacija;
import model.PomocnikDatum;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Čita i parsira podatke o rezervacijama iz CSV datoteke.
 * <p>
 * Implementira {@link UcitavacPodataka} za tip {@link Rezervacija}. Svaka greška u retku ispisuje
 * se na stderr, ali ne prekida čitanje.
 * </p>
 */
public class CitacRezervacija implements UcitavacPodataka<Rezervacija> {

  /**
   * Učitava sve rezervacije iz datoteke.
   *
   * @param nazivDatoteke putanja do datoteke rezervacija
   * @return lista učitanih rezervacija
   */
  @Override
  public List<Rezervacija> ucitaj(String nazivDatoteke) {
    List<Rezervacija> rezultat = new ArrayList<>();
    int redniBroj = 0;
    int redniBrojGreske = 0;

    try (BufferedReader br = CsvParser.otvoriUtf8(nazivDatoteke)) {
      String redak;
      boolean prvi = true;

      while ((redak = br.readLine()) != null) {
        redniBroj++;

        if (prvi) {
          prvi = false;
          continue;
        }

        if (redak.isBlank() || redak.trim().startsWith("#")) {
          continue;
        }

        try {
          List<String> stupci = CsvParser.procitajZapis(redak, br);
          String ime = uzmi(stupci, 0);
          String prezime = uzmi(stupci, 1);
          String oznaka = uzmi(stupci, 2);
          LocalDateTime datumVrijeme = PomocnikDatum.procitajDatumIVrijeme(uzmi(stupci, 3));

          Rezervacija r = new Rezervacija(ime, prezime, oznaka, datumVrijeme);
          rezultat.add(r);

        } catch (Exception e) {
          redniBrojGreske++;
          System.err.println(redniBrojGreske + ". greška u " + redniBroj + ". retku rezervacije: "
              + e.getMessage());
          System.err.println("Sadržaj retka s greškom: " + redak.trim());
        }
      }

    } catch (IOException e) {
      System.err.println("Greška pri čitanju rezervacija: " + e.getMessage());
    }

    return rezultat;
  }

  /**
   * Uzmi.
   *
   * @param polja the polja
   * @param i the i
   * @return the string
   */
  private String uzmi(List<String> polja, int i) {
    if (i >= polja.size()) {
      return "";
    }
    return polja.get(i);
  }
}
