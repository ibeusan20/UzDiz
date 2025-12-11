package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

import edu.unizg.foi.uzdiz.ibeusan20.model.Aranzman;
import edu.unizg.foi.uzdiz.ibeusan20.model.FormatDatuma;

/**
 * Adapter između klase {@link Aranzman} i formata ispisa.
 * <p>
 * Pretvara podatke o aranžmanu u tekstualni oblik pogodan za ispis u tabličnom formatu.
 * </p>
 */
public class IspisAranzmanaAdapter {
  private final Aranzman a;

  /**
   * @param a objekt aranžmana koji se prilagođava za ispis
   */
  public IspisAranzmanaAdapter(Aranzman a) {
    this.a = a;
  }

  /** @return oznaka aranžmana */
  public String getOznaka() {
    return a.getOznaka();
  }

  /** @return naziv aranžmana */
  public String getNaziv() {
    return a.getNaziv();
  }

  /** @return formatirani početni datum */
  public String getDatumOd() {
    return FormatDatuma.formatiraj(a.getPocetniDatum());
  }

  /** @return formatirani završni datum */
  public String getDatumDo() {
    return FormatDatuma.formatiraj(a.getZavrsniDatum());
  }

  /** @return vrijeme polaska */
  public String getVrijemeKretanja() {
    return FormatDatuma.formatiraj(a.getVrijemeKretanja());
  }

  /** @return vrijeme povratka */
  public String getVrijemePovratka() {
    return FormatDatuma.formatiraj(a.getVrijemePovratka());
  }

  /** @return cijena aranžmana s oznakom valute */
  public String getCijena() {
    return String.format("%.2f €", a.getCijena());
  }

  /** @return minimalan broj putnika */
  public int getMinPutnika() {
    return a.getMinPutnika();
  }

  /** @return maksimalan broj putnika */
  public int getMaxPutnika() {
    return a.getMaxPutnika();
  }
}
