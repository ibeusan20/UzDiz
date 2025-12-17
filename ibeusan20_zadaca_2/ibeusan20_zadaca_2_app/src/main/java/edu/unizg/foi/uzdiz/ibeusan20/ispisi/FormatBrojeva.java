package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Pomoćna klasa za formatiranje brojeva u ispisima.
 * <p>
 * Pravila formatiranja:
 * </p>
 * <ul>
 * <li>tisućice se odvajaju točkom (.)</li>
 * <li>decimalni separator je točka (.)</li>
 * <li>valuta se ispisuje s dvije decimale i sufiksom " €"</li>
 * </ul>
 * <p>
 * Koristi {@link ThreadLocal} formatera kako bi bila sigurna za korištenje iz više dretvi.
 * </p>
 */
public final class FormatBrojeva {

  private static final DecimalFormatSymbols SYM;

  private static final ThreadLocal<DecimalFormat> NOVAC;
  private static final ThreadLocal<DecimalFormat> DEC2;
  private static final ThreadLocal<DecimalFormat> INT;

  static {
    SYM = new DecimalFormatSymbols(Locale.ROOT);
    SYM.setGroupingSeparator('.'); // tisućice s točkom
    SYM.setDecimalSeparator('.'); // decimale s točkom

    NOVAC = ThreadLocal.withInitial(() -> {
      DecimalFormat df = new DecimalFormat("#,##0.00", SYM);
      df.setGroupingUsed(true);
      df.setParseBigDecimal(true);
      return df;
    });

    DEC2 = ThreadLocal.withInitial(() -> {
      DecimalFormat df = new DecimalFormat("#,##0.00", SYM);
      df.setGroupingUsed(true);
      df.setParseBigDecimal(true);
      return df;
    });

    INT = ThreadLocal.withInitial(() -> {
      DecimalFormat df = new DecimalFormat("#,##0", SYM);
      df.setGroupingUsed(true);
      df.setParseBigDecimal(true);
      return df;
    });
  }

  private FormatBrojeva() {}

  public static String eur(Number n) {
    if (n == null)
      return "";
    return NOVAC.get().format(toBigDecimal(n)) + " €";
  }

  public static String dec2(Number n) {
    if (n == null)
      return "";
    return DEC2.get().format(toBigDecimal(n));
  }

  public static String cijeli(Number n) {
    if (n == null)
      return "";
    return INT.get().format(toBigDecimal(n));
  }

  public static String eur(String s) {
    BigDecimal bd = parse(s);
    return bd == null ? nullToEmpty(s) : NOVAC.get().format(bd) + " €";
  }

  public static String dec2(String s) {
    BigDecimal bd = parse(s);
    return bd == null ? nullToEmpty(s) : DEC2.get().format(bd);
  }

  public static String cijeli(String s) {
    BigDecimal bd = parse(s);
    return bd == null ? nullToEmpty(s) : INT.get().format(bd);
  }

  public static String auto(String s) {
    BigDecimal bd = parse(s);
    if (bd == null)
      return nullToEmpty(s);
    boolean whole = bd.stripTrailingZeros().scale() <= 0;
    return whole ? INT.get().format(bd) : DEC2.get().format(bd);
  }

  private static BigDecimal parse(String s) {
    if (s == null)
      return null;
    String t = s.trim();
    if (t.isEmpty())
      return null;
    if (t.contains(":"))
      return null; // vrijeme
    if (t.endsWith("."))
      return null; // datum "01.10.2025."

    try {
      // Podržava ulaz koji možda dođe kao "4100,00" ili "4.100,00"
      // ali izlaz uvijek s točkama.
      if (t.contains(".") && t.contains(",")) {
        // "4.100,00" -> "4100.00"
        t = t.replace(".", "").replace(",", ".");
      } else {
        // "4100,00" -> "4100.00"
        t = t.replace(",", ".");
      }
      return new BigDecimal(t);
    } catch (Exception e) {
      return null;
    }
  }

  private static BigDecimal toBigDecimal(Number n) {
    if (n instanceof BigDecimal bd)
      return bd;
    return new BigDecimal(String.valueOf(n));
  }

  private static String nullToEmpty(String s) {
    return s == null ? "" : s;
  }
}
