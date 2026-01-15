package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TablicniFormat implements FormatIspisaBridge {

  private boolean ispisujeOtkazane = true;

  // Stupci koji se uvijek ispisuju kao cijeli brojevi
  private final Set<String> intStupci = new HashSet<>();

  // Stupci koji se ispisuju kao valuta (2 decimale + €)
  private final Set<String> valutaStupci = new HashSet<>();

  public TablicniFormat() {
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
    valutaStupci.add("Doplata jednokrevetna");
    valutaStupci.add("Ukupan prihod");
    valutaStupci.add("Prihod"); // ITAS
  }

  public void setIspisujeOtkazane(boolean vrijednost) {
    this.ispisujeOtkazane = vrijednost;
  }

  @Override
  public void ispisi(String tekst) {
    if (tekst == null)
      return;
    System.out.println(tekst);
  }

  public void ispisiTablicu(String komandaTekst, String nazivTablice,
      List<? extends IspisniRed> redovi) {

    if (komandaTekst != null && !komandaTekst.isBlank()) {
      System.out.println(komandaTekst.trim());
    }
    if (nazivTablice != null && !nazivTablice.isBlank()) {
      System.out.println(nazivTablice.trim());
    }

    if (redovi == null || redovi.isEmpty()) {
      System.out.println();
      return;
    }

    // header iz prvog reda koji ga ima
    String[] header = null;
    for (IspisniRed r : redovi) {
      if (r != null && r.zaglavlje() != null && r.zaglavlje().length > 0) {
        header = r.zaglavlje();
        break;
      }
    }

    if (header == null) {
      System.out.println();
      for (IspisniRed r : redovi) {
        if (r == null || r.vrijednosti() == null)
          continue;
        System.out.println(String.join(" ", r.vrijednosti()));
      }
      System.out.println();
      return;
    }

    int cols = header.length;

    List<String[]> values = new ArrayList<>();
    for (IspisniRed r : redovi) {
      if (r == null)
        continue;

      String[] v = r.vrijednosti();
      if (v == null)
        v = new String[0];

      String[] row = new String[cols];
      for (int i = 0; i < cols; i++) {
        row[i] = i < v.length ? nullToEmpty(v[i]) : "";
      }

      if (!ispisujeOtkazane && jeOtkazanaRow(header, row)) {
        continue;
      }

      values.add(row);
    }

    if (values.isEmpty()) {
      System.out.println();
      return;
    }

    // detekcija numeričkih kolona za poravnanje desno
    boolean[] numericCol = new boolean[cols];
    for (int c = 0; c < cols; c++) {
      boolean any = false;
      boolean allNumeric = true;
      for (String[] row : values) {
        String cell = row[c];
        if (cell == null || cell.isBlank())
          continue;
        any = true;
        if (!isNumeric(cell)) {
          allNumeric = false;
          break;
        }
      }
      numericCol[c] = any && allNumeric;
    }

    // formatiranje + širine
    int[] widths = new int[cols];
    for (int c = 0; c < cols; c++) {
      widths[c] = header[c] == null ? 0 : header[c].length();
    }

    String[][] formatted = new String[values.size()][cols];
    for (int r = 0; r < values.size(); r++) {
      for (int c = 0; c < cols; c++) {
        String cell = values.get(r)[c];
        String nazivStupca = header[c] == null ? "" : header[c].trim();

        String out = cell;

        if (numericCol[c]) {
          if (valutaStupci.contains(nazivStupca)) {
            out = FormatBrojeva.eur(cell);
          } else if (intStupci.contains(nazivStupca)) {
            out = FormatBrojeva.cijeli(cell);
          } else {
            out = FormatBrojeva.auto(cell);
          }
        }

        formatted[r][c] = out;
        if (out != null && out.length() > widths[c]) {
          widths[c] = out.length();
        }
      }
    }

    // širina tablice
    int totalWidth = 1;
    for (int c = 0; c < cols; c++) {
      totalWidth += widths[c] + 2;
      totalWidth += 1;
    }

    System.out.println("-".repeat(totalWidth));
    System.out.println(renderRow(header, widths, null));
    System.out.println("-".repeat(totalWidth));

    for (int r = 0; r < formatted.length; r++) {
      System.out.println(renderRow(formatted[r], widths, numericCol));
    }

    System.out.println("-".repeat(totalWidth));
    System.out.println();
  }

  private boolean jeOtkazanaRow(String[] header, String[] row) {
    int idxStanje = -1;
    int idxOtkaz = -1;

    for (int i = 0; i < header.length; i++) {
      String h = header[i] == null ? "" : header[i].toLowerCase();
      if (idxStanje == -1 && h.contains("stanje"))
        idxStanje = i;
      if (idxOtkaz == -1 && h.contains("otkaz"))
        idxOtkaz = i;
    }

    if (idxStanje >= 0) {
      String s = row[idxStanje] == null ? "" : row[idxStanje].toUpperCase();
      if (s.contains("OTKAZ"))
        return true;
    }

    if (idxOtkaz >= 0) {
      String t = row[idxOtkaz] == null ? "" : row[idxOtkaz].trim();
      if (!t.isEmpty())
        return true;
    }

    if (row.length > 0) {
      String last = row[row.length - 1] == null ? "" : row[row.length - 1].toUpperCase();
      if (last.contains("OTKAZ"))
        return true;
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
      if (numeric)
        sb.append(String.format("%" + widths[c] + "s", v));
      else
        sb.append(String.format("%-" + widths[c] + "s", v));
      sb.append(" |");
    }
    return sb.toString();
  }

  private String nullToEmpty(String s) {
    return s == null ? "" : s;
  }

  private boolean isNumeric(String s) {
    if (s == null)
      return false;
    String t = s.trim();
    if (t.isEmpty())
      return false;
    if (t.contains(":"))
      return false; // vrijeme
    if (t.endsWith("."))
      return false; // datumi tipa 01.10.2025.
    return t.matches("^-?\\d+(?:[\\.,]\\d+)?$");
  }
}
