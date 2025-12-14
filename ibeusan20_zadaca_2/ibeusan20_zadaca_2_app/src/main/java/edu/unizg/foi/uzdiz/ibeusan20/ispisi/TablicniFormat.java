package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TablicniFormat implements FormatIspisaBridge {

  private boolean ispisujeOtkazane = true;
  private enum Mode { AUTO, INT, VALUTA }
  
//Stupci koji se uvijek ispisuju kao cijeli brojevi
 private final Set<String> intStupci = new HashSet<>();

 // Stupci koji se ispisuju kao valuta (2 decimale + €)
 private final Set<String> valutaStupci = new HashSet<>();

 public TablicniFormat() {
   // Default prema tvom PDF-u / tablicama
   intStupci.add("Min");
   intStupci.add("Max");
   intStupci.add("Broj noćenja");
   intStupci.add("Doručaka");
   intStupci.add("Ručkova");
   intStupci.add("Večera");
   intStupci.add("Ukupno");
   intStupci.add("Aktivne");
   intStupci.add("Čekanje");
   intStupci.add("Odgođene");
   intStupci.add("Otkazane");

   valutaStupci.add("Cijena");
   valutaStupci.add("Ukupan prihod");
   valutaStupci.add("Doplata jednokrevetna");
 }

  /**
   * Ako je false, redovi koji su OTKAZANI se preskaču.
   * IRTA to koristi kad filter NE uključuje O.
   */
  public void setIspisujeOtkazane(boolean vrijednost) {
    this.ispisujeOtkazane = vrijednost;
  }


  @Override
  public void ispisi(IspisniRed red) {
    if (red == null) return;
    String[] v = red.vrijednosti();
    if (v == null) return;
    System.out.println(String.join(" ", v));
  }

  public void ispisiTablicu(String komandaTekst, String nazivTablice, List<? extends IspisniRed> redovi) {
    // 1) komanda
    if (komandaTekst != null && !komandaTekst.isBlank()) {
      System.out.println(komandaTekst.trim());
    }

    // 2) naziv tablice
    if (nazivTablice != null && !nazivTablice.isBlank()) {
      System.out.println(nazivTablice.trim());
    }

    // prazna lista -> prazan red i gotovo
    if (redovi == null || redovi.isEmpty()) {
      System.out.println();
      return;
    }

    // 3) header iz prvog reda koji ga ima
    String[] header = null;
    for (IspisniRed r : redovi) {
      if (r != null && r.zaglavlje() != null && r.zaglavlje().length > 0) {
        header = r.zaglavlje();
        break;
      }
    }

    // Ako nema headera, ispiši vrijednosti kao tekst
    if (header == null) {
      System.out.println();
      for (IspisniRed r : redovi) {
        if (r == null || r.vrijednosti() == null) continue;
        System.out.println(String.join(" ", r.vrijednosti()));
      }
      System.out.println();
      return;
    }

    int cols = header.length;

    // 4) pripremi redove (padaj/puni na cols) + primijeni “ne ispisuj otkazane”
    List<String[]> values = new ArrayList<>();
    for (IspisniRed r : redovi) {
      if (r == null) continue;

      String[] v = r.vrijednosti();
      if (v == null) v = new String[0];

      String[] row = new String[cols];
      for (int i = 0; i < cols; i++) {
        row[i] = i < v.length ? nullToEmpty(v[i]) : "";
      }

      if (!ispisujeOtkazane && row.length > 0) {
        String zadnje = row[row.length - 1];
        if (zadnje != null && zadnje.toUpperCase().contains("OTKAZ")) {
          continue;
        }
      }

      values.add(row);
    }

    // nakon filtriranja – ako je prazno, samo završi tablicu
    if (values.isEmpty()) {
      System.out.println();
      return;
    }

    // 5) detekcija numeričkih kolona (sve ne-prazne ćelije su broj)
    boolean[] numericCol = new boolean[cols];
    for (int c = 0; c < cols; c++) {
      boolean any = false;
      boolean allNumeric = true;
      for (String[] row : values) {
        String cell = row[c];
        if (cell == null || cell.isBlank()) continue;
        any = true;
        if (!isNumeric(cell)) {
          allNumeric = false;
          break;
        }
      }
      numericCol[c] = any && allNumeric;
    }

    // 6) formatiraj + širine
    int[] widths = new int[cols];
    for (int c = 0; c < cols; c++) {
      widths[c] = header[c] == null ? 0 : header[c].length();
    }

    String[][] formatted = new String[values.size()][cols];
    for (int r = 0; r < values.size(); r++) {
      for (int c = 0; c < cols; c++) {
        String cell = values.get(r)[c];

        String out = cell;
        if (numericCol[c]) {
          String nazivStupca = header[c] == null ? "" : header[c].trim();

          if (valutaStupci.contains(nazivStupca)) {
            out = formatNumber(cell, Mode.VALUTA);
          } else if (intStupci.contains(nazivStupca)) {
            out = formatNumber(cell, Mode.INT);
          } else {
            out = formatNumber(cell, Mode.AUTO);
          }
        }

        formatted[r][c] = out;

        if (out != null && out.length() > widths[c]) {
          widths[c] = out.length();
        }
      }
    }

    // 7) širina tablice
    int totalWidth = 1;
    for (int c = 0; c < cols; c++) {
      totalWidth += widths[c] + 2;
      totalWidth += 1;
    }

    // 8) crta, zaglavlje, crta
    System.out.println("-".repeat(totalWidth));
    System.out.println(renderRow(header, widths, null));
    System.out.println("-".repeat(totalWidth));

    // 9) podaci
    for (int r = 0; r < formatted.length; r++) {
      System.out.println(renderRow(formatted[r], widths, numericCol));
    }

    System.out.println("-".repeat(totalWidth));
    System.out.println();
  }

  /**
   * Prepoznaj otkazani red:
   * - ako postoji stupac "Stanje" i sadrži "OTKAZ"
   * - ili ako postoji stupac koji sadrži "otkaza" (datum/vrijeme otkaza) i nije prazan
   */
  private boolean jeOtkazanaRow(String[] header, String[] row) {
    int idxStanje = -1;
    int idxOtkaz = -1;

    for (int i = 0; i < header.length; i++) {
      String h = header[i] == null ? "" : header[i].toLowerCase();
      if (idxStanje == -1 && h.contains("stanje")) idxStanje = i;
      if (idxOtkaz == -1 && h.contains("otkaz")) idxOtkaz = i;
    }

    if (idxStanje >= 0) {
      String s = row[idxStanje] == null ? "" : row[idxStanje].toUpperCase();
      if (s.contains("OTKAZ")) return true;
    }

    if (idxOtkaz >= 0) {
      String t = row[idxOtkaz] == null ? "" : row[idxOtkaz].trim();
      if (!t.isEmpty()) return true;
    }

    // fallback: zadnja kolona
    if (row.length > 0) {
      String last = row[row.length - 1] == null ? "" : row[row.length - 1].toUpperCase();
      if (last.contains("OTKAZ")) return true;
    }

    return false;
  }

  private String renderRow(String[] cells, int[] widths, boolean[] numericCol) {
    StringBuilder sb = new StringBuilder();
    sb.append("|");
    for (int c = 0; c < widths.length; c++) {
      String v = (cells != null && c < cells.length && cells[c] != null) ? cells[c] : "";
      boolean numeric = (numericCol != null && c < numericCol.length && numericCol[c]);

      sb.append(" ");
      if (numeric) {
        sb.append(String.format("%" + widths[c] + "s", v));
      } else {
        sb.append(String.format("%-" + widths[c] + "s", v));
      }
      sb.append(" |");
    }
    return sb.toString();
  }

  private String nullToEmpty(String s) {
    return s == null ? "" : s;
  }

  private boolean isNumeric(String s) {
    if (s == null) return false;
    String t = s.trim();
    if (t.isEmpty()) return false;
    if (t.contains(":")) return false;   // vrijeme
    if (t.endsWith(".")) return false;   // datumi tipa 01.10.2025.
    return t.matches("^-?\\d+(?:[\\.,]\\d+)?$");
  }

  /**
   * Format broja: grupiranje s '.' i decimal ',' + 2 decimale (npr. 12.345,67).
   */
  private String formatNumber(String s, Mode mode) {
    if (s == null || s.isBlank()) return "";

    String t = s.trim();

    BigDecimal bd;
    try {
      if (t.contains(".") && t.contains(",")) {
        t = t.replace(".", "").replace(",", ".");
      } else {
        t = t.replace(",", ".");
      }
      bd = new BigDecimal(t);
    } catch (Exception e) {
      return s;
    }

    DecimalFormatSymbols sym = new DecimalFormatSymbols(Locale.ROOT);
    sym.setGroupingSeparator('.');
    sym.setDecimalSeparator(',');

    String pattern;
    switch (mode) {
      case INT -> pattern = "#,##0";
      case VALUTA -> pattern = "#,##0.00";
      default -> {
        boolean isWhole = bd.stripTrailingZeros().scale() <= 0;
        pattern = isWhole ? "#,##0" : "#,##0.00";
      }
    }

    DecimalFormat df = new DecimalFormat(pattern, sym);
    df.setGroupingUsed(true);

    String out = df.format(bd);
    if (mode == Mode.VALUTA) {
      out += " €";
    }
    return out;
  }

}
