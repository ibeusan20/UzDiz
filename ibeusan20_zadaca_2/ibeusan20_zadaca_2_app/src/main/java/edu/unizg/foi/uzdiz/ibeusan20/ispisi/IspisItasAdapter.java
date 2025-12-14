package edu.unizg.foi.uzdiz.ibeusan20.ispisi;

/**
 * Red statistike za ITAS.
 */
public class IspisItasAdapter implements IspisniRed {

  private final String oznaka;
  private final int ukupno;
  private final int aktivne;
  private final int cekanje;
  private final int odgodene;
  private final int otkazane;
  private final double prihod;

  public IspisItasAdapter(String oznaka, int ukupno, int aktivne, int cekanje,
      int odgodene, int otkazane, double prihod) {
    this.oznaka = oznaka;
    this.ukupno = ukupno;
    this.aktivne = aktivne;
    this.cekanje = cekanje;
    this.odgodene = odgodene;
    this.otkazane = otkazane;
    this.prihod = prihod;
  }

  @Override
  public String[] zaglavlje() {
    return new String[] {"Oznaka", "Ukupno", "Aktivne", "Čekanje", "Odgođene", "Otkazane", "Prihod"};
  }

  @Override
  public String[] vrijednosti() {
    return new String[] {
        oznaka,
        String.valueOf(ukupno),
        String.valueOf(aktivne),
        String.valueOf(cekanje),
        String.valueOf(odgodene),
        String.valueOf(otkazane),
        FormatBrojeva.eur(prihod)
    };
  }
}
