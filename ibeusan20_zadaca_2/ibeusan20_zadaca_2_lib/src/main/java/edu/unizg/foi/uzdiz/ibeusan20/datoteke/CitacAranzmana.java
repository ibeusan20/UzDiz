package edu.unizg.foi.uzdiz.ibeusan20.datoteke;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.unizg.foi.uzdiz.ibeusan20.datoteke.model.AranzmanCsv;

public class CitacAranzmana implements UcitavacPodataka<AranzmanCsv> {

  @Override
  public List<AranzmanCsv> ucitaj(String nazivDatoteke) {
    List<AranzmanCsv> rezultat = new ArrayList<>();
    int redniBroj = 0;
    int redniBrojGreske = 0;

    try (BufferedReader br = CsvParser.otvoriUtf8(nazivDatoteke)) {
      String prviRedak;
      boolean prvi = true;

      while ((prviRedak = br.readLine()) != null) {
        redniBroj++;

        if (prvi) { // preskoči header
          prvi = false;
          continue;
        }
        if (prviRedak.isBlank() || prviRedak.trim().startsWith("#")) {
          continue;
        }

        // pročitaj cijeli zapis (podržava višeredne zapise) i zadrži original teksta za ispis
        String zapisTekst = procitajZapisTekst(prviRedak, br);

        try {
          List<String> stupci = CsvParser.razdvojiJedanZapis(zapisTekst);

          AranzmanCsv a = new AranzmanCsv();

          a.oznaka = uzmi(stupci, 0);
          a.naziv = uzmi(stupci, 1);
          a.program = uzmi(stupci, 2);

          // VALIDACIJE (po uzoru na poruke koje želiš)
          if (a.oznaka == null || a.oznaka.isBlank()) {
            throw new IllegalArgumentException("Oznaka aranžmana nije definirana.");
          }

          a.pocetniDatum = PomocnikDatum.procitajDatum(uzmi(stupci, 3));
          a.zavrsniDatum = PomocnikDatum.procitajDatum(uzmi(stupci, 4));

          if (a.pocetniDatum == null) {
            throw new IllegalArgumentException("Početni datum nije definiran.");
          }
          if (a.zavrsniDatum == null) {
            throw new IllegalArgumentException("Završni datum nije definiran.");
          }
          if (a.zavrsniDatum.isBefore(a.pocetniDatum)) {
            throw new IllegalArgumentException("Završni datum ne može biti prije početnog datuma.");
          }

          a.vrijemeKretanja = PomocnikDatum.procitajVrijeme(uzmi(stupci, 5));
          a.vrijemePovratka = PomocnikDatum.procitajVrijeme(uzmi(stupci, 6));

          // Cijena, min, max – obično obavezni (ako želiš labavije, promijeni na opcionalno)
          a.cijena = procitajFloatObavezno(uzmi(stupci, 7), "Cijena nije definirana.");
          a.minPutnika = procitajIntObavezno(uzmi(stupci, 8), "Minimalni broj putnika nije definiran.");
          a.maxPutnika = procitajIntObavezno(uzmi(stupci, 9), "Maksimalni broj putnika nije definiran.");

          if (a.minPutnika < 0 || a.maxPutnika < 0) {
            throw new IllegalArgumentException("Min/Max putnika ne može biti negativan.");
          }
          if (a.maxPutnika < a.minPutnika) {
            throw new IllegalArgumentException("Max putnika ne može biti manji od Min putnika.");
          }

          // ostalo može biti 0 ako prazno (ili napravi strože ako treba)
          a.brojNocenja = procitajIntOpcionalno(uzmi(stupci, 10));
          a.doplataJednokrevetna = procitajFloatOpcionalno(uzmi(stupci, 11));

          String prijevoz = uzmi(stupci, 12);
          if (prijevoz != null && !prijevoz.isBlank()) {
            a.prijevoz = Arrays.asList(prijevoz.split(";"));
          }

          a.brojDorucaka = procitajIntOpcionalno(uzmi(stupci, 13));
          a.brojRuckova = procitajIntOpcionalno(uzmi(stupci, 14));
          a.brojVecera = procitajIntOpcionalno(uzmi(stupci, 15));

          rezultat.add(a);

        } catch (Exception e) {
          redniBrojGreske++;
          System.err.println("[" + redniBrojGreske + ". greška (aranžmani)] u " + redniBroj
              + ". retku aranžmana: " + e.getMessage());
          System.err.println("Sadržaj retka s greškom: " + zapisTekst.trim());
        }
      }

    } catch (IOException e) {
      System.err.println("Greška pri čitanju datoteke aranžmana: " + e.getMessage());
    }

    return rezultat;
  }

  // ---- helpers ----

  private String uzmi(List<String> polja, int i) {
    if (polja == null || i >= polja.size()) return "";
    return polja.get(i);
  }

  private float procitajFloatObavezno(String tekst, String porukaAkoNema) {
    if (tekst == null || tekst.isBlank()) throw new IllegalArgumentException(porukaAkoNema);
    String t = tekst.trim().replace(",", ".");
    try { return Float.parseFloat(t); }
    catch (Exception e) { throw new IllegalArgumentException("Neispravan broj: " + tekst); }
  }

  private int procitajIntObavezno(String tekst, String porukaAkoNema) {
    if (tekst == null || tekst.isBlank()) throw new IllegalArgumentException(porukaAkoNema);
    try { return Integer.parseInt(tekst.trim()); }
    catch (Exception e) { throw new IllegalArgumentException("Neispravan cijeli broj: " + tekst); }
  }

  private float procitajFloatOpcionalno(String tekst) {
    if (tekst == null || tekst.isBlank()) return 0f;
    String t = tekst.trim().replace(",", ".");
    try { return Float.parseFloat(t); }
    catch (Exception e) { return 0f; }
  }

  private int procitajIntOpcionalno(String tekst) {
    if (tekst == null || tekst.isBlank()) return 0;
    try { return Integer.parseInt(tekst.trim()); }
    catch (Exception e) { return 0; }
  }

  private String procitajZapisTekst(String prviRedak, BufferedReader br) throws IOException {
    StringBuilder sb = new StringBuilder(prviRedak);
    int brojNavodnika = brojNavodnika(sb);

    while (brojNavodnika % 2 != 0) {
      String nastavak = br.readLine();
      if (nastavak == null) break;
      sb.append("\n").append(nastavak);
      brojNavodnika = brojNavodnika(sb);
    }
    return sb.toString();
  }

  private int brojNavodnika(CharSequence tekst) {
    int br = 0;
    for (int i = 0; i < tekst.length(); i++) {
      if (tekst.charAt(i) == '"') br++;
    }
    return br;
  }
}
