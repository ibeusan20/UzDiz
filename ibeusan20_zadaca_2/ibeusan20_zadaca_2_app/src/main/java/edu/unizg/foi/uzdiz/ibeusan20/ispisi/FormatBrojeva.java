package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

// TODO: Auto-generated Javadoc
/**
 * The Class FormatBrojeva.
 */
public final class FormatBrojeva {

  /** The Constant SYM. */
  private static final DecimalFormatSymbols SYM;

  /** The Constant NOVAC. */
  private static final ThreadLocal<DecimalFormat> NOVAC;
  
  /** The Constant DEC2. */
  private static final ThreadLocal<DecimalFormat> DEC2;
  
  /** The Constant INT. */
  private static final ThreadLocal<DecimalFormat> INT;

  static {
    SYM = new DecimalFormatSymbols(Locale.ROOT);
    SYM.setGroupingSeparator('.'); // tisućice s točkom
    SYM.setDecimalSeparator('.');  // decimale s točkom (bez zareza)

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

  /**
   * Instantiates a new format brojeva.
   */
  private FormatBrojeva() {}

  /**
   * Eur.
   *
   * @param n the n
   * @return the string
   */
  public static String eur(Number n) {
    if (n == null) return "";
    return NOVAC.get().format(toBigDecimal(n)) + " €";
  }

  /**
   * Dec 2.
   *
   * @param n the n
   * @return the string
   */
  public static String dec2(Number n) {
    if (n == null) return "";
    return DEC2.get().format(toBigDecimal(n));
  }

  /**
   * Cijeli.
   *
   * @param n the n
   * @return the string
   */
  public static String cijeli(Number n) {
    if (n == null) return "";
    return INT.get().format(toBigDecimal(n));
  }

  /**
   * Eur.
   *
   * @param s the s
   * @return the string
   */
  public static String eur(String s) {
    BigDecimal bd = parse(s);
    return bd == null ? nullToEmpty(s) : NOVAC.get().format(bd) + " €";
  }

  /**
   * Dec 2.
   *
   * @param s the s
   * @return the string
   */
  public static String dec2(String s) {
    BigDecimal bd = parse(s);
    return bd == null ? nullToEmpty(s) : DEC2.get().format(bd);
  }

  /**
   * Cijeli.
   *
   * @param s the s
   * @return the string
   */
  public static String cijeli(String s) {
    BigDecimal bd = parse(s);
    return bd == null ? nullToEmpty(s) : INT.get().format(bd);
  }

  /**
   * Auto.
   *
   * @param s the s
   * @return the string
   */
  public static String auto(String s) {
    BigDecimal bd = parse(s);
    if (bd == null) return nullToEmpty(s);
    boolean whole = bd.stripTrailingZeros().scale() <= 0;
    return whole ? INT.get().format(bd) : DEC2.get().format(bd);
  }

  /**
   * Parses the.
   *
   * @param s the s
   * @return the big decimal
   */
  private static BigDecimal parse(String s) {
    if (s == null) return null;
    String t = s.trim();
    if (t.isEmpty()) return null;
    if (t.contains(":")) return null; // vrijeme
    if (t.endsWith(".")) return null; // datum "01.10.2025."

    try {
      // Podrži ulaz koji možda dođe kao "4100,00" ili "4.100,00"
      // ali izlaz uvijek formatiramo s točkama.
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

  /**
   * To big decimal.
   *
   * @param n the n
   * @return the big decimal
   */
  private static BigDecimal toBigDecimal(Number n) {
    if (n instanceof BigDecimal bd) return bd;
    return new BigDecimal(String.valueOf(n));
  }

  /**
   * Null to empty.
   *
   * @param s the s
   * @return the string
   */
  private static String nullToEmpty(String s) {
    return s == null ? "" : s;
  }
}
