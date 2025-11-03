package datoteke;

import model.Aranzman;
import model.AranzmanBuilder;
import model.PomocnikDatum;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CitacAranzmana implements UcitavacPodataka<Aranzman> {

  @Override
  public List<Aranzman> ucitaj(String nazivDatoteke) {
    List<Aranzman> rezultat = new ArrayList<>();
    int redniBroj = 0;

    try (BufferedReader br = CsvParser.otvoriUtf8(nazivDatoteke)) {
      String redak;
      boolean prvi = true;

      while ((redak = br.readLine()) != null) {
        redniBroj++;

        // preskoči zaglavlje
        if (prvi) {
          prvi = false;
          continue;
        }

        // preskoči prazne i komentare
        if (redak.isBlank() || redak.trim().startsWith("#")) {
          continue;
        }

        try {
          List<String> stupci = CsvParser.procitajZapis(redak, br);

          AranzmanBuilder b = new AranzmanBuilder();
          b.postaviOznaku(uzmi(stupci, 0));
          b.postaviNaziv(uzmi(stupci, 1));
          b.postaviProgram(uzmi(stupci, 2));
          b.postaviPocetniDatum(PomocnikDatum.procitajDatum(uzmi(stupci, 3)));
          b.postaviZavrsniDatum(PomocnikDatum.procitajDatum(uzmi(stupci, 4)));
          b.postaviVrijemeKretanja(PomocnikDatum.procitajVrijeme(uzmi(stupci, 5)));
          b.postaviVrijemePovratka(PomocnikDatum.procitajVrijeme(uzmi(stupci, 6)));
          b.postaviCijenu(procitajFloat(uzmi(stupci, 7)));
          b.postaviMinPutnika(procitajInt(uzmi(stupci, 8)));
          b.postaviMaxPutnika(procitajInt(uzmi(stupci, 9)));
          b.postaviBrojNocenja(procitajInt(uzmi(stupci, 10)));
          b.postaviDoplatuJednokrevetna(procitajFloat(uzmi(stupci, 11)));
          b.postaviPrijevoz(uzmi(stupci, 12));
          b.postaviBrojDorucaka(procitajInt(uzmi(stupci, 13)));
          b.postaviBrojRuckova(procitajInt(uzmi(stupci, 14)));
          b.postaviBrojVecera(procitajInt(uzmi(stupci, 15)));


          Aranzman a = b.izgradi();
          rezultat.add(a);

        } catch (Exception e) {
          System.err.println("Greška u retku " + redniBroj + ": " + e.getMessage());
        }
      }

    } catch (IOException e) {
      System.err.println("Greška pri čitanju datoteke aranžmana: " + e.getMessage());
    }

    return rezultat;
  }

  private String uzmi(List<String> polja, int i) {
    if (i >= polja.size()) {
      return "";
    }
    return polja.get(i);
  }

  private float procitajFloat(String tekst) {
    if (tekst == null || tekst.isBlank()) {
      return 0f;
    }
    try {
      return Float.parseFloat(tekst.replace(",", "."));
    } catch (NumberFormatException e) {
      return 0f;
    }
  }

  private int procitajInt(String tekst) {
    if (tekst == null || tekst.isBlank())
      return 0;
    try {
      return Integer.parseInt(tekst.trim());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  public static LocalTime procitajVrijeme(String tekst) {
    if (tekst == null || tekst.isBlank()) {
      return null;
    }
    try {
      return LocalTime.parse(tekst.trim(), DateTimeFormatter.ofPattern("H:mm"));
    } catch (Exception e) {
      return null;
    }
  }
}
