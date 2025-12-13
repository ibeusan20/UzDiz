package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TablicniFormat implements FormatIspisaBridge {

  // I dalje implementira Bridge (možeš ga koristiti i za "tekstne" redove),
  // ali za tablice koristi ispisiTablicu().
  @Override
  public void ispisi(IspisniRed red) {
    if (red == null) return;
    String[] v = red.vrijednosti();
    if (v == null) return;
    System.out.println(String.join(" ", v));
  }

  public void ispisiTablicu(String komandaTekst, String nazivTablice, List<? extends IspisniRed> redovi) {
    // 1) komanda + parametri (prvi red)
    if (komandaTekst != null && !komandaTekst.isBlank()) {
      System.out.println(komandaTekst.trim());
    }

    // 2) naziv tablice (drugi red)
    if (nazivTablice != null && !nazivTablice.isBlank()) {
      System.out.println(nazivTablice.trim());
    }

    // Ako nema redova – svejedno smo ispisali komandu + naziv tablice
    if (redovi == null || redovi.isEmpty()) {
      System.out.println();
      return;
    }

    // 3) uzmi zaglavlje iz prvog reda koji ga ima
    String[] header = null;
    for (IspisniRed r : redovi) {
      if (r != null && r.zaglavlje() != null && r.zaglavlje().length > 0) {
        header = r.zaglavlje();
        break;
      }
    }
    if (header == null) {
      // nema zaglavlja -> ispiši samo vrijednosti
      System.out.println();
      for (IspisniRed r : redovi) {
        if (r == null || r.vrijednosti() == null) continue;
        System.out.println(String.join(" ", r.vrijednosti()));
      }
      System.out.println();
      return;
    }

    int cols = header.length;

    // 4) izgradi matricu vrijednosti (padaj/puni da uvijek ima cols)
    List<String[]> values = new ArrayList<>();
    for (IspisniRed r : redovi) {
      if (r == null) continue;
      String[] v = r.vrijednosti();
      if (v == null) v = new String[0];

      String[] row = new String[cols];
      for (int i = 0; i < cols; i++) {
        row[i] = i < v.length ? nullToEmpty(v[i]) : "";
      }
      values.add(row);
    }

    // 5) odredi koje kolone su numeričke (sve ne-prazne ćelije su broj)
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

    // 6) formatiraj vrijednosti (brojevi: 1.234.567,89) i izračunaj širine
    int[] widths = new int[cols];
    for (int c = 0; c < cols; c++) {
      widths[c] = header[c] == null ? 0 : header[c].length();
    }

    String[][] formatted = new String[values.size()][cols];
    for (int r = 0; r < values.size(); r++) {
      for (int c = 0; c < cols; c++) {
        String cell = values.get(r)[c];
        String out = numericCol[c] ? formatNumber(cell) : cell;
        formatted[r][c] = out;

        if (out != null && out.length() > widths[c]) {
          widths[c] = out.length();
        }
      }
    }

    // 7) širina cijele tablice (za crte)
    int totalWidth = 1; // početni |
    for (int c = 0; c < cols; c++) {
      totalWidth += widths[c] + 2; // razmak + sadržaj + razmak
      totalWidth += 1; // |
    }

    // 8) crta / prazan red (po specifikaciji: može prazan ili crta) -> mi radimo crtu
    System.out.println("-".repeat(totalWidth));

    // 9) zaglavlje + crta
    System.out.println(renderRow(header, widths, /*numeric*/ null));
    System.out.println("-".repeat(totalWidth));

    // 10) podaci
    for (int r = 0; r < formatted.length; r++) {
      System.out.println(renderRow(formatted[r], widths, numericCol));
    }

    // završna crta (nije striktno nužna, ali tablicu čini “završenom”)
    System.out.println("-".repeat(totalWidth));
    System.out.println();
  }

  private String renderRow(String[] cells, int[] widths, boolean[] numericCol) {
    StringBuilder sb = new StringBuilder();
    sb.append("|");
    for (int c = 0; c < widths.length; c++) {
      String v = (cells != null && c < cells.length && cells[c] != null) ? cells[c] : "";
      boolean numeric = (numericCol != null && c < numericCol.length && numericCol[c]);

      sb.append(" ");
      if (numeric) {
        sb.append(String.format("%" + widths[c] + "s", v)); // desno
      } else {
        sb.append(String.format("%-" + widths[c] + "s", v)); // lijevo
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
    if (t.contains(":")) return false;       // vrijeme
    if (t.endsWith(".")) return false;       // datumi tipa 01.10.2025.
    // dopusti: 123, 123.45, 123,45, -123, -123,45 ...
    return t.matches("^-?\\d+(?:[\\.,]\\d+)?$");
  }

  private String formatNumber(String s) {
    if (s == null || s.isBlank()) return "";

    // normalizacija: dopusti ulaz s '.' ili ',' kao decimalni separator
    String t = s.trim();

    BigDecimal bd;
    try {
      // Ako ima i '.' i ',' -> pretpostavi '.' grupiranje, ',' decimal
      if (t.contains(".") && t.contains(",")) {
        t = t.replace(".", "").replace(",", ".");
      } else {
        // inače zamijeni ',' u '.' radi parsiranja
        t = t.replace(",", ".");
      }
      bd = new BigDecimal(t);
    } catch (Exception e) {
      return s; // fallback
    }

    // format: grupiranje '.' i decimal ',' (HR stil), 2 decimale kad ima decimalu
    DecimalFormatSymbols sym = new DecimalFormatSymbols(Locale.ROOT);
    sym.setGroupingSeparator('.');
    sym.setDecimalSeparator(',');

    boolean hasDecimal = bd.scale() > 0 && bd.stripTrailingZeros().scale() > 0;
    DecimalFormat df = new DecimalFormat(hasDecimal ? "#,##0.00" : "#,##0", sym);
    df.setGroupingUsed(true);
    return df.format(bd);
  }
}
