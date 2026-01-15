package edu.unizg.foi.uzdiz.ibeusan20.datoteke;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import edu.unizg.foi.uzdiz.ibeusan20.datoteke.model.RezervacijaCsv;

public class CitacRezervacija implements UcitavacPodataka<RezervacijaCsv> {

  @Override
  public List<RezervacijaCsv> ucitaj(String nazivDatoteke) {
    List<RezervacijaCsv> rezultat = new ArrayList<>();
    int redniBroj = 0;
    int redniBrojGreske = 0;

    try (BufferedReader br = CsvParser.otvoriUtf8(nazivDatoteke)) {
      String prviRedak;
      boolean prvi = true;

      while ((prviRedak = br.readLine()) != null) {
        redniBroj++;

        // preskoči header
        if (prvi) {
          prvi = false;
          continue;
        }

        // preskoči prazno i komentare
        if (prviRedak.isBlank() || prviRedak.trim().startsWith("#")) {
          continue;
        }

        // pročitaj cijeli CSV zapis (može biti višeredni) i zadrži ga za ispis greške
        String zapisTekst = procitajZapisTekst(prviRedak, br);

        try {
          List<String> stupci = CsvParser.razdvojiJedanZapis(zapisTekst);

          RezervacijaCsv r = new RezervacijaCsv();
          r.ime = uzmi(stupci, 0);
          r.prezime = uzmi(stupci, 1);
          r.oznakaAranzmana = uzmi(stupci, 2);

          String datumTekst = uzmi(stupci, 3);

          // validacije
          if (r.ime == null || r.ime.isBlank()) {
            throw new IllegalArgumentException("Ime nije definirano.");
          }
          if (r.prezime == null || r.prezime.isBlank()) {
            throw new IllegalArgumentException("Prezime nije definirano.");
          }
          if (r.oznakaAranzmana == null || r.oznakaAranzmana.isBlank()) {
            throw new IllegalArgumentException("Oznaka aranžmana nije definirana.");
          }
          if (datumTekst == null || datumTekst.isBlank()) {
            throw new IllegalArgumentException("Datum i vrijeme rezervacije nije definirano.");
          }

          LocalDateTime dt = PomocnikDatum.procitajDatumIVrijeme(datumTekst);
          if (dt == null) {
            throw new IllegalArgumentException("Neispravan datum/vrijeme rezervacije.");
          }
          r.datumVrijeme = dt;

          rezultat.add(r);

        } catch (Exception e) {
          redniBrojGreske++;
          System.err.println("[" + redniBrojGreske + ". greška (rezervacije)] u " + redniBroj
              + ". retku rezervacije: " + e.getMessage());
          System.err.println("Sadržaj retka s greškom: " + zapisTekst.trim());
        }
      }

    } catch (IOException e) {
      System.err.println("Greška pri čitanju datoteke rezervacija: " + e.getMessage());
    }

    return rezultat;
  }

  private String uzmi(List<String> polja, int i) {
    if (polja == null || i >= polja.size())
      return "";
    return polja.get(i);
  }

  /**
   * Pročita cijeli CSV zapis i vraća TEKST zapisa da ga možemo ispisati kod greške.
   */
  private String procitajZapisTekst(String prviRedak, BufferedReader br) throws IOException {
    StringBuilder sb = new StringBuilder(prviRedak);
    int brojNavodnika = brojNavodnika(sb);

    while (brojNavodnika % 2 != 0) {
      String nastavak = br.readLine();
      if (nastavak == null)
        break;
      sb.append("\n").append(nastavak);
      brojNavodnika = brojNavodnika(sb);
    }
    return sb.toString();
  }

  private int brojNavodnika(CharSequence tekst) {
    int br = 0;
    for (int i = 0; i < tekst.length(); i++) {
      if (tekst.charAt(i) == '"')
        br++;
    }
    return br;
  }
}
