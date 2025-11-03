package ispisi;

import model.Aranzman;
import model.FormatDatuma;

/**
 * Adapter između modela Aranzman i ispisa u tablici.
 */
public class IspisAranzmanaAdapter {

  private final Aranzman a;

  public IspisAranzmanaAdapter(Aranzman a) {
    this.a = a;
  }

  public String getOznaka() {
    return a.getOznaka();
  }

  public String getNaziv() {
    return a.getNaziv();
  }

  public String getDatumOd() {
    return FormatDatuma.formatiraj(a.getPocetniDatum());
  }

  public String getDatumDo() {
    return FormatDatuma.formatiraj(a.getZavrsniDatum());
  }

  public String getVrijemeKretanja() {
    return FormatDatuma.formatiraj(a.getVrijemeKretanja());
  }

  public String getVrijemePovratka() {
    return FormatDatuma.formatiraj(a.getVrijemePovratka());
  }

  public String getCijena() {
    return String.format("%.2f €", a.getCijena());
  }

  public int getMinPutnika() {
    return a.getMinPutnika();
  }

  public int getMaxPutnika() {
    return a.getMaxPutnika();
  }
}
