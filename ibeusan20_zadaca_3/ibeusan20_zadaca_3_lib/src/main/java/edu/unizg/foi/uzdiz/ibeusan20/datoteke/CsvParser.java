package edu.unizg.foi.uzdiz.ibeusan20.datoteke;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Jednostavan CSV parser s podrškom za:
 * <ul>
 * <li>UTF-8 kodiranje</li>
 * <li>zareze unutar navodnika</li>
 * <li>višeredne zapise</li>
 * <li>preskakanje praznih i komentiranih redaka (#)</li>
 * </ul>
 * <p>
 * Koristi se prilikom čitanja aranžmana i rezervacija.
 * </p>
 */
public final class CsvParser {

  /**
   * Otvara datoteku u UTF-8 i vraća reader.
   *
   * @param putanja putanja do CSV datoteke
   * @return {@link BufferedReader} za čitanje sadržaja
   * @throws IOException ako datoteka nije dostupna
   */
  private CsvParser() {}

  /**
   * Otvara datoteku u UTF-8 i vraća BufferedReader.
   *
   * @param putanja the putanja
   * @return the buffered reader
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static BufferedReader otvoriUtf8(String putanja) throws IOException {
    return new BufferedReader(
        new InputStreamReader(new FileInputStream(putanja), StandardCharsets.UTF_8));
  }

  /**
   * Čita jedan CSV zapis. Ako je broj navodnika neparan, čita dodatne redke dok se navodnici ne
   * zatvore.
   *
   * @param prviRedak prvi redak zapisa
   * @param br aktivni {@link BufferedReader}
   * @return lista pročitanih polja
   * @throws IOException ako dođe do pogreške pri čitanju
   */
  public static List<String> procitajZapis(String prviRedak, BufferedReader br) throws IOException {
    if (prviRedak == null) {
      return null;
    }

    StringBuilder sb = new StringBuilder(prviRedak);
    int brojNavodnika = brojNavodnika(sb);

    while (brojNavodnika % 2 != 0) {
      String nastavak = br.readLine();
      if (nastavak == null) {
        break;
      }
      sb.append("\n").append(nastavak);
      brojNavodnika = brojNavodnika(sb);
    }

    return razdvojiJedanZapis(sb.toString());
  }

  /**
   * Razdvaja jedan CSV zapis na polja("").
   *
   * @param zapis the zapis
   * @return the list
   */
  public static List<String> razdvojiJedanZapis(String zapis) {
    List<String> polja = new ArrayList<>();
    StringBuilder polje = new StringBuilder();
    boolean uNavodnicima = false;

    for (int i = 0; i < zapis.length(); i++) {
      char c = zapis.charAt(i);

      if (c == '"') {
        if (uNavodnicima && i + 1 < zapis.length() && zapis.charAt(i + 1) == '"') {
          polje.append('"');
          i++;
        } else {
          uNavodnicima = !uNavodnicima;
        }
      } else if (c == ',' && !uNavodnicima) {
        polja.add(ocisti(polje.toString()));
        polje.setLength(0);
      } else {
        polje.append(c);
      }
    }
    // zadnje polje
    polja.add(ocisti(polje.toString()));

    return polja;
  }

  /**
   * Brojač navodnika.
   *
   * @param tekst za analizu
   * @return int broja navodnika
   */
  private static int brojNavodnika(CharSequence tekst) {
    int br = 0;
    for (int i = 0; i < tekst.length(); i++) {
      if (tekst.charAt(i) == '"') {
        br++;
      }
    }
    return br;
  }

  /**
   * skida vanjske razmake i eventualne vanjske navodnike.
   *
   * @param polje the polje
   * @return the string
   */
  private static String ocisti(String polje) {
    if (polje == null) {
      return "";
    }
    String r = polje.trim();
    if (r.startsWith("\"") && r.endsWith("\"") && r.length() >= 2) {
      r = r.substring(1, r.length() - 1);
    }
    return r;
  }
}
