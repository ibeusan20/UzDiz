package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import java.time.format.DateTimeFormatter;
import edu.unizg.foi.uzdiz.ibeusan20.audit.AuditStavka;

// TODO: Auto-generated Javadoc
/**
 * The Class IspisAuditAdapter.
 */
public class IspisAuditAdapter implements IspisniRed {

  /** The Constant F. */
  private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss");

  /** The s. */
  private final AuditStavka s;

  /**
   * Instantiates a new ispis audit adapter.
   *
   * @param s the s
   */
  public IspisAuditAdapter(AuditStavka s) {
    this.s = s;
  }

  /**
   * Zaglavlje.
   *
   * @return the string[]
   */
  @Override
  public String[] zaglavlje() {
    return new String[] {"Rbr", "Vrijeme", "Komanda", "Status", "Trajanje(ms)"};
  }

  /**
   * Vrijednosti.
   *
   * @return the string[]
   */
  @Override
  public String[] vrijednosti() {
    if (s == null) return new String[] {"", "", "", "", ""};

    String vrijeme = (s.vrijeme() == null) ? "" : s.vrijeme().format(F);
    String unos = skrati(s.unos(), 80);

    return new String[] {
        String.valueOf(s.rbr()),
        vrijeme,
        unos,
        s.status() == null ? "" : s.status(),
        String.valueOf(s.trajanjeMs())
    };
  }

  /**
   * Skrati.
   *
   * @param t the t
   * @param max the max
   * @return the string
   */
  private String skrati(String t, int max) {
    if (t == null) return "";
    String x = t.trim();
    if (x.length() <= max) return x;
    return x.substring(0, Math.max(0, max - 3)) + "...";
  }
}
